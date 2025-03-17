<template>
  <div class="applications-container">
    <div class="page-header">
      <h2 class="page-title">{{ isSpecificJob ? '岗位应聘者' : '申请管理' }}</h2>
      <div v-if="isSpecificJob" class="header-actions">
        <el-button type="primary" @click="goBack">
          <el-icon><Back /></el-icon> 返回岗位管理
        </el-button>
      </div>
    </div>
    
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
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import MessageChat from '../common/MessageChat.vue';

const route = useRoute();
const router = useRouter();
const jobId = computed(() => route.params.jobId);
const isSpecificJob = computed(() => !!jobId.value);

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

// 加载应聘者数据
const fetchApplications = async () => {
  try {
    loading.value = true;
    
    // 使用自定义数据替代API调用
    const mockApplications = [
      {
        applicationId: '1',
        name: '张三',
        studentId: '2022001',
        user: { userId: 'u1' },
        jobPost: { jobPostId: 'job1', jobTitle: '前端开发实习生' },
        applyTime: new Date().toISOString(),
        status: 'PENDING',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        introduction: '我是一名大三学生，熟悉前端开发技术，有React和Vue项目经验。希望能够加入贵公司实习，提升自己的能力。'
      },
      {
        applicationId: '2',
        name: '李四',
        studentId: '2022002',
        user: { userId: 'u2' },
        jobPost: { jobPostId: 'job1', jobTitle: '前端开发实习生' },
        applyTime: new Date(Date.now() - 86400000).toISOString(), // 昨天
        status: 'APPROVED',
        phone: '13900139001',
        email: 'lisi@example.com',
        introduction: '大四学生，专注Web开发两年，参与过多个实际项目，精通HTML/CSS/JavaScript，熟悉Vue.js框架。'
      },
      {
        applicationId: '3',
        name: '王五',
        studentId: '2022003',
        user: { userId: 'u3' },
        jobPost: { jobPostId: 'job2', jobTitle: '后端开发实习生' },
        applyTime: new Date(Date.now() - 172800000).toISOString(), // 前天
        status: 'REJECTED',
        phone: '13700137001',
        email: 'wangwu@example.com',
        introduction: '计算机专业大四学生，对Java后端开发有浓厚兴趣，熟悉Spring Boot框架，希望能在实习中提升实战能力。'
      },
      {
        applicationId: '4',
        name: '赵六',
        studentId: '2022004',
        user: { userId: 'u4' },
        jobPost: { jobPostId: 'job2', jobTitle: '后端开发实习生' },
        applyTime: new Date(Date.now() - 259200000).toISOString(), // 三天前
        status: 'PENDING',
        phone: '13600136001',
        email: 'zhaoliu@example.com',
        introduction: '熟悉Java、Python，有Spring Boot项目经验，曾参与校内项目开发，希望能够获得实习机会深入学习后端技术。'
      },
      {
        applicationId: '5',
        name: '钱七',
        studentId: '2022005',
        user: { userId: 'u5' },
        jobPost: { jobPostId: 'job3', jobTitle: '全栈开发实习生' },
        applyTime: new Date(Date.now() - 345600000).toISOString(), // 四天前
        status: 'APPROVED',
        phone: '13500135001',
        email: 'qianqi@example.com',
        introduction: '计算机科学与技术专业大三学生，熟悉前后端开发技术，对全栈开发充满热情，希望能够加入贵公司锻炼自己。'
      }
    ];
    
    // 根据筛选条件过滤数据
    let filteredApplications = [...mockApplications];
    
    // 根据岗位筛选
    if (jobFilter.value !== 'all') {
      filteredApplications = filteredApplications.filter(app => app.jobPost.jobPostId === jobFilter.value);
    }
    
    // 根据状态筛选
    if (statusFilter.value !== 'all') {
      filteredApplications = filteredApplications.filter(app => app.status === statusFilter.value);
    }
    
    // 设置应用数据
    applications.value = filteredApplications;
    
    // 计算状态统计
    stats.value = {
      PENDING: mockApplications.filter(app => app.status === 'PENDING').length,
      APPROVED: mockApplications.filter(app => app.status === 'APPROVED').length,
      REJECTED: mockApplications.filter(app => app.status === 'REJECTED').length
    };
    
    // 设置总数
    pagination.value.total = applications.value.length;
  } catch (error) {
    ElMessage.error('获取申请数据失败');
    console.error('获取申请数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 加载岗位数据
const fetchJobs = async () => {
  try {
    // 使用自定义岗位数据
    jobs.value = [
      { jobPostId: 'job1', jobTitle: '前端开发实习生' },
      { jobPostId: 'job2', jobTitle: '后端开发实习生' },
      { jobPostId: 'job3', jobTitle: '全栈开发实习生' },
      { jobPostId: 'job4', jobTitle: '产品经理助理' },
      { jobPostId: 'job5', jobTitle: 'UI设计实习生' }
    ];
    
    // 如果是从特定岗位进入，检查该岗位是否在列表中
    if (isSpecificJob.value && !jobs.value.find(job => job.jobPostId === jobId.value)) {
      // 假设岗位存在
      jobs.value.push({ jobPostId: jobId.value, jobTitle: `岗位${jobId.value}` });
    }
  } catch (error) {
    ElMessage.error('获取岗位数据失败');
    console.error('获取岗位数据失败:', error);
  }
};

// 计算状态统计
const calculateStats = () => {
  // 已在fetchApplications中计算
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
    
    // 更新本地数据而不是调用API
    const appIndex = applications.value.findIndex(app => app.applicationId === selectedApplication.value.applicationId);
    if (appIndex !== -1) {
      // 更新应用状态
      applications.value[appIndex].status = status;
      applications.value[appIndex].reviewComment = reviewForm.value.comment;
      
      // 更新选中的应用状态
      selectedApplication.value.status = status;
      selectedApplication.value.reviewComment = reviewForm.value.comment;
      
      // 更新统计数据
      const oldStatus = selectedApplication.value.status;
      if (oldStatus !== status) {
        if (oldStatus) stats.value[oldStatus]--;
        stats.value[status] = (stats.value[status] || 0) + 1;
      }
    }
    
    ElMessage.success(`申请已${reviewType.value === 'approve' ? '通过' : '拒绝'}`);
    
    // 关闭对话框
    reviewDialogVisible.value = false;
    detailDialogVisible.value = false;
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
  fetchApplications();
};

// 处理分页变化
const handleCurrentChange = (page) => {
  pagination.value.currentPage = page;
  fetchApplications();
};

// 返回岗位管理页面
const goBack = () => {
  router.push('/employer/jobs/manage');
};

// 初始化
onMounted(async () => {
  currentUser.value = getCurrentUser();
  if (currentUser.value) {
    await fetchJobs();
    await fetchApplications();
  }
});
</script>

<style scoped>
.applications-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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