<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import * as echarts from 'echarts';

const jobStats = ref({
  totalJobs: 0,
  activeJobs: 0,
  totalApplications: 0,
  pendingReviews: 0
});

const recentActivities = ref([]);
const applicantChart = ref(null);
const jobTrendChart = ref(null);

onMounted(() => {
  fetchDashboardData();
  initCharts();
});

const fetchDashboardData = async () => {
  try {
    const response = await axios.get('/api/jobs/dashboard');
    jobStats.value = response.data.stats;
    recentActivities.value = response.data.activities;
  } catch (error) {
    console.error('获取数据失败:', error);
  }
};

const initCharts = () => {
  // 初始化应聘者分布图表
  const applicantChartDom = document.getElementById('applicantChart');
  if (applicantChartDom) {
    applicantChart.value = echarts.init(applicantChartDom);
    applicantChart.value.setOption({
      title: { text: '应聘者学历分布' },
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: '本科' },
          { value: 25, name: '硕士' },
          { value: 20, name: '专科' },
          { value: 15, name: '博士' },
          { value: 5, name: '其他' }
        ]
      }]
    });
  }

  // 初始化岗位趋势图表
  const jobTrendChartDom = document.getElementById('jobTrendChart');
  if (jobTrendChartDom) {
    jobTrendChart.value = echarts.init(jobTrendChartDom);
    jobTrendChart.value.setOption({
      title: { text: '近期岗位申请趋势' },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: { type: 'value' },
      series: [{
        data: [120, 132, 101, 134, 90, 230, 210],
        type: 'line',
        smooth: true
      }]
    });
  }
};
</script>

<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总岗位数</span>
              <el-icon><Briefcase /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ jobStats.totalJobs }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>在招岗位</span>
              <el-icon><Document /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ jobStats.activeJobs }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总申请数</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ jobStats.totalApplications }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待审核</span>
              <el-icon><Timer /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ jobStats.pendingReviews }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-container">
      <el-col :span="12">
        <el-card shadow="hover">
          <div id="applicantChart" style="height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div id="jobTrendChart" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-card shadow="hover" class="recent-activities">
      <template #header>
        <div class="card-header">
          <span>最近活动</span>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="(activity, index) in recentActivities"
          :key="index"
          :timestamp="activity.time"
          :type="activity.type"
        >
          {{ activity.content }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
}

.charts-container {
  margin-bottom: 20px;
}

.recent-activities {
  margin-top: 20px;
}

:deep(.el-card) {
  margin-bottom: 20px;
}

:deep(.el-timeline-item__content) {
  color: #606266;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #EBEEF5;
  font-weight: bold;
}
</style> 