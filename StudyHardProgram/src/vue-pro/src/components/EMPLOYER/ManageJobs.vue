<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const jobList = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const currentStatus = ref('all');

// 分页数据
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 状态选项
const statusOptions = [
  { value: 'all', label: '全部' },
  { value: 'active', label: '招聘中' },
  { value: 'pending', label: '待审核' },
  { value: 'rejected', label: '已拒绝' },
  { value: 'closed', label: '已结束' }
];

// 获取岗位列表
const fetchJobs = async () => {
  try {
    loading.value = true;
    const response = await axios.get('/api/employer/jobs', {
      params: {
        page: pagination.value.currentPage - 1,
        size: pagination.value.pageSize,
        status: currentStatus.value === 'all' ? null : currentStatus.value,
        query: searchQuery.value || null
      }
    });
    jobList.value = response.data.content;
    pagination.value.total = response.data.totalElements;
  } catch (error) {
    ElMessage.error('获取岗位列表失败');
    console.error('获取岗位列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 编辑岗位
const handleEdit = (jobId) => {
  router.push(`/employer/jobs/edit/${jobId}`);
};

// 关闭岗位
const handleClose = async (jobId) => {
  try {
    await ElMessageBox.confirm('确定要关闭该岗位吗？关闭后将不再显示在招聘列表中。', '提示', {
      type: 'warning'
    });
    
    await axios.patch(`/api/employer/jobs/${jobId}/close`);
    ElMessage.success('岗位已关闭');
    fetchJobs();
  } catch (error) {
    if (error === 'cancel') return;
    ElMessage.error('关闭岗位失败');
    console.error('关闭岗位失败:', error);
  }
};

// 删除岗位
const handleDelete = async (jobId) => {
  try {
    await ElMessageBox.confirm('确定要删除该岗位吗？此操作不可恢复。', '警告', {
      type: 'error'
    });
    
    await axios.delete(`/api/employer/jobs/${jobId}`);
    ElMessage.success('岗位已删除');
    fetchJobs();
  } catch (error) {
    if (error === 'cancel') return;
    ElMessage.error('删除岗位失败');
    console.error('删除岗位失败:', error);
  }
};

// 查看应聘者
const handleViewApplicants = (jobId) => {
  router.push(`/employer/jobs/${jobId}/applicants`);
};

// 搜索
const handleSearch = () => {
  pagination.value.currentPage = 1;
  fetchJobs();
};

// 重置搜索
const handleReset = () => {
  searchQuery.value = '';
  currentStatus.value = 'all';
  pagination.value.currentPage = 1;
  fetchJobs();
};

// 状态改变
const handleStatusChange = () => {
  pagination.value.currentPage = 1;
  fetchJobs();
};

// 页码改变
const handleCurrentChange = (page) => {
  pagination.value.currentPage = page;
  fetchJobs();
};

// 每页条数改变
const handleSizeChange = (size) => {
  pagination.value.pageSize = size;
  pagination.value.currentPage = 1;
  fetchJobs();
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

onMounted(() => {
  fetchJobs();
});
</script>

<template>
  <div class="manage-jobs">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <h2>岗位管理</h2>
          <el-button type="primary" @click="router.push('/employer/jobs/post')">
            <el-icon><Plus /></el-icon>发布岗位
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索岗位名称"
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
          v-model="currentStatus"
          placeholder="岗位状态"
          class="status-select"
          @change="handleStatusChange"
        >
          <el-option
            v-for="option in statusOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>

        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 岗位列表 -->
      <el-table
        :data="jobList"
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="jobTitle" label="岗位名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="department" label="所属部门" width="120" />
        <el-table-column prop="jobType" label="类型" width="100" />
        <el-table-column prop="location" label="工作地点" width="120" show-overflow-tooltip />
        <el-table-column prop="salaryRange" label="薪资" width="120" />
        <el-table-column prop="recruitmentCount" label="招聘人数" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : (row.status === 'pending' ? 'warning' : 'info')">
              {{ statusOptions.find(option => option.value === row.status)?.label || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="postedAt" label="发布时间" width="150">
          <template #default="{ row }">
            {{ formatDateTime(row.postedAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止日期" width="150">
          <template #default="{ row }">
            {{ formatDateTime(row.deadline) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleViewApplicants(row.jobPostId)"
            >
              查看应聘者
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleEdit(row.jobPostId)"
              :disabled="row.status === 'closed'"
            >
              编辑
            </el-button>
            <el-button
              link
              type="warning"
              @click="handleClose(row.jobPostId)"
              :disabled="row.status === 'closed'"
            >
              关闭
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row.jobPostId)"
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
  </div>
</template>

<style scoped>
.manage-jobs {
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

.status-select {
  width: 150px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table .cell) {
  white-space: nowrap;
}
</style> 