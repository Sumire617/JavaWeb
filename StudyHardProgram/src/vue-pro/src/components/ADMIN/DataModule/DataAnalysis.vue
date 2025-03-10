<template>
	<div class="data-analysis">
		<h1>数据分析</h1>
		<!-- 筛选条件 -->
		<el-date-picker
				v-model="dateRange"
				type="daterange"
				range-separator="至"
				start-placeholder="开始日期"
				end-placeholder="结束日期">
		</el-date-picker>
		<el-select v-model="dataType" placeholder="选择数据类型">
			<el-option label="岗位数量" value="jobCount"></el-option>
			<el-option label="用户活跃度" value="userActivity"></el-option>
		</el-select>
		<el-button @click="analyzeData">分析数据</el-button>
		<!-- 数据图表 -->
		<div id="chart" style="width: 100%; height: 400px;"></div>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';

// 日期范围
const dateRange = ref([]);
// 数据类型
const dataType = ref('jobCount');
// 图表实例
const chart = ref(null);

// 分析数据
const analyzeData = () => {
	// 这里后续会调用后端接口，暂时留空
	// 模拟数据更新图表
	const option = {
		xAxis: {
			type: 'category',
			data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
		},
		yAxis: {
			type: 'value'
		},
		series: [{
			data: [120, 200, 150, 80, 70, 110, 130],
			type: 'bar'
		}]
	};
	chart.value.setOption(option);
};

onMounted(() => {
	// 初始化图表
	chart.value = echarts.init(document.getElementById('chart'));
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

.el-date-picker {
	margin-right: 10px;
}

.el-select {
	width: 150px;
	margin-right: 10px;
}

#chart {
	margin-top: 20px;
}
</style>