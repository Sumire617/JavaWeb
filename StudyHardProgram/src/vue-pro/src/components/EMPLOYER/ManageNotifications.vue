<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

const notificationList = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const currentType = ref('all');
const dialogVisible = ref(false);
const currentNotification = ref(null);

// 分页数据
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 通知类型选项
const typeOptions = [
  { value: 'all', label: '全部' },
  { value: 'GENERAL', label: '普通通知' },
  { value: 'IMPORTANT', label: '重要通知' },
  { value: 'URGENT', label: '紧急通知' },
  { value: 'SYSTEM', label: '系统通知' }
];

// 优先级映射
const priorityMap = {
  'LOW': { label: '低', type: 'info' },
  'NORMAL': { label: '中', type: 'warning' },
  'HIGH': { label: '高', type: 'danger' }
};

// 获取通知列表
const fetchNotifications = async () => {
  try {
    loading.value = true;
    const response = await axios.get('/api/employer/notifications', {
      params: {
        page: pagination.value.currentPage - 1,
        size: pagination.value.pageSize,
        type: currentType.value === 'all' ? null : currentType.value,
        query: searchQuery.value || null
      }
    });
    notificationList.value = response.data.content;
    pagination.value.total = response.data.totalElements;
  } catch (error) {
    ElMessage.error('获取通知列表失败');
    console.error('获取通知列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 查看通知详情
const handleView = (notification) => {
  currentNotification.value = notification;
  dialogVisible.value = true;
};

// 编辑通知
const handleEdit = (notification) => {
  // 实现编辑功能
  ElMessage.info('编辑功能开发中...');
};

// 删除通知
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该通知吗？此操作不可恢复。', '警告', {
      type: 'error'
    });
    
    await axios.delete(`/api/employer/notifications/${id}`);
    ElMessage.success('通知已删除');
    fetchNotifications();
  } catch (error) {
    if (error === 'cancel') return;
    ElMessage.error('删除通知失败');
    console.error('删除通知失败:', error);
  }
};

// 搜索
const handleSearch = () => {
  pagination.value.currentPage = 1;
  fetchNotifications();
};

// 重置搜索
const handleReset = () => {
  searchQuery.value = '';
  currentType.value = 'all';
  pagination.value.currentPage = 1;
  fetchNotifications();
};

// 类型改变
const handleTypeChange = () => {
  pagination.value.currentPage = 1;
  fetchNotifications();
};

// 页码改变
const handleCurrentChange = (page) => {
  pagination.value.currentPage = page;
  fetchNotifications();
};

// 每页条数改变
const handleSizeChange = (size) => {
  pagination.value.pageSize = size;
  pagination.value.currentPage = 1;
  fetchNotifications();
};

// 格式化时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 格式化目标群体
const formatTargetGroups = (groups) => {
  if (!groups || !groups.length) return '无';
  const groupMap = {
    'ALL': '全体员工',
    'DEPARTMENT': '指定部门',
    'POSITION': '指定职位',
    'INDIVIDUAL': '指定个人'
  };
  return groups.map(group => groupMap[group] || group).join('、');
};

onMounted(() => {
  fetchNotifications();
});
</script>

<template>
  <div class="manage-notifications">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <h2>通知管理</h2>
          <el-button type="primary" @click="$router.push('/employer/notifications/publish')">
            <el-icon><Plus /></el-icon>发布通知
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索通知标题"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>

        <el-select
          v-model="currentType"
          placeholder="通知类型"
          class="type-select"
          @change="handleTypeChange"
        >
          <el-option
            v-for="option in typeOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>

        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 通知列表 -->
      <el-table
        :data="notificationList"
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'URGENT' ? 'danger' : (row.type === 'IMPORTANT' ? 'warning' : '')">
              {{ typeOptions.find(option => option.value === row.type)?.label || row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="priorityMap[row.priority]?.type">
              {{ priorityMap[row.priority]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetGroups" label="目标群体" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ formatTargetGroups(row.targetGroups) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="150">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="validUntil" label="有效期至" width="150">
          <template #default="{ row }">
            {{ formatDateTime(row.validUntil) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="通知详情"
      width="60%"
    >
      <div v-if="currentNotification" class="notification-detail">
        <h3>{{ currentNotification.title }}</h3>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="通知类型">
            {{ typeOptions.find(option => option.value === currentNotification.type)?.label }}
          </el-descriptions-item>
          <el-descriptions-item label="优先级">
            {{ priorityMap[currentNotification.priority]?.label }}
          </el-descriptions-item>
          <el-descriptions-item label="目标群体">
            {{ formatTargetGroups(currentNotification.targetGroups) }}
          </el-descriptions-item>
          <el-descriptions-item label="有效期至">
            {{ formatDateTime(currentNotification.validUntil) }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="content-section">
          <h4>通知内容：</h4>
          <div class="content-box">{{ currentNotification.content }}</div>
        </div>

        <div v-if="currentNotification.attachments?.length" class="attachments-section">
          <h4>附件：</h4>
          <el-upload
            class="attachment-list"
            :file-list="currentNotification.attachments"
            :disabled="true"
          >
            <template #file="{ file }">
              <div class="attachment-item">
                <el-icon><Document /></el-icon>
                <span class="filename">{{ file.name }}</span>
                <el-button link type="primary" @click="window.open(file.url)">
                  下载
                </el-button>
              </div>
            </template>
          </el-upload>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.manage-notifications {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.type-select {
  width: 150px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.notification-detail {
  padding: 20px;
}

.notification-detail h3 {
  margin: 0 0 20px;
  font-size: 20px;
  font-weight: 500;
}

.content-section {
  margin-top: 20px;
}

.content-section h4 {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 500;
}

.content-box {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  min-height: 100px;
  white-space: pre-wrap;
}

.attachments-section {
  margin-top: 20px;
}

.attachments-section h4 {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 500;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.filename {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.el-descriptions) {
  margin: 20px 0;
}
</style> 