<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

// 响应式数据
const contacts = ref([]);
const messages = ref([]);
const currentContact = ref(null);
const messageInput = ref('');
const loading = ref(false);
const chatContainer = ref(null);

// 当前用户信息（从 localStorage 或 Vuex/Pinia 获取）
const currentUser = ref({
  id: localStorage.getItem('userId'),
  name: localStorage.getItem('username'),
  avatar: localStorage.getItem('userAvatar')
});

// 获取联系人列表
const fetchContacts = async () => {
  try {
    loading.value = true;
    const response = await axios.get('/api/employer/contacts');
    contacts.value = response.data;
  } catch (error) {
    ElMessage.error('获取联系人列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取与指定联系人的聊天记录
const fetchMessages = async (contactId) => {
  try {
    loading.value = true;
    const response = await axios.get(`/api/employer/messages/${contactId}`);
    messages.value = response.data;
    await nextTick();
    scrollToBottom();
  } catch (error) {
    ElMessage.error('获取聊天记录失败');
  } finally {
    loading.value = false;
  }
};

// 选择联系人
const selectContact = async (contact) => {
  currentContact.value = contact;
  await fetchMessages(contact.id);
  // 更新未读消息数
  contact.unreadCount = 0;
};

// 发送消息
const sendMessage = async () => {
  if (!messageInput.value.trim() || !currentContact.value) return;
  
  try {
    const message = {
      contactId: currentContact.value.id,
      content: messageInput.value,
      type: 'TEXT',
      sendTime: new Date().toISOString()
    };
    
    await axios.post('/api/employer/messages', message);
    messages.value.push(message);
    messageInput.value = '';
    await nextTick();
    scrollToBottom();
    updateLastMessage(currentContact.value.id, message);
  } catch (error) {
    ElMessage.error('发送消息失败');
  }
};

// 处理文件上传
const handleFileUpload = async (file) => {
  if (!currentContact.value) return false;
  
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!');
    return false;
  }
  
  try {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('contactId', currentContact.value.id);
    
    const response = await axios.post('/api/employer/messages/file', formData);
    messages.value.push(response.data);
    await nextTick();
    scrollToBottom();
    updateLastMessage(currentContact.value.id, response.data);
  } catch (error) {
    ElMessage.error('文件发送失败');
  }
  
  return false;
};

// 更新联系人最后一条消息
const updateLastMessage = (contactId, message) => {
  const contact = contacts.value.find(c => c.id === contactId);
  if (contact) {
    contact.lastMessage = message.content;
    contact.lastMessageTime = message.sendTime;
  }
};

// 滚动到底部
const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
  }
};

