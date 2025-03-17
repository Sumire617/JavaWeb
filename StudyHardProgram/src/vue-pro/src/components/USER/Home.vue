<template>
	<div class="home-container">
		<!-- 顶部搜索区域 -->
		<div class="search-section">
			<div class="search-content">
				<h1>校园勤工俭学</h1>
				<p class="subtitle">为同学们提供优质的校内外兼职机会</p>
				<div class="search-box">
					<el-input
						v-model="searchQuery"
						placeholder="搜索职位、公司或关键词"
						class="search-input"
						@keyup.enter="handleSearch"
					>
						<template #append>
							<el-button type="primary" @click="handleSearch">
								搜索
							</el-button>
						</template>
					</el-input>
				</div>
				<div class="hot-cities">
					<span class="label">热门校区：</span>
					<template v-for="city in hotCities" :key="city">
						<el-tag
							class="city-tag"
							effect="plain"
							@click="searchQuery = city; handleSearch()"
						>
							{{ city }}
						</el-tag>
					</template>
				</div>
			</div>
		</div>

		<!-- 主要内容区域 -->
		<div class="main-content">
			<!-- 职位分类 -->
			<div class="categories-section">
				<el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
					<el-tab-pane
						v-for="category in jobCategories"
						:key="category.id"
						:label="category.name"
						:name="category.id"
					/>
				</el-tabs>
			</div>

			<!-- 职位列表 -->
			<div class="job-list" v-loading="loading">
				<el-row :gutter="20">
					<el-col
						v-for="job in jobList"
						:key="job.jobPostId"
						:xs="24"
						:sm="12"
						:md="8"
						:lg="6"
						class="job-col"
					>
						<el-card
							class="job-card"
							shadow="hover"
						>
							<div class="job-header">
								<h3 class="job-title" @click="viewJobDetail(job.jobPostId)">{{ job.jobTitle }}</h3>
								<div class="job-salary">{{ job.salaryRange }}/小时</div>
							</div>
							<div class="job-info">
								<div class="job-meta">
									<span><i class="el-icon-location"></i>{{ job.location }}</span>
									<span><i class="el-icon-time"></i>{{ job.workSchedule }}</span>
								</div>
								<div class="job-rating" v-if="job.rating">
									<el-rate
										v-model="job.rating"
										disabled
										show-score
										text-color="#ff9900"
										score-template="{value}"
									/>
									<span class="review-count">({{ job.reviewCount || 0 }}条评价)</span>
								</div>
								<div class="job-tags">
									<el-tag size="small" type="success">{{ job.jobType }}</el-tag>
									<el-tag size="small" type="warning">{{ job.requirements }}</el-tag>
								</div>
							</div>
							<div class="job-footer">
								<span class="post-time">发布时间：{{ formatDateTime(job.postedAt) }}</span>
								<el-button type="primary" size="small" @click="handleApply(job)">立即申请</el-button>
							</div>
						</el-card>
					</el-col>
				</el-row>
			</div>

			<!-- 分页 -->
			<div class="pagination-container">
				<el-pagination
					v-model:current-page="pagination.currentPage"
					:page-size="pagination.pageSize"
					:total="pagination.total"
					layout="prev, pager, next, jumper"
					@current-change="handleCurrentChange"
				/>
			</div>
		</div>

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
				<el-form-item label="个人介绍" prop="introduction">
					<el-input v-model="applyForm.introduction" type="textarea" />
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
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const router = useRouter();

// 岗位列表数据
const jobList = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const activeCategory = ref('all');

// 分页数据
const pagination = ref({
	currentPage: 1,
	pageSize: 8,
	total: 0
});

// 职位分类
const jobCategories = [
	{ id: 'all', name: '全部' },
	{ id: 'campus', name: '校内岗位' },
	{ id: 'teaching', name: '助教辅导' },
	{ id: 'admin', name: '行政助理' },
	{ id: 'library', name: '图书馆' },
	{ id: 'research', name: '科研助理' },
	{ id: 'it', name: '信息技术' },
	{ id: 'service', name: '校园服务' },
	{ id: 'other', name: '其他' }
];

// 热门校区
const hotCities = ['本部校区', '医学部', '工学部', '信息学部', '新校区', '附属医院', '科技园', '创新园'];

// 获取岗位列表
const fetchJobs = async () => {
	loading.value = true;
	try {
		const response = await axios.get('/api/jobs/public', {
			params: {
				page: pagination.value.currentPage - 1,
				size: pagination.value.pageSize,
				category: activeCategory.value !== 'all' ? activeCategory.value : null,
				search: searchQuery.value || null
			}
		});
		jobList.value = response.data.content;
		pagination.value.total = response.data.totalElements;
	} catch (error) {
		console.error('获取岗位列表失败:', error);
	} finally {
		loading.value = false;
	}
};

// 搜索处理
const handleSearch = () => {
	pagination.value.currentPage = 1;
	fetchJobs();
};

// 分类切换
const handleCategoryChange = (category) => {
	activeCategory.value = category;
	pagination.value.currentPage = 1;
	fetchJobs();
};

