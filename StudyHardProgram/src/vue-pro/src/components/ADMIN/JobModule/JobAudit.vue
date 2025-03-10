<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

// 响应式数据
const jobPosts = ref([]);
const currentJob = ref(null);
const drawerVisible = ref(false);
const auditHistory = ref([]);
const evaluations = ref([]);
const auditForm = ref({
  auditResult: '',
  auditComment: '',
  isApproved: 1,
  adminId: '42f15136-a9db-4fed-bbfd-6b2af59a9cec' // 当前登录的管理员ID
});

// 分页相关数据
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 状态映射
const statusMap = {
  'PENDING': '待审核',
  'APPROVED': '已通过',
  'REJECTED': '已拒绝'
};

// 加载岗位列表
const fetchJobPosts = async () => {
  try {
    const response = await axios.get('/api/jobs/pending', {
      params: {
        page: pagination.value.currentPage - 1,
        size: pagination.value.pageSize
      }
    });
    jobPosts.value = response.data.content;
    pagination.value.total = response.data.totalElements;
  } catch (error) {
    ElMessage.error('加载岗位列表失败：' + error.message);
  }
};

// 打开审核抽屉
const openAuditDrawer = async (job) => {
  currentJob.value = job;
  drawerVisible.value = true;
  await Promise.all([
    fetchAuditHistory(job.jobId),
    fetchJobEvaluations(job.jobId)
  ]);
};

// 获取审核历史
const fetchAuditHistory = async (jobPostId) => {
  try {
    const response = await axios.get(`http://localhost:8080/api/job-audits/${jobPostId}`);
    auditHistory.value = response.data;
  } catch (error) {
    ElMessage.error('加载审核历史失败：' + error.message);
  }
};

// 获取岗位评价
const fetchJobEvaluations = async (jobPostId) => {
  try {
    const response = await axios.get(`http://localhost:8080/api/job-evaluations/${jobPostId}`);
    evaluations.value = response.data;
  } catch (error) {
    ElMessage.error('加载岗位评价失败：' + error.message);
  }
};

// 提交审核
const submitAudit = async () => {
  try {
    const auditData = {
      jobPost: { jobPostId: currentJob.value.jobPostId },
      auditResult: auditForm.value.auditResult,
      auditComment: auditForm.value.auditComment,
      isApproved: auditForm.value.isApproved,
      adminId: auditForm.value.adminId,
      auditTime: new Date().toISOString()
    };

    await axios.post('http://localhost:8080/api/job-audits', auditData);
    
    // 更新岗位状态
    const jobStatus = auditForm.value.isApproved === 1 ? 'APPROVED' : 'REJECTED';
    await axios.patch(`http://localhost:8080/api/job-posts/editId=${currentJob.value.jobPostId}`, {
      ...currentJob.value,
      status: jobStatus
    });

    ElMessage.success('审核提交成功');
    drawerVisible.value = false;
    await fetchJobPosts();
  } catch (error) {
    ElMessage.error('审核提交失败：' + error.message);
  }
};

// 格式化时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

// 计算平均评分
const averageRating = computed(() => {
  if (!evaluations.value.length) return 0;
  const sum = evaluations.value.reduce((acc, curr) => acc + curr.rating, 0);
  return (sum / evaluations.value.length).toFixed(1);
});

// 页码改变处理
const handleCurrentChange = (val) => {
  pagination.value.currentPage = val;
  fetchJobPosts();
};

// 每页条数改变处理
const handleSizeChange = (val) => {
  pagination.value.pageSize = val;
  pagination.value.currentPage = 1;
  fetchJobPosts();
};

onMounted(fetchJobPosts);
</script>

