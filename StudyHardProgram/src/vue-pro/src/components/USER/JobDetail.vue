<template>
	<div class="job-detail">
		<el-card class="job-info-card" v-loading="loading">
			<!-- 岗位基本信息 -->
			<div class="job-header">
				<div class="title-section">
					<h1>{{ jobDetail.jobTitle }}</h1>
					<div class="salary">{{ jobDetail.salaryRange }}/小时</div>
				</div>
				<div class="company-section">
					<div class="job-meta">
						<span><i class="el-icon-location"></i>{{ jobDetail.location }}</span>
						<span><i class="el-icon-time"></i>{{ jobDetail.workSchedule }}</span>
					</div>
				</div>
				<div class="tags-section">
					<el-tag size="small" type="success">{{ jobDetail.jobType }}</el-tag>
					<el-tag size="small" type="warning">{{ jobDetail.requirements }}</el-tag>
					<el-tag size="small" type="info">截止日期：{{ formatDate(jobDetail.deadline) }}</el-tag>
				</div>
			</div>

			<!-- 岗位详细信息 -->
			<el-divider>岗位详情</el-divider>
			<div class="job-content">
				<div class="section">
					<h3>岗位描述</h3>
					<div class="content-text">{{ jobDetail.jobDescription }}</div>
				</div>
				<div class="section">
					<h3>任职要求</h3>
					<div class="content-text">{{ jobDetail.requirements }}</div>
				</div>
				<div class="section">
					<h3>福利待遇</h3>
					<div class="content-text">{{ jobDetail.benefits }}</div>
				</div>
			</div>

			<!-- 申请按钮 -->
			<div class="action-section">
				<el-button type="primary" size="large" @click="handleApply" :disabled="jobDetail.status !== 'active'">
					立即申请
				</el-button>
			</div>
		</el-card>

		<!-- 评价区域 -->
		<el-card class="reviews-card">
			<template #header>
				<div class="reviews-header">
					<div class="rating-overview">
						<div class="average-rating">
							<span class="rating-number">{{ averageRating }}</span>
							<el-rate
								v-model="averageRating"
								disabled
								show-score
								text-color="#ff9900"
								score-template=""
							/>
						</div>
						<div class="rating-count">共 {{ reviews.length }} 条评价</div>
					</div>
					<el-button type="primary" @click="showReviewDialog" v-if="canReview">
						写评价
					</el-button>
				</div>
			</template>

			<!-- 评价列表 -->
			<div class="reviews-list">
				<div v-if="reviews.length === 0" class="no-reviews">
					暂无评价
				</div>
				<div v-else class="review-item" v-for="review in reviews" :key="review.id">
					<div class="review-header">
						<div class="reviewer-info">
							<el-avatar :size="32" :src="review.userAvatar">{{ review.userName.charAt(0) }}</el-avatar>
							<span class="reviewer-name">{{ review.userName }}</span>
						</div>
						<div class="review-rating">
							<el-rate v-model="review.rating" disabled />
							<span class="review-time">{{ formatDateTime(review.reviewTime) }}</span>
						</div>
					</div>
					<div class="review-content">{{ review.content }}</div>
				</div>
			</div>

			<!-- 分页 -->
			<div class="pagination" v-if="reviews.length > 0">
				<el-pagination
					v-model:current-page="pagination.currentPage"
					:page-size="pagination.pageSize"
					:total="pagination.total"
					layout="prev, pager, next"
					@current-change="handlePageChange"
				/>
			</div>
		</el-card>

		<!-- 评价对话框 -->
		<el-dialog
			v-model="reviewDialogVisible"
			title="岗位评价"
			width="500px"
		>
			<el-form
				ref="reviewFormRef"
				:model="reviewForm"
				:rules="reviewRules"
				label-width="80px"
			>
				<el-form-item label="评分" prop="rating">
					<el-rate
						v-model="reviewForm.rating"
						show-text
						:texts="['很差', '较差', '一般', '不错', '很好']"
					/>
				</el-form-item>
				<el-form-item label="评价内容" prop="content">
					<el-input
						v-model="reviewForm.content"
						type="textarea"
						:rows="4"
						placeholder="请分享您的工作体验"
					/>
				</el-form-item>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="reviewDialogVisible = false">取消</el-button>
					<el-button type="primary" @click="submitReview">
						提交评价
					</el-button>
				</span>
			</template>
		</el-dialog>

		<!-- 申请对话框 -->
		<el-dialog
			v-model="applyDialogVisible"
			title="岗位申请"
			width="500px"
		>
			<el-form
				ref="applyFormRef"
				:model="applyForm"
				:rules="applyRules"
				label-width="100px"
			>
				<el-form-item label="姓名" prop="name">
					<el-input v-model="applyForm.name" />
				</el-form-item>
				<el-form-item label="学号" prop="studentId">
					<el-input v-model="applyForm.studentId" />
				</el-form-item>
				<el-form-item label="联系电话" prop="phone">
					<el-input v-model="applyForm.phone" />
				</el-form-item>
				<el-form-item label="电子邮箱" prop="email">
					<el-input v-model="applyForm.email" />
				</el-form-item>
				<el-form-item label="自我介绍" prop="introduction">
					<el-input
						v-model="applyForm.introduction"
						type="textarea"
						:rows="4"
						placeholder="请简要介绍自己的情况和申请原因"
					/>
				</el-form-item>
				<el-form-item label="简历" prop="resume">
					<el-upload
						class="resume-upload"
						action="/api/upload/resume"
						:before-upload="beforeResumeUpload"
						:on-success="handleResumeSuccess"
						:on-remove="handleResumeRemove"
						:limit="1"
					>
						<el-button type="primary">
							<el-icon><Upload /></el-icon>
							<span>上传简历</span>
						</el-button>
						<template #tip>
							<div class="el-upload__tip">
								支持 PDF、Word 格式，大小不超过 10MB
							</div>
						</template>
					</el-upload>
				</el-form-item>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="applyDialogVisible = false">取消</el-button>
					<el-button type="primary" @click="submitApplication">
						提交申请
					</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const route = useRoute();
