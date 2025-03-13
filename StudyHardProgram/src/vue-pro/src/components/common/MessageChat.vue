<template>
    <div class="message-chat">
        <div class="chat-header">
            <h3>{{ chatTitle }}</h3>
        </div>
        
        <div class="chat-body" ref="chatBodyRef">
            <div v-if="loading" class="loading-container">
                <el-skeleton :rows="6" animated />
            </div>
            
            <template v-else>
                <div v-if="messages.length === 0" class="empty-messages">
                    <el-empty description="暂无消息" />
                </div>
                
                <div v-else class="message-list">
                    <div 
                        v-for="(message, index) in messages" 
                        :key="message.messageId" 
                        :class="['message-item', message.senderId === currentUserId ? 'message-mine' : 'message-other']"
                    >
                        <div class="message-content">
                            <div class="message-info">
                                <span class="message-sender">{{ getSenderName(message) }}</span>
                                <span class="message-time">{{ formatTime(message.sendTime) }}</span>
                            </div>
                            <div class="message-bubble">{{ message.content }}</div>
                        </div>
                    </div>
                </div>
            </template>
        </div>
        
        <div class="chat-footer">
            <el-input
                v-model="newMessage"
                type="textarea"
                :rows="3"
                placeholder="请输入消息..."
                @keydown.enter.exact.prevent="sendMessage"
            />
            <el-button 
                type="primary" 
                :disabled="!newMessage.trim()" 
                @click="sendMessage"
            >
                发送
            </el-button>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';

const props = defineProps({
    // 当前用户ID
    currentUserId: {
        type: String,
        required: true
    },
    // 对话用户ID
    targetUserId: {
        type: String,
        required: true
    },
    // 当前用户类型 (USER/EMPLOYER)
    currentUserType: {
        type: String,
        required: true
    },
    // 对话标题
    chatTitle: {
        type: String,
        default: '消息对话'
    },
    // 岗位ID (可选)
    jobPostId: {
        type: String,
        default: null
    },
    // 申请ID (可选)
    applicationId: {
        type: String,
        default: null
    }
});

const emit = defineEmits(['message-sent', 'messages-loaded']);

const messages = ref([]);
const newMessage = ref('');
const loading = ref(true);
const chatBodyRef = ref(null);
const page = ref(0);
const size = ref(20);
const hasMore = ref(true);

// 发送消息
const sendMessage = async () => {
    if (!newMessage.value.trim()) return;
    
    try {
        const messageData = {
            senderId: props.currentUserId,
            receiverId: props.targetUserId,
            senderType: props.currentUserType,
            content: newMessage.value,
            jobPostId: props.jobPostId,
            applicationId: props.applicationId
        };
        
        const response = await axios.post('http://localhost:8080/api/messages', messageData);
        
        // 添加到消息列表
        messages.value.unshift(response.data);
        emit('message-sent', response.data);
        
        // 清空输入框并滚动到底部
        newMessage.value = '';
        scrollToBottom();
    } catch (error) {
        console.error('发送消息失败:', error);
        ElMessage.error('发送消息失败，请重试');
    }
};

// 加载消息
const loadMessages = async (reset = false) => {
    if (reset) {
        page.value = 0;
        hasMore.value = true;
        messages.value = [];
    }
    
    if (!hasMore.value) return;
    
    try {
        loading.value = true;
        
        const response = await axios.get('http://localhost:8080/api/messages/conversation', {
            params: {
                userId1: props.currentUserId,
                userId2: props.targetUserId,
                page: page.value,
                size: size.value
            }
        });
        
        if (response.data.content.length === 0) {
            hasMore.value = false;
        } else {
            if (reset) {
                messages.value = response.data.content;
            } else {
                messages.value = [...messages.value, ...response.data.content];
            }
            page.value++;
            
            // 标记接收的消息为已读
            markMessagesAsRead(response.data.content);
        }
        
        emit('messages-loaded', messages.value);
    } catch (error) {
        console.error('加载消息失败:', error);
        ElMessage.error('加载消息失败，请重试');
    } finally {
        loading.value = false;
        if (reset) {
            nextTick(() => {
                scrollToBottom();
            });
        }
    }
};

// 标记消息为已读
const markMessagesAsRead = async (msgs) => {
    const unreadMessages = msgs.filter(
        msg => msg.receiverId === props.currentUserId && !msg.readStatus
    );
    
    for (const msg of unreadMessages) {
        try {
            await axios.patch(`http://localhost:8080/api/messages/${msg.messageId}/read`);
            // 更新本地消息状态
            const index = messages.value.findIndex(m => m.messageId === msg.messageId);
            if (index !== -1) {
                messages.value[index].readStatus = true;
            }
        } catch (error) {
            console.error('标记消息已读失败:', error);
        }
    }
};

// 获取发送者名称
const getSenderName = (message) => {
    if (message.senderId === props.currentUserId) {
        return '我';
    } else {
        return '对方'; // 这里可以根据需要动态获取用户名
    }
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
        return weekdays[date.getDay()] + ' ' + 
               date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    }
    
    // 其他情况显示完整日期
    return date.toLocaleDateString('zh-CN') + ' ' + 
           date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
};

// 滚动到底部
const scrollToBottom = () => {
    if (chatBodyRef.value) {
        chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight;
    }
};

// 监听目标用户ID变化，重新加载消息
watch(() => props.targetUserId, () => {
    loadMessages(true);
});

// 初始化
onMounted(() => {
    loadMessages(true);
    
    // 设置定时刷新消息
    const refreshInterval = setInterval(() => {
        loadMessages(true);
    }, 30000); // 每30秒刷新一次
    
    // 组件卸载时清除定时器
    return () => {
        clearInterval(refreshInterval);
    };
});
</script>

<style scoped>
.message-chat {
    display: flex;
    flex-direction: column;
    height: 100%;
    border: 1px solid #e0e0e0;
    border-radius: 4px;
    overflow: hidden;
}

.chat-header {
    padding: 10px 15px;
    background-color: #f5f7fa;
    border-bottom: 1px solid #e0e0e0;
}

.chat-header h3 {
    margin: 0;
    font-size: 16px;
    color: #333;
}

.chat-body {
    flex: 1;
    padding: 15px;
    overflow-y: auto;
    background-color: #f9f9f9;
}

.loading-container {
    padding: 20px;
}

.empty-messages {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.message-list {
    display: flex;
    flex-direction: column;
}

.message-item {
    margin-bottom: 15px;
    max-width: 80%;
}

.message-mine {
    align-self: flex-end;
}

.message-other {
    align-self: flex-start;
}

.message-content {
    display: flex;
    flex-direction: column;
}

.message-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: 4px;
    font-size: 12px;
    color: #909399;
}

.message-bubble {
    padding: 10px 15px;
    border-radius: 10px;
    word-break: break-word;
    white-space: pre-wrap;
}

.message-mine .message-bubble {
    background-color: #e1f3ff;
    color: #333;
}

.message-other .message-bubble {
    background-color: #fff;
    color: #333;
    border: 1px solid #e0e0e0;
}

.chat-footer {
    padding: 10px 15px;
    background-color: #fff;
    border-top: 1px solid #e0e0e0;
    display: flex;
    flex-direction: column;
}

.chat-footer .el-button {
    margin-top: 10px;
    align-self: flex-end;
}
</style> 