// 分页处理
const handleCurrentChange = (val) => {
	pagination.value.currentPage = val;
	fetchJobs();
};

// 查看岗位详情
const viewJobDetail = (jobId) => {
	router.push(`/job/${jobId}`);
};

// 申请相关数据
const applyDialogVisible = ref(false);
const applyFormRef = ref(null);
const currentJob = ref(null);
const applyForm = ref({
	name: '',
	studentId: '',
	phone: '',
	email: '',
	introduction: ''
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
	]
};

// 处理申请
const handleApply = (job) => {
	currentJob.value = job;
	applyDialogVisible.value = true;
};

// 提交申请
const submitApplication = async () => {
	if (!applyFormRef.value) return;
	
	try {
		await applyFormRef.value.validate();
		
		// 获取当前登录用户信息
		const userStr = localStorage.getItem('user');
		if (!userStr) {
			ElMessage.error('请先登录');
			router.push('/');
			return;
		}
		const user = JSON.parse(userStr);
		
		const applicationData = {
			jobPostId: currentJob.value.jobPostId,
			userId: user.userId,
			name: applyForm.value.name,
			studentId: applyForm.value.studentId,
			phone: applyForm.value.phone,
			email: applyForm.value.email,
			introduction: applyForm.value.introduction || ''
		};

		try {
			const response = await axios.post('/api/applications', applicationData);
			if (response.data.error) {
				ElMessage.error(response.data.error);
				return;
			}
			ElMessage.success('申请提交成功');
			applyDialogVisible.value = false;
			applyFormRef.value.resetFields();
		} catch (error) {
			if (error.response?.data?.error) {
				ElMessage.error(error.response.data.error);
			} else {
				ElMessage.error('申请提交失败，请稍后重试');
			}
			console.error('申请提交失败:', error);
		}
	} catch (error) {
		if (error.name === 'ValidationError') return;
		ElMessage.error('申请提交失败');
		console.error('申请提交失败:', error);
	}
};

// 格式化时间
const formatDateTime = (dateTime) => {
	if (!dateTime) return '';
	return new Date(dateTime).toLocaleDateString('zh-CN', {
		year: 'numeric',
		month: '2-digit',
		day: '2-digit'
	});
};

onMounted(() => {
	fetchJobs();
});
</script>

<style scoped>
.home-container {
	width: 100%;
	background-color: #f5f7fa;
}

.search-section {
	background: linear-gradient(135deg, #36D1DC 0%, #5B86E5 100%);
	padding: 60px 0;
	color: white;
	text-align: center;
}

.search-content {
	max-width: 1200px;
	margin: 0 auto;
	padding: 0 20px;
}

.search-content h1 {
	font-size: 2.8em;
	margin-bottom: 15px;
	font-weight: bold;
}

.subtitle {
	font-size: 1.2em;
	margin-bottom: 30px;
	opacity: 0.9;
}

.search-box {
	margin-bottom: 20px;
	max-width: 800px;
	margin: 0 auto 20px;
}

.search-input {
	width: 100%;
}

.hot-cities {
	margin-top: 20px;
}

.city-tag {
	margin: 0 5px 8px;
	cursor: pointer;
}

.main-content {
	max-width: 1400px;
	margin: 0 auto;
	padding: 20px;
}

.categories-section {
	margin-bottom: 30px;
	background: white;
	padding: 20px;
	border-radius: 8px;
	box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

.job-col {
	margin-bottom: 20px;
}

.job-card {
	height: 100%;
	cursor: pointer;
	transition: all 0.3s;
}

.job-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.job-header {
	border-bottom: 1px solid #eee;
	padding-bottom: 10px;
	margin-bottom: 10px;
}

.job-title {
	cursor: pointer;
	color: #303133;
	font-size: 1.1em;
	margin: 0 0 8px;
}

.job-title:hover {
	color: #409EFF;
}

.job-salary {
	color: #f56c6c;
	font-weight: bold;
	font-size: 1.1em;
}

.job-info {
	margin-bottom: 15px;
}

.company-name {
	color: #606266;
	margin-bottom: 8px;
}

.job-meta {
	margin-bottom: 8px;
	font-size: 0.9em;
	color: #909399;
}

.job-meta span {
	margin-right: 15px;
}

.job-meta i {
	margin-right: 4px;
}

.job-rating {
	margin: 8px 0;
	display: flex;
	align-items: center;
}

.review-count {
	margin-left: 8px;
	font-size: 0.9em;
	color: #909399;
}

.job-tags {
	margin-top: 8px;
}

.job-tags .el-tag {
	margin-right: 5px;
}

.job-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 10px;
	padding-top: 10px;
	border-top: 1px solid #eee;
}

.post-time {
	color: #909399;
	font-size: 0.9em;
}

.pagination-container {
	text-align: center;
	margin-top: 30px;
	padding: 20px 0;
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
	.search-content h1 {
		font-size: 2em;
	}
	
	.subtitle {
		font-size: 1em;
	}
	
	.main-content {
		padding: 10px;
	}
	
	.job-card {
		margin-bottom: 15px;
	}
}
</style>