<template>
  <div class="job-audit-container">
    <!-- 岗位列表 -->
    <el-table :data="jobPosts" border style="width: 100%; margin-bottom: 20px;">
      <el-table-column prop="jobPostId" label="岗位ID" width="220" />
      <el-table-column prop="jobTitle" label="岗位名称" />
      <el-table-column prop="employerId" label="发布单位ID" width="120" />
      <el-table-column prop="location" label="工作地点" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PENDING' ? 'warning' : (row.status === 'APPROVED' ? 'success' : 'danger')">
            {{ statusMap[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="postedAt" label="发布时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.postedAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openAuditDrawer(row)">
            审核
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 30, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 审核抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="岗位审核"
      direction="rtl"
      size="50%"
    >
      <template #default>
        <div v-if="currentJob" class="drawer-content">
          <!-- 岗位基本信息 -->
          <div class="section">
            <h3>岗位信息</h3>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="岗位名称">{{ currentJob.jobTitle }}</el-descriptions-item>
              <el-descriptions-item label="发布单位ID">{{ currentJob.employerId }}</el-descriptions-item>
              <el-descriptions-item label="工作地点">{{ currentJob.location }}</el-descriptions-item>
              <el-descriptions-item label="薪资范围">{{ currentJob.salaryRange }}</el-descriptions-item>
              <el-descriptions-item label="岗位描述">{{ currentJob.jobDescription }}</el-descriptions-item>
              <el-descriptions-item label="任职要求">{{ currentJob.requirements }}</el-descriptions-item>
              <el-descriptions-item label="发布时间">{{ formatDateTime(currentJob.postedAt) }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ formatDateTime(currentJob.updatedAt) }}</el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 评价信息 -->
          <div class="section">
            <h3>岗位评价 ({{ evaluations.length }}条) - 平均评分：{{ averageRating }}</h3>
            <el-timeline>
              <el-timeline-item
                v-for="evaluation in evaluations"
                :key="evaluation.evaluationId"
                :timestamp="formatDateTime(evaluation.evaluationTime)"
              >
                <el-card>
                  <div>用户ID：{{ evaluation.userId }}</div>
                  <div>评分：{{ evaluation.rating }}</div>
                  <div>评价：{{ evaluation.comment }}</div>
                  <div>评价内容：{{ evaluation.evaluationContent }}</div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>

          <!-- 审核历史 -->
          <div class="section">
            <h3>审核历史</h3>
            <el-timeline>
              <el-timeline-item
                v-for="audit in auditHistory"
                :key="audit.auditId"
                :timestamp="formatDateTime(audit.auditTime)"
                :type="audit.isApproved ? 'success' : 'danger'"
              >
                <el-card>
                  <div>审核人：{{ audit.adminId }}</div>
                  <div>结果：{{ audit.auditResult }}</div>
                  <div>意见：{{ audit.auditComment }}</div>
                  <div>状态：{{ audit.isApproved ? '通过' : '拒绝' }}</div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>

          <!-- 审核表单 -->
          <div class="section">
            <h3>审核意见</h3>
            <el-form :model="auditForm" label-width="100px">
              <el-form-item label="审核结果">
                <el-radio-group v-model="auditForm.isApproved">
                  <el-radio :label="1">通过</el-radio>
                  <el-radio :label="0">拒绝</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="审核说明">
                <el-input
                  v-model="auditForm.auditResult"
                  type="textarea"
                  rows="2"
                  placeholder="请输入审核结果说明"
                />
              </el-form-item>
              <el-form-item label="审核意见">
                <el-input
                  v-model="auditForm.auditComment"
                  type="textarea"
                  rows="4"
                  placeholder="请输入详细审核意见"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitAudit">提交审核</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<style scoped>
.job-audit-container {
  padding: 20px;
}

.drawer-content {
  padding: 20px;
}

.section {
  margin-bottom: 30px;
}

.section h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.el-timeline {
  max-height: 300px;
  overflow-y: auto;
}

:deep(.el-drawer__body) {
  padding: 0;
}

:deep(.el-timeline-item__content) {
  width: 100%;
}

:deep(.el-card) {
  margin-right: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>