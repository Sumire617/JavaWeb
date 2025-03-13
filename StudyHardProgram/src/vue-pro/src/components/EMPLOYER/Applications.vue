<template>
  <div class="applications-container">
    <h2 class="page-title">申请管理</h2>
    
    <div class="filter-section">
      <el-select v-model="statusFilter" placeholder="申请状态" @change="handleFilterChange">
        <el-option label="全部" value="all" />
        <el-option label="待处理" value="PENDING" />
        <el-option label="已通过" value="APPROVED" />
        <el-option label="已拒绝" value="REJECTED" />
      </el-select>
      
      <el-select v-model="jobFilter" placeholder="岗位筛选" @change="handleFilterChange">
        <el-option label="全部岗位" value="all" />
        <el-option 
          v-for="job in jobs" 
          :key="job.jobPostId" 
          :label="job.jobTitle" 
          :value="job.jobPostId" 
        />
      </el-select>
    </div>
    
    <div class="stats-section">
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.PENDING || 0 }}</div>
        <div class="stat-label">待处理</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.APPROVED || 0 }}</div>
        <div class="stat-label">已通过</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.REJECTED || 0 }}</div>
        <div class="stat-label">已拒绝</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ totalApplications }}</div>
        <div class="stat-label">总申请</div>
      </el-card>
    </div>
    
    <div class="applications-list">
      <el-table
        v-loading="loading"
        :data="applications"
        style="width: 100%"
        border
      >
        <el-table-column prop="name" label="申请人" width="120" />
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column label="岗位" width="180">
          <template #default="scope">
            {{ getJobTitle(scope.row.jobPost.jobPostId) }}
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              effect="plain"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="viewApplication(scope.row)"
            >
              查看
            </el-button>
            <el-button
              v-if="scope.row.status === 'PENDING'"
              size="small"
              type="success"
              @click="approveApplication(scope.row)"
            >
              通过
            </el-button>
            <el-button
              v-if="scope.row.status === 'PENDING'"
              size="small"
              type="danger"
              @click="rejectApplication(scope.row)"
            >
              拒绝
            </el-button>
            <el-button
              size="small"
              @click="contactApplicant(scope.row)"
            >
              联系
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          :page-size="pagination.pageSize"
          :total="pagination.total"
          layout="prev, pager, next, jumper"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    
    <!-- 申请详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="申请详情"
      width="600px"
    >
      <div v-if="selectedApplication" class="application-detail">
        <div class="detail-section">
          <h3>申请人信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ selectedApplication.name }}</el-descriptions-item>
            <el-descriptions-item label="学号">{{ selectedApplication.studentId }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ selectedApplication.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ selectedApplication.email }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="detail-section">
          <h3>申请岗位</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="岗位名称">{{ getJobTitle(selectedApplication.jobPost.jobPostId) }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ formatDateTime(selectedApplication.applyTime) }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(selectedApplication.status)">
                {{ getStatusText(selectedApplication.status) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="detail-section">
          <h3>自我介绍</h3>
          <div class="introduction-text">{{ selectedApplication.introduction }}</div>
        </div>
        
        <div v-if="selectedApplication.status !== 'PENDING'" class="detail-section">
          <h3>审核信息</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="审核时间">{{ formatDateTime(selectedApplication.reviewTime) }}</el-descriptions-item>
            <el-descriptions-item label="审核意见">{{ selectedApplication.reviewComment || '无' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button
            v-if="selectedApplication && selectedApplication.status === 'PENDING'"
            type="success"
            @click="approveApplication(selectedApplication)"
          >
            通过申请
          </el-button>
          <el-button
            v-if="selectedApplication && selectedApplication.status === 'PENDING'"
            type="danger"
            @click="rejectApplication(selectedApplication)"
          >
            拒绝申请
          </el-button>
          <el-button
            type="primary"
            @click="contactApplicant(selectedApplication)"
          >
            联系申请人
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 审核对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      :title="reviewType === 'approve' ? '通过申请' : '拒绝申请'"
      width="500px"
    >
      <el-form
        ref="reviewFormRef"
        :model="reviewForm"
        label-width="100px"
      >
        <el-form-item label="审核意见">
          <el-input
            v-model="reviewForm.comment"
            type="textarea"
            :rows="4"
            :placeholder="reviewType === 'approve' ? '请输入通过意见（可选）' : '请输入拒绝原因（可选）'"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button
            :type="reviewType === 'approve' ? 'success' : 'danger'"
            @click="submitReview"
          >
            {{ reviewType === 'approve' ? '确认通过' : '确认拒绝' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 消息对话框 -->
    <el-dialog
      v-model="chatDialogVisible"
      title="发送消息"
      width="600px"
    >
      <div v-if="selectedApplication" class="chat-container">
        <MessageChat
          :current-user-id="currentUser.userId"
          :target-user-id="selectedApplication.user.userId"
          :current-user-type="'EMPLOYER'"
          :chat-title="`与 ${selectedApplication.name} 的对话`"
          :job-post-id="selectedApplication.jobPost.jobPostId"
          :application-id="selectedApplication.applicationId"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import MessageChat from '../common/MessageChat.vue';

const loading = ref(true);
const applications = ref([]);
const jobs = ref([]);
const stats = ref({});
const statusFilter = ref('all');
const jobFilter = ref('all');
const currentUser = ref(null);

// 分页数据
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 对话框控制
const detailDialogVisible = ref(false);
const reviewDialogVisible = ref(false);
const chatDialogVisible = ref(false);
const selectedApplication = ref(null);
const reviewType = ref('approve');
const reviewForm = ref({
  comment: ''
});

// 计算总申请数
const totalApplications = computed(() => {
  return (stats.value.PENDING || 0) + (stats.value.APPROVED || 0) + (stats.value.REJECTED || 0);
});

// 获取当前用户信息
const getCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  if (!userStr) {
    ElMessage.error('请先登录');
    return null;
  }
  return JSON.parse(userStr);
};

// 加载申请列表
const loadApplications = async () => {
  if (!currentUser.value) return;
  
  try {
    loading.value = true;
    
    // 构建请求参数
    const params = {
      page: pagination.value.currentPage - 1,
      size: pagination.value.pageSize
    };
    
    let url = `http://localhost:8080/api/applications/employer/${currentUser.value.userId}`;
    
    // 如果有岗位筛选
    if (jobFilter.value !== 'all') {
      url = `http://localhost:8080/api/applications/status`;
      params.jobPostId = jobFilter.value;
      params.status = statusFilter.value !== 'all' ? statusFilter.value : null;
    }
    
    const response = await axios.get(url, { params });
    
    applications.value = response.data.content;
    pagination.value.total = response.data.totalElements;
  } catch (error) {
    console.error('加载申请列表失败:', error);
    ElMessage.error('加载申请列表失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 加载岗位列表
const loadJobs = async () => {
  if (!currentUser.value) return;
  
  try {
    const response = await axios.get(`http://localhost:8080/api/jobs`, {
      params: {
        employerId: currentUser.value.userId
      }
    });
    
    jobs.value = response.data.content;
  } catch (error) {
    console.error('加载岗位列表失败:', error);
  }
};

// 加载申请统计
const loadStats = async () => {
  if (!currentUser.value) return;
  
  try {
    const response = await axios.get(`http://localhost:8080/api/applications/employer/${currentUser.value.userId}/stats`);
    stats.value = response.data;
  } catch (error) {
    console.error('加载申请统计失败:', error);
  }
};

// 获取岗位标题
const getJobTitle = (jobId) => {
  const job = jobs.value.find(j => j.jobPostId === jobId);
  return job ? job.jobTitle : '未知岗位';
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'PENDING': return '待处理';
    case 'APPROVED': return '已通过';
    case 'REJECTED': return '已拒绝';
    default: return '未知';
  }
};

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 'PENDING': return 'warning';
    case 'APPROVED': return 'success';
    case 'REJECTED': return 'danger';
    default: return 'info';
  }
};

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-CN');
};

// 查看申请详情
const viewApplication = (application) => {
  selectedApplication.value = application;
  detailDialogVisible.value = true;
};

// 通过申请
const approveApplication = (application) => {
  selectedApplication.value = application;
  reviewType.value = 'approve';
  reviewForm.value.comment = '';
  reviewDialogVisible.value = true;
};

// 拒绝申请
const rejectApplication = (application) => {
  selectedApplication.value = application;
  reviewType.value = 'reject';
  reviewForm.value.comment = '';
  reviewDialogVisible.value = true;
};

// 提交审核
const submitReview = async () => {
  if (!selectedApplication.value) return;
  
  try {
    const status = reviewType.value === 'approve' ? 'APPROVED' : 'REJECTED';
    
    await axios.patch(`http://localhost:8080/api/applications/${selectedApplication.value.applicationId}`, {
      status,
      reviewComment: reviewForm.value.comment
    });
    
    ElMessage.success(`申请已${reviewType.value === 'approve' ? '通过' : '拒绝'}`);
    
    // 关闭对话框
    reviewDialogVisible.value = false;
    detailDialogVisible.value = false;
    
    // 重新加载数据
    loadApplications();
    loadStats();
  } catch (error) {
    console.error('处理申请失败:', error);
    ElMessage.error('处理申请失败，请重试');
  }
};

// 联系申请人
const contactApplicant = (application) => {
  selectedApplication.value = application;
  chatDialogVisible.value = true;
};

// 处理筛选变化
const handleFilterChange = () => {
  pagination.value.currentPage = 1;
  loadApplications();
};

// 处理分页变化
const handleCurrentChange = (page) => {
  pagination.value.currentPage = page;
  loadApplications();
};

// 初始化
onMounted(() => {
  currentUser.value = getCurrentUser();
  if (currentUser.value) {
    loadJobs();
    loadStats();
    loadApplications();
  }
});
</script>

<style scoped>
.applications-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.filter-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.stats-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  text-align: center;
  padding: 15px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.applications-list {
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.application-detail {
  padding: 10px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h3 {
  margin-bottom: 10px;
  font-size: 16px;
  color: #333;
}

.introduction-text {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  white-space: pre-wrap;
}

.chat-container {
  height: 500px;
}
</style> 