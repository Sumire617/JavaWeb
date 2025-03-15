<template>
	<div class="data-analysis">
		<h1>数据分析</h1>
		
		<!-- 概览卡片 -->
		<el-row :gutter="20" class="overview-cards">
			<el-col :span="6" v-for="(item, index) in overviewData" :key="index">
				<el-card shadow="hover">
					<div class="card-title">{{ item.title }}</div>
					<div class="card-value">{{ item.value }}</div>
				</el-card>
			</el-col>
		</el-row>
		
		<!-- 筛选条件 -->
		<div class="filter-section">
			<el-date-picker
					v-model="dateRange"
					type="daterange"
					range-separator="至"
					start-placeholder="开始日期"
					end-placeholder="结束日期"
					value-format="YYYY-MM-DD"
					format="YYYY-MM-DD"
					:disabled="loading">
			</el-date-picker>
			<el-select v-model="dataType" placeholder="选择数据类型" :disabled="loading">
				<el-option label="岗位数量" value="jobCount"></el-option>
				<el-option label="用户活跃度" value="userActivity"></el-option>
			</el-select>
			<el-select v-model="timeInterval" placeholder="选择时间间隔" :disabled="loading">
				<el-option label="按天" value="day"></el-option>
				<el-option label="按周" value="week"></el-option>
				<el-option label="按月" value="month"></el-option>
			</el-select>
			<el-button @click="analyzeData" type="primary" :loading="loading">分析数据</el-button>
		</div>
		
		<!-- 主数据图表 -->
		<el-card shadow="hover" class="chart-card">
			<div slot="header" class="chart-header">
				<span>{{ chartTitle }}</span>
			</div>
			<div id="mainChart" style="width: 100%; height: 400px;"></div>
		</el-card>
		
		<!-- 补充图表 -->
		<el-row :gutter="20" class="chart-row">
			<el-col :span="12">
				<el-card shadow="hover" class="chart-card">
					<div slot="header" class="chart-header">
						<span>岗位状态分布</span>
					</div>
					<div id="statusChart" style="width: 100%; height: 300px;"></div>
				</el-card>
			</el-col>
			<el-col :span="12">
				<el-card shadow="hover" class="chart-card">
					<div slot="header" class="chart-header">
						<span>岗位地区分布</span>
					</div>
					<div id="locationChart" style="width: 100%; height: 300px;"></div>
				</el-card>
			</el-col>
		</el-row>
	</div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';
import { ElMessage } from 'element-plus';

// 响应式数据
const dateRange = ref([]);
const dataType = ref('jobCount');
const timeInterval = ref('day');
const loading = ref(false);
const overviewData = ref([]);
const chartData = ref({
	labels: [],
	data: [],
	total: 0
});
const statusData = ref({
	labels: ['待审核', '已通过', '已拒绝'],
	data: [0, 0, 0],
	total: 0
});
const locationData = ref({
	locations: [],
	counts: []
});

// 图表实例
const mainChart = ref(null);
const statusChart = ref(null);
const locationChart = ref(null);

// 计算属性
const chartTitle = computed(() => {
	if (dataType.value === 'jobCount') {
		return '岗位发布趋势分析';
	} else if (dataType.value === 'userActivity') {
		return '用户活跃度分析';
	}
	return '数据分析';
});

// 获取概览数据
const fetchOverviewData = async () => {
	try {
		const response = await axios.get('/api/data-analysis/overview');
		const data = response.data;
		
		overviewData.value = [
			{ title: '总岗位数', value: data.totalJobs || 0 },
			{ title: '活跃岗位数', value: data.activeJobs || 0 },
			{ title: '总申请数', value: data.totalApplications || 0 },
			{ title: '总用户数', value: data.totalUsers || 0 }
		];
	} catch (error) {
		ElMessage.error('获取概览数据失败');
		console.error(error);
	}
};

// 获取岗位状态统计
const fetchStatusStats = async () => {
	try {
		const response = await axios.get('/api/data-analysis/job-status');
		const data = response.data;
		
		statusData.value = {
			labels: ['待审核', '已通过', '已拒绝'],
			data: [data.pending || 0, data.approved || 0, data.rejected || 0],
			total: data.total || 0
		};
		
		updateStatusChart();
	} catch (error) {
		ElMessage.error('获取岗位状态统计失败');
		console.error(error);
	}
};

// 获取岗位地区分布
const fetchLocationDistribution = async () => {
	try {
		const response = await axios.get('/api/data-analysis/location-distribution');
		locationData.value = response.data;
		
		updateLocationChart();
	} catch (error) {
		ElMessage.error('获取岗位地区分布失败');
		console.error(error);
	}
};

// 分析数据
const analyzeData = async () => {
	if (!dateRange.value || dateRange.value.length !== 2) {
		ElMessage.warning('请选择日期范围');
		return;
	}
	
	loading.value = true;
	
	try {
		const startDate = dateRange.value[0];
		const endDate = dateRange.value[1];
		
		let url = '';
		if (dataType.value === 'jobCount') {
			url = `/api/data-analysis/job-count?startDate=${startDate}&endDate=${endDate}&interval=${timeInterval.value}`;
		} else if (dataType.value === 'userActivity') {
			url = `/api/data-analysis/user-activity?startDate=${startDate}&endDate=${endDate}&interval=${timeInterval.value}`;
		}
		
		const response = await axios.get(url);
		chartData.value = response.data;
		
		updateMainChart();
	} catch (error) {
		ElMessage.error('分析数据失败');
		console.error(error);
	} finally {
		loading.value = false;
	}
};

