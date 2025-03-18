<template>
	<div class="home-container">
		<!-- 顶部导航栏 -->
		<el-header class="header">
			<div class="header-left">

			</div>
			<div class="header-right">
				<el-dropdown @command="handleCommand">
					<span class="user-info">
						<el-avatar :size="32" :src="userAvatar">
							{{ userInitials }}
						</el-avatar>
						<span class="username">{{ userName }}</span>
						<el-icon class="el-icon--right"><arrow-down /></el-icon>
					</span>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item command="profile">个人信息</el-dropdown-item>
							<el-dropdown-item command="applications">我的申请</el-dropdown-item>
							<el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>
			</div>
		</el-header>

		<!-- 顶部搜索区域 -->
		<div class="search-section">
			<div class="search-content">
				<h1>校园勤工俭学</h1>
				<p class="subtitle">为同学们提供优质的校内外兼职机会</p>
				<div class="search-box">
					<el-autocomplete
						v-model="searchQuery"
						:fetch-suggestions="querySearchAsync"
						placeholder="搜索职位、地点或关键词"
						class="search-input"
						clearable
						@select="handleSelect"
						@keyup.enter="handleSearch"
					>
						<template #suffix>
							<el-button type="primary" @click="handleSearch">
								搜索
							</el-button>
						</template>
						<template #default="{ item }">
							<div class="suggestion-item">
								{{ item.value }}
							</div>
						</template>
					</el-autocomplete>
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
									<el-tag size="small" type="success" class="job-type-tag">{{ job.jobType || '其他' }}</el-tag>
									<el-tag size="small" type="warning" v-if="job.requirements">{{ job.requirements }}</el-tag>
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
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import { ArrowDown } from '@element-plus/icons-vue';

const router = useRouter();
const user = ref(null);

// 用户信息计算属性
const userName = computed(() => {
	return user.value?.name;
});

const userAvatar = computed(() => {
	return user.value?.avatar || '';
});

const userInitials = computed(() => {
	if (!user.value?.name) return '未';
	return user.value.name.charAt(0);
});

// 处理下拉菜单命令
const handleCommand = async (command) => {
	switch (command) {
		case 'profile':
			router.push('/profile');
			break;
		case 'applications':
			router.push('/applications');
			break;
		case 'logout':
			try {
				await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				});
				localStorage.removeItem('user');
				router.push('/');
			} catch {
				// 用户取消退出
			}
			break;
	}
};

// 获取用户信息
const fetchUserInfo = async () => {
	try {
		const userStr = localStorage.getItem('user');
		if (!userStr) {
			ElMessage.error('请先登录');
			router.push('/');
			return;
		}
		
		// 先使用本地存储的用户信息
		try {
			const userData = JSON.parse(userStr);
			console.log('本地存储的用户信息:', userData);
			
			// 设置基本用户信息
			user.value = userData;
			
			// 从后端获取最新信息
			try {
				console.log('尝试获取最新用户信息，用户ID:', userData.userId);
				const response = await axios.get(`/api/users/id=${userData.userId}`);
				console.log('从后端获取的用户信息:', response.data);
				
				if (response.data) {
					// 合并本地和后端数据，优先使用后端数据
					user.value = { ...userData, ...response.data };
					// 更新本地存储
					localStorage.setItem('user', JSON.stringify(user.value));
				}
			} catch (apiError) {
				console.warn('从后端获取用户信息失败，使用本地存储的信息:', apiError);
				// 继续使用本地数据，不中断用户体验
			}
		} catch (parseError) {
			console.error('解析本地用户信息失败:', parseError);
			localStorage.removeItem('user');
			router.push('/');
			return;
		}
	} catch (error) {
		console.error('获取用户信息完全失败:', error);
		ElMessage.error('获取用户信息失败，请重新登录');
		localStorage.removeItem('user');
		router.push('/');
	}
};

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
const jobCategories = ref([
	{ id: 'all', name: '全部' },
	{ id: 'campus', name: '校内岗位' },
	{ id: 'teaching', name: '助教辅导' },
	{ id: 'admin', name: '行政助理' },
	{ id: 'library', name: '图书馆' },
	{ id: 'research', name: '科研助理' },
	{ id: 'service', name: '校园服务' },
	{ id: 'other', name: '其他' }
]);