// 格式化时间
const formatTime = (time) => {
  return new Date(time).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 模拟接收新消息
const simulateNewMessage = () => {
  const randomContact = contacts.value[Math.floor(Math.random() * contacts.value.length)];
  if (randomContact) {
    if (currentContact.value?.id === randomContact.id) {
      messages.value.push({
        contactId: randomContact.id,
        content: '这是一条模拟的新消息',
        type: 'TEXT',
        sendTime: new Date().toISOString(),
        isReceived: true
      });
      scrollToBottom();
    } else {
      randomContact.unreadCount = (randomContact.unreadCount || 0) + 1;
    }
    updateLastMessage(randomContact.id, {
      content: '这是一条模拟的新消息',
      sendTime: new Date().toISOString()
    });
  }
};

onMounted(() => {
  fetchContacts();
  // 模拟定期接收新消息
  setInterval(simulateNewMessage, 30000);
});
</script>

<template>
  <div class="chat-container">
    <el-card shadow="never" class="chat-card">
      <!-- 联系人列表 -->
      <div class="contacts-panel">
        <div class="contacts-header">
          <h3>联系人列表</h3>
        </div>
        <div class="contacts-list">
          <div
            v-for="contact in contacts"
            :key="contact.id"
            class="contact-item"
            :class="{ active: currentContact?.id === contact.id }"
            @click="selectContact(contact)"
          >
            <el-badge :value="contact.unreadCount" :hidden="!contact.unreadCount">
              <el-avatar :size="40" :src="contact.avatar">
                {{ contact.name.charAt(0) }}
              </el-avatar>
            </el-badge>
            <div class="contact-info">
              <div class="contact-name">
                {{ contact.name }}
                <el-tag
                  size="small"
                  :type="contact.online ? 'success' : 'info'"
                  class="online-status"
                >
                  {{ contact.online ? '在线' : '离线' }}
                </el-tag>
              </div>
              <div class="last-message" v-if="contact.lastMessage">
                {{ contact.lastMessage }}
              </div>
            </div>
            <div class="contact-time" v-if="contact.lastMessageTime">
              {{ formatTime(contact.lastMessageTime) }}
            </div>
          </div>
        </div>
      </div>

      <!-- 聊天区域 -->
      <div class="chat-panel">
        <template v-if="currentContact">
          <div class="chat-header">
            <h3>{{ currentContact.name }}</h3>
          </div>
          
          <div class="chat-messages" ref="chatContainer">
            <div
              v-for="(message, index) in messages"
              :key="index"
              class="message-item"
              :class="{ 'message-received': message.isReceived }"
            >
              <div class="message-content">
                <template v-if="message.type === 'FILE'">
                  <div class="file-message">
                    <el-icon><Document /></el-icon>
                    <div class="file-info">
                      <div class="file-name">{{ message.fileName }}</div>
                      <div class="file-size">{{ formatFileSize(message.fileSize) }}</div>
                    </div>
                    <el-button link type="primary" @click="window.open(message.fileUrl)">
                      下载
                    </el-button>
                  </div>
                </template>
                <template v-else>
                  {{ message.content }}
                </template>
                <div class="message-time">{{ formatTime(message.sendTime) }}</div>
              </div>
            </div>
          </div>

          <div class="chat-input">
            <el-input
              v-model="messageInput"
              type="textarea"
              :rows="3"
              placeholder="输入消息..."
              @keyup.enter.exact="sendMessage"
            />
            <div class="input-actions">
              <el-upload
                action=""
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleFileUpload"
              >
                <el-button>
                  <el-icon><Upload /></el-icon>
                  发送文件
                </el-button>
              </el-upload>
              <el-button type="primary" @click="sendMessage">
                发送
              </el-button>
            </div>
          </div>
        </template>
        
        <div v-else class="no-chat">
          <el-empty description="选择一个联系人开始聊天" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.chat-container {
  padding: 20px;
  height: calc(100vh - 120px);
}

.chat-card {
  height: 100%;
  display: flex;
}

:deep(.el-card__body) {
  height: 100%;
  padding: 0;
  display: flex;
}

.contacts-panel {
  width: 300px;
  border-right: 1px solid #dcdfe6;
  display: flex;
  flex-direction: column;
}

.contacts-header {
  padding: 20px;
  border-bottom: 1px solid #dcdfe6;
}

.contacts-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.contacts-list {
  flex: 1;
  overflow-y: auto;
}

.contact-item {
  padding: 15px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.3s;
}

.contact-item:hover {
  background-color: #f5f7fa;
}

.contact-item.active {
  background-color: #ecf5ff;
}

.contact-info {
  margin-left: 12px;
  flex: 1;
  min-width: 0;
}

.contact-name {
  font-size: 14px;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
}

.online-status {
  margin-left: 8px;
}

.last-message {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.contact-time {
  font-size: 12px;
  color: #909399;
}

.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid #dcdfe6;
}

.chat-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.message-item {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.message-item.message-received {
  justify-content: flex-start;
}

.message-content {
  max-width: 70%;
  background-color: #409eff;
  color: white;
  padding: 10px 15px;
  border-radius: 4px;
  position: relative;
}

.message-received .message-content {
  background-color: #f4f4f5;
  color: #303133;
}

.message-time {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 4px;
  text-align: right;
}

.message-received .message-time {
  color: #909399;
}

.file-message {
  display: flex;
  align-items: center;
  gap: 10px;
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  font-size: 12px;
  color: #c0c4cc;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #dcdfe6;
}

.input-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.no-chat {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style> 