<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import * as echarts from 'echarts';

// 响应式数据
const loading = ref(false);
const overview = ref({
  totalJobs: 0,
  totalApplications: 0,
  passRate: 0,
  completionRate: 0
});

// 图表实例
const trendChart = ref(null);
const jobTypeChart = ref(null);
const salaryChart = ref(null);
const locationChart = ref(null);
const efficiencyChart = ref(null);

// 时间范围
const dateRange = ref([]);

// 获取数据概览
const fetchOverview = async () => {
  try {
    const response = await axios.get('/api/employer/analysis/overview');
    overview.value = response.data;
  } catch (error) {
    ElMessage.error('获取数据概览失败');
  }
};

// 初始化趋势图表
const initTrendChart = () => {
  const chart = echarts.init(document.getElementById('trendChart'));
  chart.setOption({
    title: { text: '招聘趋势分析' },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['职位发布数', '申请数量', '面试通过数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '职位发布数',
        type: 'line',
        data: [10, 15, 12, 18, 20, 25],
        smooth: true
      },
      {
        name: '申请数量',
        type: 'line',
        data: [50, 80, 65, 90, 100, 120],
        smooth: true
      },
      {
        name: '面试通过数',
        type: 'line',
        data: [20, 30, 25, 35, 40, 45],
        smooth: true
      }
    ]
  });
  trendChart.value = chart;
};

// 初始化职位类型分布图
const initJobTypeChart = () => {
  const chart = echarts.init(document.getElementById('jobTypeChart'));
  chart.setOption({
    title: { text: '职位类型分布' },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: '技术' },
          { value: 20, name: '销售' },
          { value: 15, name: '运营' },
          { value: 10, name: '市场' },
          { value: 20, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  });
  jobTypeChart.value = chart;
};

// 初始化薪资分布图
const initSalaryChart = () => {
  const chart = echarts.init(document.getElementById('salaryChart'));
  chart.setOption({
    title: { text: '薪资范围分布' },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: ['3k-5k', '5k-8k', '8k-12k', '12k-15k', '15k-20k', '20k以上']
    },
    yAxis: {
      type: 'value',
      name: '职位数量'
    },
    series: [
      {
        type: 'bar',
        data: [5, 15, 25, 20, 10, 5],
        barWidth: '60%'
      }
    ]
  });
  salaryChart.value = chart;
};

// 初始化地区分布图
const initLocationChart = () => {
  const chart = echarts.init(document.getElementById('locationChart'));
  chart.setOption({
    title: { text: '工作地区分布' },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '20',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 40, name: '北京' },
          { value: 30, name: '上海' },
          { value: 20, name: '广州' },
          { value: 10, name: '深圳' },
          { value: 20, name: '其他' }
        ]
      }
    ]
  });
  locationChart.value = chart;
};

// 初始化招聘效率图
const initEfficiencyChart = () => {
  const chart = echarts.init(document.getElementById('efficiencyChart'));
  chart.setOption({
    title: { text: '招聘效率分析' },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['平均周期(天)', '转化率(%)']
    },
    xAxis: {
      type: 'category',
      data: ['技术', '销售', '运营', '市场', '其他']
    },
    yAxis: [
      {
        type: 'value',
        name: '天数',
        min: 0,
        max: 30
      },
      {
        type: 'value',
        name: '百分比',
        min: 0,
        max: 100
      }
    ],
    series: [
      {
        name: '平均周期(天)',
        type: 'bar',
        data: [15, 10, 12, 8, 11]
      },
      {
        name: '转化率(%)',
        type: 'line',
        yAxisIndex: 1,
        data: [45, 60, 55, 50, 48]
      }
    ]
  });
  efficiencyChart.value = chart;
};

// 处理时间范围变化
const handleDateRangeChange = () => {
  // 根据新的时间范围更新图表数据
  // TODO: 实现数据更新逻辑
};

// 处理窗口大小变化
const handleResize = () => {
  trendChart.value?.resize();
  jobTypeChart.value?.resize();
  salaryChart.value?.resize();
  locationChart.value?.resize();
  efficiencyChart.value?.resize();
};

onMounted(() => {
  fetchOverview();
  initTrendChart();
  initJobTypeChart();
  initSalaryChart();
  initLocationChart();
  initEfficiencyChart();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  trendChart.value?.dispose();
  jobTypeChart.value?.dispose();
  salaryChart.value?.dispose();
  locationChart.value?.dispose();
  efficiencyChart.value?.dispose();
});
</script>

<template>
  <div class="data-analysis">
    <!-- 数据概览 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总职位数</span>
              <el-icon><Briefcase /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ overview.totalJobs }}</div>
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
          <div class="card-value">{{ overview.totalApplications }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>面试通过率</span>
              <el-icon><TrendCharts /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ overview.passRate }}%</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>招聘完成率</span>
              <el-icon><CircleCheck /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ overview.completionRate }}%</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 时间范围选择 -->
    <div class="filter-section">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        @change="handleDateRangeChange"
      />
    </div>

    <!-- 趋势分析 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="never">
          <div id="trendChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分类统计 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="never">
          <div id="jobTypeChart" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <div id="salaryChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 地区分布和招聘效率 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="never">
          <div id="locationChart" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <div id="efficiencyChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.data-analysis {
  padding: 20px;
}

.overview-cards {
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

.filter-section {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.chart-row {
  margin-bottom: 20px;
}

.chart {
  height: 400px;
  width: 100%;
}

:deep(.el-card__header) {
  padding: 12px 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style> 