// 获取可用的工作类型
const fetchJobTypes = async () => {
	try {
		const response = await axios.get('/api/jobs/types');
		console.log('获取到工作类型:', response.data);
		
		// 如果API返回了类型列表，用它替换默认值
		if (response.data && response.data.length > 0) {
			// 添加"全部"选项
			const typesList = [{ id: 'all', name: '全部' }];
			
			// 添加从API获取的类型
			response.data.forEach(type => {
				// 将中文类型作为id和name使用
				typesList.push({ id: type, name: type });
			});
			
			jobCategories.value = typesList;
			console.log('更新后的分类列表:', jobCategories.value);
		} else {
			console.warn('API没有返回工作类型数据，使用默认分类');
		}
	} catch (error) {
		console.error('获取工作类型失败:', error);
		// 保留默认类型
	}
};

// 获取岗位列表
const fetchJobs = async () => {
	loading.value = true;
	try {
		console.log('获取岗位列表, 分类:', activeCategory.value, '搜索:', searchQuery.value);
		
		const response = await axios.get('/api/jobs/public', {
			params: {
				page: pagination.value.currentPage - 1,
				size: pagination.value.pageSize,
				category: activeCategory.value !== 'all' ? activeCategory.value : null,
				search: searchQuery.value || null
			}
		});
		
		console.log('获取到岗位列表:', response.data);
		
		if (response.data.content) {
			jobList.value = response.data.content;
			pagination.value.total = response.data.totalElements;
		} else {
			jobList.value = [];
			pagination.value.total = 0;
			console.warn('返回的数据格式不符合预期:', response.data);
		}
	} catch (error) {
		console.error('获取岗位列表失败:', error);
		jobList.value = [];
		pagination.value.total = 0;
		
		if (error.response) {
			ElMessage.error(`获取岗位列表失败: ${error.response.data?.message || '服务器错误'}`);
		} else {
			ElMessage.error('获取岗位列表失败，请检查网络连接');
		}
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
	console.log('分类切换为:', category);
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

// 搜索建议函数
const querySearchAsync = (queryString, callback) => {
	if (queryString.length < 1) {
		callback([]);
		return;
	}
	
	axios.get('/api/jobs/suggestions', {
		params: {
			keyword: queryString,
			limit: 10
		}
	})
	.then(response => {
		const suggestions = response.data.map(item => ({
			value: item
		}));
		callback(suggestions);
	})
	.catch(error => {
		console.error('获取搜索建议失败:', error);
		callback([]);
	});
};

// 选择建议项
const handleSelect = (item) => {
	searchQuery.value = item.value;
	handleSearch();
};

onMounted(() => {
	fetchUserInfo();
	fetchJobTypes();
	fetchJobs();
});
</script>

<style scoped>
.home-container {
	width: 100%;
	background-color: #f5f7fa;
}

.header {
	background-color: white;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 0 20px;
	height: 60px;
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	z-index: 1000;
}

.header-left h2 {
	margin: 0;
	color: #303133;
	font-size: 1.5em;
}

.header-right {
	display: flex;
	align-items: center;
}

.user-info {
	display: flex;
	align-items: center;
	cursor: pointer;
	padding: 5px 10px;
	border-radius: 4px;
	transition: background-color 0.3s;
}

.user-info:hover {
	background-color: #f5f7fa;
}

.username {
	margin: 0 8px;
	color: #606266;
	font-size: 14px;
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
	margin-top: 80px;
}

.categories-section {
	background-color: white;
	padding: 15px 20px;
	border-radius: 8px;
	box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
	margin-bottom: 20px;
}

.el-tabs :deep(.el-tabs__nav) {
	display: flex;
	flex-wrap: wrap;
}

.el-tabs :deep(.el-tabs__item) {
	flex: 0 0 auto;
	margin: 5px 10px 5px 0;
	font-size: 15px;
	transition: all 0.3s;
}

.el-tabs :deep(.el-tabs__item.is-active) {
	font-weight: 600;
	transform: scale(1.05);
}

.el-tabs :deep(.el-tabs__active-bar) {
	background-color: #409eff;
	height: 3px;
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
	margin-top: 10px;
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
}

.job-type-tag {
	font-weight: 500;
	background-color: #f0f9eb !important;
	border-color: #e1f3d8 !important;
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

/* 添加自动完成样式 */
.suggestion-item {
	padding: 5px 0;
}

.search-input {
	width: 100%;
}

.search-input :deep(.el-input__suffix) {
	right: 0;
}

.search-input :deep(.el-input__inner) {
	padding-right: 80px;
}
</style>