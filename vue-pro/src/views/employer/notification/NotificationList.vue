<template>
  <div class="notification-list">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>通知列表</span>
          <div>
            <el-button type="primary" @click="markAllAsRead" :disabled="!hasUnread">
              全部标记已读
            </el-button>
            <el-button type="primary" @click="refreshNotifications">刷新</el-button>
          </div>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部通知" name="all">
          <el-table :data="notifications" style="width: 100%">
            <el-table-column prop="message" label="通知内容" />
            <el-table-column prop="sentAt" label="发送时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.sentAt) }}
              </template>
            </el-table-column>
            <el-table-column prop="isRead" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isRead ? 'success' : 'warning'">
                  {{ scope.row.isRead ? '已读' : '未读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button
                  v-if="!scope.row.isRead"
                  type="primary"
                  size="small"
                  @click="markAsRead(scope.row.notificationId)"
                >
                  标记已读
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="未读通知" name="unread">
          <el-table :data="unreadNotifications" style="width: 100%">
            <el-table-column prop="message" label="通知内容" />
            <el-table-column prop="sentAt" label="发送时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.sentAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="markAsRead(scope.row.notificationId)"
                >
                  标记已读
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { formatDateTime } from '@/utils/date'

export default {
  name: 'NotificationList',
  setup() {
    const notifications = ref([])
    const unreadNotifications = ref([])
    const activeTab = ref('all')

    const hasUnread = computed(() => {
      return unreadNotifications.value.length > 0
    })

    const fetchNotifications = async () => {
      try {
        const userId = localStorage.getItem('userId')
        const response = await axios.get(`/api/notifications/user/${userId}`)
        notifications.value = response.data
        unreadNotifications.value = notifications.value.filter(notification => !notification.isRead)
      } catch (error) {
        ElMessage.error('获取通知列表失败')
      }
    }

    const markAsRead = async (notificationId) => {
      try {
        await axios.put(`/api/notifications/${notificationId}/read`)
        ElMessage.success('标记已读成功')
        fetchNotifications()
      } catch (error) {
        ElMessage.error('标记已读失败')
      }
    }

    const markAllAsRead = async () => {
      try {
        const userId = localStorage.getItem('userId')
        await axios.put(`/api/notifications/user/${userId}/read-all`)
        ElMessage.success('全部标记已读成功')
        fetchNotifications()
      } catch (error) {
        ElMessage.error('全部标记已读失败')
      }
    }

    const refreshNotifications = () => {
      fetchNotifications()
    }

    onMounted(() => {
      fetchNotifications()
    })

    return {
      notifications,
      unreadNotifications,
      activeTab,
      hasUnread,
      formatDateTime,
      markAsRead,
      markAllAsRead,
      refreshNotifications
    }
  }
}
</script>

<style scoped>
.notification-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header > div {
  display: flex;
  gap: 10px;
}
</style> 