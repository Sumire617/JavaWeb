<template>
	<div class="applications-container">
		<!-- 顶部导航栏 -->
		<el-header class="header">
			<div class="header-left">

			</div>
			<div class="header-right">
				<el-button type="primary" plain @click="router.push('/Home')">返回首页</el-button>
			</div>
		</el-header>

		<el-card class="applications-card">
			<template #header>
				<div class="card-header">
					<h2>我的申请</h2>
					<div class="header-right">
						<el-select v-model="statusFilter" placeholder="申请状态" clearable>
							<el-option label="待处理" value="PENDING" />
							<el-option label="已通过" value="APPROVED" />
							<el-option label="已拒绝" value="REJECTED" />
						</el-select>
					</div>
				</div>
			</template>
			
			<div class="applications-list" v-loading="loading">
				<el-empty v-if="applications.length === 0" description="暂无申请记录">
					<template #description>
						<span>暂无申请记录</span>
					</template>
					<el-button type="primary" @click="router.push('/Home')">
						前往浏览岗位
					</el-button>
				</el-empty>
				
				<el-timeline v-else>
					<el-timeline-item
						v-for="application in applications"
						:key="application.applicationId"
						:type="getStatusType(application.status)"
						:timestamp="formatDateTime(application.applyTime)"
					>
						<el-card class="application-card">
							<div class="application-header">
								<h3>{{ application.jobPost.jobTitle }}</h3>
								<el-tag :type="getStatusTagType(application.status)">
									{{ getStatusText(application.status) }}
								</el-tag>
							</div>
							
							<div class="application-info">
								<p><strong>申请时间：</strong>{{ formatDateTime(application.applyTime) }}</p>
								<p><strong>工作地点：</strong>{{ application.jobPost.location }}</p>
								<p><strong>薪资范围：</strong>{{ application.jobPost.salaryRange }}/小时</p>
								<p><strong>工作时间：</strong>{{ application.jobPost.workSchedule }}</p>
							</div>
							
							<div v-if="application.reviewComment" class="review-comment">
								<p><strong>审核意见：</strong>{{ application.reviewComment }}</p>
								<p><strong>审核时间：</strong>{{ formatDateTime(application.reviewTime) }}</p>
							</div>
							
							<div class="application-actions">
								<el-button
									type="primary"
									link
									@click="viewJobDetail(application.jobPost.jobPostId)"
								>
									查看职位详情
								</el-button>
								<el-button
									v-if="application.status === 'APPROVED'"
									type="success"
									link
									@click="handleReview(application)"
								>
									评价
								</el-button>
							</div>
						</el-card>
					</el-timeline-item>
				</el-timeline>
			</div>
			
			<div class="pagination-container">
				<el-pagination
					v-model:current-page="pagination.currentPage"
					:page-size="pagination.pageSize"
					:total="pagination.total"
					layout="prev, pager, next, jumper"
					@current-change="handleCurrentChange"
				/>
			</div>
		</el-card>
		
		<!-- 评价对话框 -->
		<el-dialog
			v-model="reviewDialogVisible"
			title="评价"
			width="500px"
		>
			<el-form
				ref="reviewFormRef"
				:model="reviewForm"
				:rules="reviewRules"
				label-width="80px"
			>
				<el-form-item label="评分" prop="rating">
					<el-rate v-model="reviewForm.rating" />
				</el-form-item>
				<el-form-item label="评价内容" prop="comment">
					<el-input
						v-model="reviewForm.comment"
						type="textarea"
						:rows="4"
						placeholder="请输入您的评价内容"
					/>
				</el-form-item>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="reviewDialogVisible = false">取消</el-button>
					<el-button type="primary" @click="submitReview">提交评价</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const loading = ref(false);
const applications = ref([]);
const statusFilter = ref('');
const reviewDialogVisible = ref(false);
const reviewFormRef = ref(null);
const currentApplication = ref(null);

const pagination = ref({
	currentPage: 1,
	pageSize: 10,
	total: 0
});

const reviewForm = ref({
	rating: 0,
	comment: ''
});

const reviewRules = {
	rating: [
		{ required: true, message: '请选择评分', trigger: 'change' }
	],
	comment: [
		{ required: true, message: '请输入评价内容', trigger: 'blur' },
		{ min: 10, max: 500, message: '长度在 10 到 500 个字符', trigger: 'blur' }
	]
};