const loading = ref(true);
const jobDetail = ref({});
const reviews = ref([]);
const averageRating = computed(() => {
	if (reviews.value.length === 0) return 0;
	const sum = reviews.value.reduce((acc, curr) => acc + curr.rating, 0);
	return (sum / reviews.value.length).toFixed(1);
});

// 评价相关数据
const reviewDialogVisible = ref(false);
const reviewFormRef = ref(null);
const reviewForm = ref({
	rating: 0,
	content: ''
});

const reviewRules = {
	rating: [
		{ required: true, message: '请选择评分', trigger: 'change' },
		{ type: 'number', min: 1, message: '请选择评分', trigger: 'change' }
	],
	content: [
		{ required: true, message: '请输入评价内容', trigger: 'blur' },
		{ min: 10, message: '评价内容不能少于10个字符', trigger: 'blur' }
	]
};

// 申请相关数据
const applyDialogVisible = ref(false);
const applyFormRef = ref(null);
const applyForm = ref({
	name: '',
	studentId: '',
	phone: '',
	email: '',
	introduction: '',
	resume: ''
});

const applyRules = {
	name: [
		{ required: true, message: '请输入姓名', trigger: 'blur' },
		{ min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
	],
	studentId: [
		{ required: true, message: '请输入学号', trigger: 'blur' },
		{ pattern: /^\d{8,12}$/, message: '请输入正确的学号', trigger: 'blur' }
	],
	phone: [
		{ required: true, message: '请输入手机号', trigger: 'blur' },
		{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
	],
	email: [
		{ required: true, message: '请输入邮箱', trigger: 'blur' },
		{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
	],
	introduction: [
		{ required: true, message: '请输入自我介绍', trigger: 'blur' },
		{ min: 50, message: '自我介绍不能少于50个字符', trigger: 'blur' }
	],
	resume: [
		{ required: true, message: '请上传简历', trigger: 'change' }
	]
};

// 分页数据
const pagination = ref({
	currentPage: 1,
	pageSize: 10,
	total: 0
});

// 是否可以评价（已申请且申请通过的用户）
const canReview = ref(false);

// 获取岗位详情
const fetchJobDetail = async () => {
	try {
		loading.value = true;
		const response = await axios.get(`/api/jobs/${route.params.id}`);
		jobDetail.value = response.data;
	} catch (error) {
		ElMessage.error('获取岗位详情失败');
		console.error('获取岗位详情失败:', error);
	} finally {
		loading.value = false;
	}
};

// 获取评价列表
const fetchReviews = async () => {
	try {
		// 使用自定义数据替代API调用
		const mockReviews = [
			{
				evaluationId: 'eval-001',
				jobPostId: route.params.id,
				userId: 'user-001',
				userName: '张三',
				rating: 4,
				comment: '公司环境很好，同事友善，工作有挑战性但压力适中。福利待遇不错，有良好的成长空间。',
				createdAt: '2025-03-10T08:30:00',
				userAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
			},
			{
				evaluationId: 'eval-002',
				jobPostId: route.params.id,
				userId: 'user-002',
				userName: '李四',
				rating: 5,
				comment: '团队氛围很好，技术栈先进，有很多学习成长的机会。管理层关心员工发展，定期有技术分享活动。',
				createdAt: '2025-03-12T14:20:00',
				userAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
			},
			{
				evaluationId: 'eval-003',
				jobPostId: route.params.id,
				userId: 'user-003',
				userName: '王五',
				rating: 3,
				comment: '工作内容与职位描述基本符合，但加班较多。团队协作氛围一般，技术成长空间有限。',
				createdAt: '2025-03-15T09:15:00',
				userAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
			}
		];
		
		reviews.value = mockReviews;
		pagination.value.total = mockReviews.length;
	} catch (error) {
		ElMessage.error('获取评价列表失败');
		console.error('获取评价列表失败:', error);
	}
};

// 检查是否可以评价
const checkCanReview = async () => {
	try {
		const response = await axios.get(`/api/jobs/${route.params.id}/can-review`);
		canReview.value = response.data.canReview;
	} catch (error) {
		console.error('检查评价权限失败:', error);
	}
};

// 显示评价对话框
const showReviewDialog = () => {
	reviewDialogVisible.value = true;
};

// 提交评价
const submitReview = async () => {
	if (!reviewFormRef.value) return;
	
	try {
		await reviewFormRef.value.validate();
		
		const reviewData = {
			jobId: route.params.id,
			...reviewForm.value,
			reviewTime: new Date().toISOString()
		};

		await axios.post(`/api/jobs/${route.params.id}/evaluate`, reviewData);
		ElMessage.success('评价提交成功');
		reviewDialogVisible.value = false;
		reviewFormRef.value.resetFields();
		await fetchReviews();
	} catch (error) {
		if (error.name === 'ValidationError') return;
		ElMessage.error('评价提交失败');
		console.error('评价提交失败:', error);
	}
};

// 处理申请
const handleApply = () => {
	applyDialogVisible.value = true;
};

// 简历上传前验证
const beforeResumeUpload = (file) => {
	const isValidFormat = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type);
	const isLt10M = file.size / 1024 / 1024 < 10;

	if (!isValidFormat) {
		ElMessage.error('简历只能是 PDF 或 Word 格式!');
		return false;
	}
	if (!isLt10M) {
		ElMessage.error('简历大小不能超过 10MB!');
		return false;
	}
	return true;
};

// 简历上传成功
const handleResumeSuccess = (response, file) => {
	applyForm.value.resume = response.url;
	ElMessage.success('简历上传成功');
};

// 移除简历
const handleResumeRemove = () => {
	applyForm.value.resume = '';
};

// 提交申请
const submitApplication = async () => {
	if (!applyFormRef.value) return;
	
	try {
		await applyFormRef.value.validate();
		
		const applicationData = {
			...applyForm.value,
			jobId: route.params.id,
			applyTime: new Date().toISOString()
		};

		await axios.post(`/api/jobs/${route.params.id}/apply`, applicationData);
		ElMessage.success('申请提交成功');
		applyDialogVisible.value = false;
		applyFormRef.value.resetFields();
	} catch (error) {
		if (error.name === 'ValidationError') return;
		console.error('申请提交失败:', error);
	}
};

// 分页改变
const handlePageChange = (page) => {
	pagination.value.currentPage = page;
	fetchReviews();
};

// 格式化日期
const formatDate = (date) => {
	if (!date) return '';
	return new Date(date).toLocaleDateString('zh-CN');
};

// 格式化日期时间
const formatDateTime = (dateTime) => {
	if (!dateTime) return '';
	return new Date(dateTime).toLocaleString('zh-CN');
};

onMounted(() => {
	fetchJobDetail();
	fetchReviews();
	checkCanReview();
});
</script>

<style scoped>
.job-detail {
	max-width: 1200px;
	margin: 20px auto;
	padding: 0 20px;
}

.job-info-card {
	margin-bottom: 20px;
}

.job-header {
	padding-bottom: 20px;
}

.title-section {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15px;
}

.title-section h1 {
	margin: 0;
	font-size: 24px;
	color: #303133;
}

.salary {
	font-size: 24px;
	color: #f56c6c;
	font-weight: bold;
}

.company-section {
	margin-bottom: 15px;
}

.company-name {
	font-size: 16px;
	color: #606266;
	margin-bottom: 8px;
	display: block;
}

.job-meta {
	color: #909399;
	font-size: 14px;
}

.job-meta span {
	margin-right: 20px;
}

.job-meta i {
	margin-right: 4px;
}

.tags-section {
	margin-top: 15px;
}

.tags-section .el-tag {
	margin-right: 8px;
}

.job-content {
	padding: 20px 0;
}

.section {
	margin-bottom: 30px;
}

.section h3 {
	font-size: 16px;
	font-weight: 500;
	color: #303133;
	margin: 0 0 15px;
}

.content-text {
	color: #606266;
	line-height: 1.6;
	white-space: pre-wrap;
}

.action-section {
	text-align: center;
	padding: 20px 0;
}

.reviews-card {
	margin-bottom: 20px;
}

.reviews-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.rating-overview {
	display: flex;
	align-items: center;
}

.average-rating {
	display: flex;
	align-items: center;
	margin-right: 20px;
}

.rating-number {
	font-size: 28px;
	color: #ff9900;
	margin-right: 10px;
}

.rating-count {
	color: #909399;
}

.reviews-list {
	padding: 20px 0;
}

.no-reviews {
	text-align: center;
	color: #909399;
	padding: 40px 0;
}

.review-item {
	padding: 20px 0;
	border-bottom: 1px solid #EBEEF5;
}

.review-item:last-child {
	border-bottom: none;
}

.review-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 10px;
}

.reviewer-info {
	display: flex;
	align-items: center;
}

.reviewer-name {
	margin-left: 10px;
	color: #303133;
}

.review-rating {
	display: flex;
	align-items: center;
}

.review-time {
	margin-left: 10px;
	color: #909399;
	font-size: 12px;
}

.review-content {
	color: #606266;
	line-height: 1.6;
}

.pagination {
	text-align: center;
	margin-top: 20px;
}

.resume-upload {
	text-align: center;
}

:deep(.el-upload-list) {
	text-align: left;
}

.el-upload__tip {
	font-size: 12px;
	color: #909399;
	margin-top: 8px;
}

@media screen and (max-width: 768px) {
	.job-detail {
		padding: 0 10px;
	}

	.title-section {
		flex-direction: column;
		align-items: flex-start;
	}

	.salary {
		margin-top: 10px;
	}

	.job-meta span {
		display: block;
		margin-bottom: 5px;
	}
}
</style> 