<template>
  <div class="messages-container">
    <h2 class="page-title">我的消息</h2>

    <el-tabs v-model="activeTab" class="message-tabs">
      <el-tab-pane label="消息列表" name="list">
        <div class="message-list-container">
          <el-card v-if="loading" class="loading-card">
            <el-skeleton :rows="6" animated />
          </el-card>

          <template v-else>
            <el-empty v-if="conversations.length === 0" description="暂无消息" />

            <el-card
              v-for="conversation in conversations"
              :key="conversation.contactId"
              class="conversation-card"
              :class="{ 'has-unread': conversation.unreadCount > 0 }"
              @click="selectConversation(conversation)"
            >
              <div class="conversation-header">
                <h3>{{ conversation.contactName }}</h3>
                <span class="badge" v-if="conversation.unreadCount > 0">{{ conversation.unreadCount }}</span>
              </div>
              <div class="conversation-preview">
                <p>{{ conversation.lastMessage.content }}</p>
                <span class="conversation-time">{{ formatTime(conversation.lastMessage.sendTime) }}</span>
              </div>
              <div class="conversation-job" v-if="conversation.jobTitle">
                <span>岗位: {{ conversation.jobTitle }}</span>
              </div>
            </el-card>
          </template>
        </div>
      </el-tab-pane>

      <el-tab-pane label="聊天对话" name="chat" v-if="selectedConversation">
        <div class="chat-container">
          <MessageChat
            :current-user-id="currentUser.userId"
            :target-user-id="selectedConversation.contactId"
            :current-user-type="'USER'"
            :chat-title="selectedConversation.contactName"
            :job-post-id="selectedConversation.jobPostId"
            @message-sent="handleMessageSent"
            @messages-loaded="handleMessagesLoaded"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import MessageChat from '../common/MessageChat.vue';

const activeTab = ref('list');
const conversations = ref([]);
const loading = ref(true);
const currentUser = ref(null);
const selectedConversation = ref(null);

// 获取当前用户信息
const getCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  if (!userStr) {
    ElMessage.error('请先登录');
    return null;
  }
  return JSON.parse(userStr);
};

// 加载消息对话列表
const loadConversations = async () => {
  if (!currentUser.value) return;
  
  try {
    loading.value = true;
    
    // 获取收到的消息
    const response = await axios.get(`http://localhost:8080/api/messages/received/${currentUser.value.userId}`, {
      params: {
        page: 0,
        size: 100
      }
    });
    
    // 整理成对话列表
    const messagesMap = new Map();
    
    response.data.content.forEach(message => {
      const contactId = message.senderId;
      
      if (!messagesMap.has(contactId)) {
        messagesMap.set(contactId, {
          contactId,
          contactName: '单位', // 实际应用中应该获取真实姓名
          jobPostId: message.jobPostId,
          jobTitle: null, // 需要另外获取
          unreadCount: 0,
          lastMessage: null,
          messages: []
        });
      }
      
      const conversation = messagesMap.get(contactId);
      conversation.messages.push(message);
      
      // 更新最新消息
      if (!conversation.lastMessage || new Date(message.sendTime) > new Date(conversation.lastMessage.sendTime)) {
        conversation.lastMessage = message;
      }
      
      // 更新未读消息数量
      if (!message.readStatus) {
        conversation.unreadCount++;
      }
    });
    
    // 转换为数组并按最新消息时间排序
    const conversationsArray = Array.from(messagesMap.values());
    conversationsArray.sort((a, b) => 
      new Date(b.lastMessage?.sendTime || 0) - new Date(a.lastMessage?.sendTime || 0)
    );
    
    // 获取岗位信息
    for (const conversation of conversationsArray) {
      if (conversation.jobPostId) {
        try {
          const jobResponse = await axios.get(`http://localhost:8080/api/jobs/${conversation.jobPostId}`);
          conversation.jobTitle = jobResponse.data.jobTitle;
        } catch (error) {
          console.error('获取岗位信息失败:', error);
        }
      }
    }
    
    conversations.value = conversationsArray;
  } catch (error) {
    console.error('加载消息列表失败:', error);
    ElMessage.error('加载消息列表失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 选择对话
const selectConversation = (conversation) => {
  selectedConversation.value = conversation;
  activeTab.value = 'chat';
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  
  const date = new Date(timeStr);
  const now = new Date();
  const diff = now - date;
  
  // 今天的消息只显示时间
  if (diff < 24 * 60 * 60 * 1000 && 
      date.getDate() === now.getDate() &&
      date.getMonth() === now.getMonth() &&
      date.getFullYear() === now.getFullYear()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
  }
  
  // 一周内的显示星期几
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    return weekdays[date.getDay()];
  }
  
  // 其他情况显示日期
  return date.toLocaleDateString('zh-CN');
};

// 处理消息发送成功事件
const handleMessageSent = (message) => {
  // 更新对话列表中的最新消息
  if (selectedConversation.value) {
    selectedConversation.value.lastMessage = message;
  }
  
  // 重新加载对话列表（可选）
  loadConversations();
};

// 处理消息加载完成事件
const handleMessagesLoaded = (messages) => {
  // 可以在这里处理消息加载完成后的逻辑
};

// 初始化
onMounted(() => {
  currentUser.value = getCurrentUser();
  if (currentUser.value) {
    loadConversations();
    
    // 设置定时刷新
    const refreshInterval = setInterval(() => {
      loadConversations();
    }, 60000); // 每分钟刷新一次
    
    // 组件卸载时清除定时器
    return () => {
      clearInterval(refreshInterval);
    };
  }
});
</script>

<style scoped>
.messages-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.message-tabs {
  margin-top: 20px;
}

.message-list-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.loading-card {
  padding: 20px;
}

.conversation-card {
  cursor: pointer;
  transition: all 0.3s;
}

.conversation-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.has-unread {
  border-left: 4px solid #409EFF;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.conversation-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.badge {
  background-color: #409EFF;
  color: white;
  border-radius: 50%;
  padding: 2px 8px;
  font-size: 12px;
}

.conversation-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.conversation-preview p {
  margin: 0;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 70%;
}

.conversation-time {
  font-size: 12px;
  color: #909399;
}

.conversation-job {
  margin-top: 8px;
  font-size: 12px;
  color: #67c23a;
}

.chat-container {
  height: 600px;
}
</style> 