// 获取申请列表
const fetchApplications = async () => {
	loading.value = true;
	try {
		const userStr = localStorage.getItem('user');
		if (!userStr) {
			ElMessage.error('请先登录');
			router.push('/');
			return;
		}
		const user = JSON.parse(userStr);
		
		console.log('获取用户申请列表，用户ID:', user.userId);
		
		// 创建带有超时的请求配置
		const config = {
			params: {
				page: pagination.value.currentPage - 1,
				size: pagination.value.pageSize,
				status: statusFilter.value || null
			},
			timeout: 10000 // 设置10秒超时
		};
		
		// 获取用户的申请列表 - 使用相对路径
		const response = await axios.get(`/api/applications/user/${user.userId}`, config);
		
		console.log('申请列表数据:', response.data);
		
		if (response.data.content) {
			applications.value = response.data.content;
			pagination.value.total = response.data.totalElements;
		} else if (Array.isArray(response.data)) {
			// 如果返回的是数组而不是分页对象
			applications.value = response.data;
			pagination.value.total = response.data.length;
		} else {
			applications.value = [];
			pagination.value.total = 0;
			console.warn('返回的数据格式不符合预期:', response.data);
		}
	} catch (error) {
		console.error('获取申请列表失败:', error);
		applications.value = [];
		pagination.value.total = 0;
		
		if (error.response) {
			console.error('错误状态码:', error.response.status);
			console.error('错误数据:', error.response.data);
			
			if (error.response.status === 500) {
				ElMessage.error('服务器内部错误，请联系管理员');
				console.error('服务器内部错误详情:', error.response.data);
			} else if (error.response.status === 404) {
				ElMessage.error('未找到数据，可能您还没有申请记录');
			} else if (error.response.status === 401) {
				ElMessage.error('登录已过期，请重新登录');
				router.push('/');
			} else {
				ElMessage.error(`获取申请列表失败: ${error.response.data.message || '未知错误'}`);
			}
		} else if (error.request) {
			// 请求已发送但没有收到响应
			console.error('无响应:', error.request);
			ElMessage.error('服务器没有响应，请检查后端服务是否启动');
		} else {
			// 请求设置时发生错误
			console.error('请求错误:', error.message);
			ElMessage.error(`请求错误: ${error.message}`);
		}
	} finally {
		loading.value = false;
	}
};

// 状态相关方法
const getStatusType = (status) => {
	switch (status) {
		case 'PENDING': return 'warning';
		case 'APPROVED': return 'success';
		case 'REJECTED': return 'danger';
		default: return 'info';
	}
};

const getStatusTagType = (status) => {
	switch (status) {
		case 'PENDING': return 'warning';
		case 'APPROVED': return 'success';
		case 'REJECTED': return 'danger';
		default: return 'info';
	}
};

const getStatusText = (status) => {
	switch (status) {
		case 'PENDING': return '待处理';
		case 'APPROVED': return '已通过';
		case 'REJECTED': return '已拒绝';
		default: return '未知';
	}
};

// 处理评价
const handleReview = (application) => {
	currentApplication.value = application;
	reviewDialogVisible.value = true;
};

// 提交评价
const submitReview = async () => {
	if (!reviewFormRef.value) return;
	
	try {
		await reviewFormRef.value.validate();
		
		const userStr = localStorage.getItem('user');
		if (!userStr) {
			ElMessage.error('请先登录');
			return;
		}
		const user = JSON.parse(userStr);
		
		await axios.post(`/api/reviews`, {
			jobPostId: currentApplication.value.jobPost.jobPostId,
			userId: user.userId,
			rating: reviewForm.value.rating,
			comment: reviewForm.value.comment
		});
		
		ElMessage.success('评价提交成功');
		reviewDialogVisible.value = false;
		reviewFormRef.value.resetFields();
	} catch (error) {
		console.error('提交评价失败:', error);
		ElMessage.error('提交评价失败');
	}
};

// 查看职位详情
const viewJobDetail = (jobId) => {
	router.push(`/job/${jobId}`);
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

// 分页处理
const handleCurrentChange = (val) => {
	pagination.value.currentPage = val;
	fetchApplications();
};

// 监听状态筛选变化
watch(statusFilter, () => {
	pagination.value.currentPage = 1;
	fetchApplications();
});

onMounted(() => {
	fetchApplications();
});
</script>

<style scoped>
.applications-container {
	max-width: 1000px;
	margin: 20px auto;
	padding: 0 20px;
}

.header {
	background-color: white;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 0 20px;
	height: 60px;
	margin-bottom: 20px;
	border-radius: 4px;
}

.header-left h2 {
	margin: 0;
	color: #303133;
	font-size: 1.5em;
}

.applications-card {
	margin-top: 20px;
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.card-header h2 {
	margin: 0;
}

.application-card {
	margin-bottom: 10px;
}

.application-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15px;
}

.application-header h3 {
	margin: 0;
}

.application-info {
	margin-bottom: 15px;
}

.application-info p {
	margin: 5px 0;
}

.review-comment {
	margin-top: 15px;
	padding-top: 15px;
	border-top: 1px solid #eee;
}

.review-comment p {
	margin: 5px 0;
}

.application-actions {
	margin-top: 15px;
	text-align: right;
}

.pagination-container {
	margin-top: 20px;
	text-align: center;
}
</style> 