// 初始化图表
const initCharts = () => {
	// 主图表
	mainChart.value = echarts.init(document.getElementById('mainChart'));
	
	// 状态图表
	statusChart.value = echarts.init(document.getElementById('statusChart'));
	
	// 地区图表
	locationChart.value = echarts.init(document.getElementById('locationChart'));
	
	// 设置默认配置
	updateMainChart();
	updateStatusChart();
	updateLocationChart();
	
	// 监听窗口大小变化
	window.addEventListener('resize', () => {
		mainChart.value.resize();
		statusChart.value.resize();
		locationChart.value.resize();
	});
};

// 更新主图表
const updateMainChart = () => {
	if (!mainChart.value) return;
	
	const option = {
		title: {
			text: chartTitle.value,
			left: 'center'
		},
		tooltip: {
			trigger: 'axis',
			axisPointer: {
				type: 'shadow'
			}
		},
		grid: {
			left: '3%',
			right: '4%',
			bottom: '3%',
			containLabel: true
		},
		xAxis: {
			type: 'category',
			data: chartData.value.labels || []
		},
		yAxis: {
			type: 'value'
		},
		series: [
			{
				name: dataType.value === 'jobCount' ? '岗位数量' : '活跃用户数',
				type: 'bar',
				data: chartData.value.data || [],
				itemStyle: {
					color: '#409EFF'
				}
			}
		]
	};
	
	mainChart.value.setOption(option);
};

// 更新状态图表
const updateStatusChart = () => {
	if (!statusChart.value) return;
	
	const option = {
		tooltip: {
			trigger: 'item',
			formatter: '{a} <br/>{b} : {c} ({d}%)'
		},
		legend: {
			orient: 'horizontal',
			bottom: 10,
			data: statusData.value.labels
		},
		series: [
			{
				name: '岗位状态',
				type: 'pie',
				radius: '55%',
				center: ['50%', '45%'],
				data: statusData.value.labels.map((label, index) => ({
					name: label,
					value: statusData.value.data[index]
				})),
				emphasis: {
					itemStyle: {
						shadowBlur: 10,
						shadowOffsetX: 0,
						shadowColor: 'rgba(0, 0, 0, 0.5)'
					}
				}
			}
		]
	};
	
	statusChart.value.setOption(option);
};

// 更新地区图表
const updateLocationChart = () => {
	if (!locationChart.value) return;
	
	const option = {
		tooltip: {
			trigger: 'axis',
			axisPointer: {
				type: 'shadow'
			}
		},
		grid: {
			left: '3%',
			right: '4%',
			bottom: '3%',
			containLabel: true
		},
		xAxis: {
			type: 'value'
		},
		yAxis: {
			type: 'category',
			data: locationData.value.locations || [],
			axisLabel: {
				interval: 0,
				rotate: 30
			}
		},
		series: [
			{
				name: '岗位数量',
				type: 'bar',
				data: locationData.value.counts || [],
				itemStyle: {
					color: '#67C23A'
				}
			}
		]
	};
	
	locationChart.value.setOption(option);
};

// 初始日期设置为过去30天
const initDateRange = () => {
	const endDate = new Date();
	const startDate = new Date();
	startDate.setDate(startDate.getDate() - 30);
	
	const formatDate = (date) => {
		const year = date.getFullYear();
		const month = (date.getMonth() + 1).toString().padStart(2, '0');
		const day = date.getDate().toString().padStart(2, '0');
		return `${year}-${month}-${day}`;
	};
	
	dateRange.value = [formatDate(startDate), formatDate(endDate)];
};

// 监听数据类型变化
watch(dataType, () => {
	if (dateRange.value && dateRange.value.length === 2) {
		analyzeData();
	}
});

// 监听时间间隔变化
watch(timeInterval, () => {
	if (dateRange.value && dateRange.value.length === 2) {
		analyzeData();
	}
});

onMounted(async () => {
	// 初始化日期范围
	initDateRange();
	
	// 初始化图表
	initCharts();
	
	// 获取概览数据
	await fetchOverviewData();
	
	// 获取状态统计
	await fetchStatusStats();
	
	// 获取地区分布
	await fetchLocationDistribution();
	
	// 初始分析
	analyzeData();
});
</script>

<style scoped>
.data-analysis {
	padding: 20px;
}

h1 {
	margin-bottom: 20px;
}

.overview-cards {
	margin-bottom: 20px;
}

.card-title {
	font-size: 14px;
	color: #606266;
}

.card-value {
	font-size: 24px;
	font-weight: bold;
	color: #409EFF;
	margin-top: 10px;
}

.filter-section {
	margin-bottom: 20px;
	display: flex;
	align-items: center;
}

.el-date-picker {
	margin-right: 10px;
}

.el-select {
	width: 150px;
	margin-right: 10px;
}

.chart-card {
	margin-bottom: 20px;
}

.chart-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.chart-row {
	margin-bottom: 20px;
}
</style>