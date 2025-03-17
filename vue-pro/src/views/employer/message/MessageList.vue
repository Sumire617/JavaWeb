<template>
  <div class="message-list">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>消息列表</span>
          <el-button type="primary" @click="refreshMessages">刷新</el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部消息" name="all">
          <el-table :data="messages" style="width: 100%">
            <el-table-column prop="senderName" label="发送者" width="120" />
            <el-table-column prop="content" label="内容" />
            <el-table-column prop="sendTime" label="发送时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.sendTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="readStatus" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.readStatus ? 'success' : 'warning'">
                  {{ scope.row.readStatus ? '已读' : '未读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button
                  v-if="!scope.row.readStatus"
                  type="primary"
                  size="small"
                  @click="markAsRead(scope.row.messageId)"
                >
                  标记已读
                </el-button>
                <el-button
                  type="primary"
                  size="small"
                  @click="showMessageDetail(scope.row)"
                >
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="未读消息" name="unread">
          <el-table :data="unreadMessages" style="width: 100%">
            <el-table-column prop="senderName" label="发送者" width="120" />
            <el-table-column prop="content" label="内容" />
            <el-table-column prop="sendTime" label="发送时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.sendTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="markAsRead(scope.row.messageId)"
                >
                  标记已读
                </el-button>
                <el-button
                  type="primary"
                  size="small"
                  @click="showMessageDetail(scope.row)"
                >
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 消息详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="消息详情"
      width="50%"
    >
      <div v-if="currentMessage">
        <p><strong>发送者：</strong>{{ currentMessage.senderName }}</p>
        <p><strong>发送时间：</strong>{{ formatDateTime(currentMessage.sendTime) }}</p>
        <p><strong>内容：</strong>{{ currentMessage.content }}</p>
        <p v-if="currentMessage.jobPostId">
          <strong>相关岗位：</strong>
          <el-button type="text" @click="viewJobPost(currentMessage.jobPostId)">
            查看岗位
          </el-button>
        </p>
        <p v-if="currentMessage.applicationId">
          <strong>相关申请：</strong>
          <el-button type="text" @click="viewApplication(currentMessage.applicationId)">
            查看申请
          </el-button>
        </p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { formatDateTime } from '@/utils/date'

export default {
  name: 'MessageList',
  setup() {
    const messages = ref([])
    const unreadMessages = ref([])
    const activeTab = ref('all')
    const dialogVisible = ref(false)
    const currentMessage = ref(null)

    const fetchMessages = async () => {
      try {
        const userId = localStorage.getItem('userId')
        const response = await axios.get(`/api/messages/user/${userId}`)
        messages.value = response.data
        unreadMessages.value = messages.value.filter(msg => !msg.readStatus)
      } catch (error) {
        ElMessage.error('获取消息列表失败')
      }
    }

    const markAsRead = async (messageId) => {
      try {
        await axios.put(`/api/messages/${messageId}/read`)
        ElMessage.success('标记已读成功')
        fetchMessages()
      } catch (error) {
        ElMessage.error('标记已读失败')
      }
    }

    const showMessageDetail = (message) => {
      currentMessage.value = message
      dialogVisible.value = true
    }

    const viewJobPost = (jobPostId) => {
      // 跳转到岗位详情页
      router.push(`/employer/job-posts/${jobPostId}`)
    }

    const viewApplication = (applicationId) => {
      // 跳转到申请详情页
      router.push(`/employer/applications/${applicationId}`)
    }

    const refreshMessages = () => {
      fetchMessages()
    }

    onMounted(() => {
      fetchMessages()
    })

    return {
      messages,
      unreadMessages,
      activeTab,
      dialogVisible,
      currentMessage,
      formatDateTime,
      markAsRead,
      showMessageDetail,
      viewJobPost,
      viewApplication,
      refreshMessages
    }
  }
}
</script>

<style scoped>
.message-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 