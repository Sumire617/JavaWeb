<template>
	<div class="job-table-container">
		<!-- 搜索和操作区域 -->
		<div class="search-bar">
			<el-input
					v-model="searchQuery"
					placeholder="根据岗位ID或名称搜索"
					@keyup.enter="searchJobs"
					style="width: 300px;"
			></el-input>
			<el-button @click="searchJobs">搜索</el-button>
			<el-button type="primary" @click="addJob" style="margin-left: 10px;">发布新岗位</el-button>
		</div>

		<!-- 岗位数据表格 -->
		<el-card class="job-table">
			<el-table :data="searchedJobs" border>
				<el-table-column prop="jobPostId" label="岗位ID" width="220"></el-table-column>
				<el-table-column prop="jobTitle" label="岗位名称"></el-table-column>
				<el-table-column prop="employerId" label="发布单位"></el-table-column>
				<el-table-column prop="location" label="工作地点" width="120"></el-table-column>
				<el-table-column prop="salaryRange" label="薪资范围" width="150"></el-table-column>
				<el-table-column prop="status" label="状态" width="100">
					<template #default="scope">
						<el-tag :type="statusTagType(scope.row.status)">
							{{ statusText(scope.row.status) }}
						</el-tag>
					</template>
				</el-table-column>
				<el-table-column prop="postedAt" label="发布日期" width="210"></el-table-column>
				<el-table-column label="操作" width="200">
					<template #default="scope">
						<el-button type="primary" size="small" @click="editJob(scope.row.jobPostId)">编辑</el-button>
						<el-button type="danger" size="small" @click="deleteJob(scope.row.jobPostId)">删除</el-button>
					</template>
				</el-table-column>
			</el-table>

			<!-- 添加分页组件 -->
			<div class="pagination-container">
				<el-pagination
						v-model:current-page="pagination.currentPage"
						v-model:page-size="pagination.pageSize"
						:page-sizes="[10, 20, 50, 100]"
						:total="pagination.total"
						layout="total, sizes, prev, pager, next"
						@size-change="handleSizeChange"
						@current-change="handleCurrentChange"
				/>
			</div>
		</el-card>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';

// 配置 axios 默认值
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.headers.common['Cache-Control'] = 'no-cache, no-store, must-revalidate';
axios.defaults.headers.common['Pragma'] = 'no-cache';
axios.defaults.headers.common['Expires'] = '0';

// 响应式数据
const jobs = ref([]);
const searchQuery = ref('');
const searchedJobs = ref([]);
const currentStatus = ref('all');

// 分页数据
const pagination = ref({
	currentPage: 1,
	pageSize: 10,
	total: 0
});

// 状态标签样式映射
const statusTagType = (status) => {
	switch(status) {
		case 'APPROVED': return 'success';
		case 'PENDING': return 'warning';
		case 'REJECTED': return 'info';
		case 'CLOSED': return 'danger';
		default: return '';
	}
};

// 状态显示文本映射
const statusText = (status) => {
	switch(status) {
		case 'APPROVED': return '招聘中';
		case 'PENDING': return '待审核';
		case 'REJECTED': return '已拒绝';
		case 'CLOSED': return '已结束';
		default: return status;
	}
};

// 获取岗位数据
const fetchJobs = async () => {
	try {
		console.log('开始获取岗位数据，参数：', {
			page: pagination.value.currentPage - 1,
			size: pagination.value.pageSize,
			keyword: searchQuery.value || null,
			status: currentStatus.value === 'all' ? null : currentStatus.value
		});

		const response = await axios.get('/api/jobs', {
			params: {
				page: pagination.value.currentPage - 1,
				size: pagination.value.pageSize,
				keyword: searchQuery.value || null,
				status: currentStatus.value === 'all' ? null : currentStatus.value
			}
		});
		
		console.log('API响应数据：', response.data);
		
		if (response.data && Array.isArray(response.data.content)) {
			jobs.value = response.data.content;
			searchedJobs.value = response.data.content;
			pagination.value.total = response.data.totalElements;
			console.log('更新后的岗位数据：', jobs.value);
			console.log('分页信息：', {
				currentPage: pagination.value.currentPage,
				pageSize: pagination.value.pageSize,
				total: pagination.value.total
			});
		} else {
			console.warn('API返回的数据格式不正确：', response.data);
			ElMessage.warning('返回的数据格式不正确');
		}
	} catch (error) {
		console.error('获取岗位数据失败:', error);
		console.error('错误详情:', error.response?.data);
		ElMessage.error('获取岗位数据失败：' + (error.response?.data?.message || error.message));
	}
};

// 处理页码改变
const handleCurrentChange = (page) => {
	pagination.value.currentPage = page;
	fetchJobs();
};

// 处理每页条数改变
const handleSizeChange = (size) => {
	pagination.value.pageSize = size;
	pagination.value.currentPage = 1;
	fetchJobs();
};

// 搜索功能
const searchJobs = () => {
	pagination.value.currentPage = 1;
	fetchJobs();
};

// 发布新岗位
const addJob = async () => {
	try {
		await ElMessageBox({
			title: '发布新岗位',
			message: `
        <div class="custom-message-box">
          <form>
            <label for="employerId">雇主ID:</label>
            <input type="text" id="employerId" placeholder="请输入雇主ID" style="width: 100%; margin-bottom: 10px;">

            <label for="jobTitle">岗位名称:</label>
            <input type="text" id="jobTitle" placeholder="请输入岗位名称" style="width: 100%; margin-bottom: 10px;">

            <label for="jobDescription">岗位描述:</label>
            <textarea id="jobDescription" placeholder="请输入岗位描述" style="width: 100%; margin-bottom: 10px;"></textarea>

            <label for="requirements">任职要求:</label>
            <textarea id="requirements" placeholder="请输入任职要求" style="width: 100%; margin-bottom: 10px;"></textarea>

            <label for="salaryRange">薪资范围:</label>
            <input type="text" id="salaryRange" placeholder="请输入薪资范围" style="width: 100%; margin-bottom: 10px;">

            <label for="location">工作地点:</label>
            <input type="text" id="location" placeholder="请输入工作地点" style="width: 100%; margin-bottom: 10px;">

            <label for="status">岗位状态:</label>
            <select id="status" style="width: 100%; margin-bottom: 10px;">
              <option value="PENDING">待审核</option>
              <option value="APPROVED">招聘中</option>
              <option value="CLOSED">已结束</option>
            </select>
          </form>
        </div>
      `,
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			showCancelButton: true,
			dangerouslyUseHTMLString: true,
			beforeClose: async (action, instance, done) => {
				if (action === 'confirm') {
					const employerIdEl = document.getElementById('employerId');
					const jobTitleEl = document.getElementById('jobTitle');
					const jobDescriptionEl = document.getElementById('jobDescription');
					const requirementsEl = document.getElementById('requirements');
					const salaryRangeEl = document.getElementById('salaryRange');
					const locationEl = document.getElementById('location');
					const statusEl = document.getElementById('status');
					
					if (employerIdEl && jobTitleEl && jobDescriptionEl && requirementsEl && salaryRangeEl && locationEl && statusEl) {
						const jobData = {
							employerId: employerIdEl.value,
							jobTitle: jobTitleEl.value,
							jobDescription: jobDescriptionEl.value,
							requirements: requirementsEl.value,
							salaryRange: salaryRangeEl.value,
							location: locationEl.value,
							status: statusEl.value
						};
						
						try {
							const response = await axios.post('/api/jobs', jobData);
							if (response.status === 200 || response.status === 201) {
								ElMessage.success('岗位发布成功！');
								await fetchJobs();
							}
						} catch (error) {
							ElMessage.error('岗位发布失败：' + (error.response?.data?.message || error.message));
						}
					}
				}
				done();
			}
		});
	} catch (error) {
		console.error('发布岗位失败:', error);
	}
};

// 编辑岗位
const editJob = async (jobPostId) => {
	try {
		// 这里使用本地模拟数据查找岗位信息；实际开发时可通过接口获取详细数据
		const job = jobs.value.find(item => item.jobPostId === jobPostId);
		if (!job) {
			ElMessage.error('未找到岗位信息');
			return;
		}
		// 若接口返回的数据不包含某些字段，可设置默认值
		const defaultEmployerId = job.employerId || '';
		const defaultJobDescription = job.jobDescription || '';
		const defaultRequirements = job.requirements || '';

		await ElMessageBox({
			title: '编辑岗位',
			message: `
        <div class="custom-message-box">
          <form>
            <label for="employerId">雇主ID:</label>
            <input type="text" id="employerId" placeholder="请输入雇主ID" value="${defaultEmployerId}" style="width: 100%; margin-bottom: 10px;">

            <label for="jobTitle">岗位名称:</label>
            <input type="text" id="jobTitle" placeholder="请输入岗位名称" value="${job.jobTitle}" style="width: 100%; margin-bottom: 10px;">

            <label for="jobDescription">岗位描述:</label>
            <textarea id="jobDescription" placeholder="请输入岗位描述" style="width: 100%; margin-bottom: 10px;">${defaultJobDescription}</textarea>

            <label for="requirements">任职要求:</label>
            <textarea id="requirements" placeholder="请输入任职要求" style="width: 100%; margin-bottom: 10px;">${defaultRequirements}</textarea>

            <label for="salaryRange">薪资范围:</label>
            <input type="text" id="salaryRange" placeholder="请输入薪资范围" value="${job.salaryRange}" style="width: 100%; margin-bottom: 10px;">

            <label for="location">工作地点:</label>
            <input type="text" id="location" placeholder="请输入工作地点" value="${job.location}" style="width: 100%; margin-bottom: 10px;">

            <label for="status">岗位状态:</label>
            <select id="status" style="width: 100%; margin-bottom: 10px;">
              <option value="APPROVED" ${job.status === 'APPROVED' ? 'selected' : ''}>招聘中</option>
              <option value="PENDING" ${job.status === 'PENDING' ? 'selected' : ''}>待审核</option>
              <option value="REJECTED" ${job.status === 'REJECTED' ? 'selected' : ''}>已拒绝</option>
              <option value="CLOSED" ${job.status === 'CLOSED' ? 'selected' : ''}>已结束</option>
            </select>
          </form>
        </div>
      `,
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			showCancelButton: true,
			dangerouslyUseHTMLString: true,
			beforeClose: async (action, instance, done) => {
				if (action === 'confirm') {
					const employerIdEl = document.getElementById('employerId');
					const jobTitleEl = document.getElementById('jobTitle');
					const jobDescriptionEl = document.getElementById('jobDescription');
					const requirementsEl = document.getElementById('requirements');
					const salaryRangeEl = document.getElementById('salaryRange');
					const locationEl = document.getElementById('location');
					const statusEl = document.getElementById('status');
					if (employerIdEl && jobTitleEl && jobDescriptionEl && requirementsEl && salaryRangeEl && locationEl && statusEl) {
						const now = new Date().toISOString();
						// 注意：确保 jobPostId 在请求体中传递
						const updatedJobData = {
							jobPostId: jobPostId, // 这里必须包含唯一标识
							employerId: employerIdEl.value,
							jobTitle: jobTitleEl.value,
							jobDescription: jobDescriptionEl.value,
							requirements: requirementsEl.value,
							salaryRange: salaryRangeEl.value,
							location: locationEl.value,
							status: statusEl.value,
							postedAt: job.postDate || now,
							updatedAt: now
						};
						try {
							const response = await axios.put(`/api/jobs/${jobPostId}`, updatedJobData);
							if (response.status === 200) {
								ElMessage.success('岗位编辑成功！');
								fetchJobs();
							} else {
								ElMessage.error('岗位编辑失败！');
							}
						} catch (error) {
							ElMessage.error('岗位编辑失败：' + error.message);
						}
					} else {
						ElMessage.error('无法获取表单元素');
					}
				} else if (action === 'cancel') {
					ElMessage.info("操作已取消");
				}
				done();
			}
		});
	} catch (error) {
		console.log(error);
	}
};

// 删除岗位
const deleteJob = async (jobPostId) => {
	try {
		await ElMessageBox.confirm('确定要删除该岗位吗？', '警告', {
			type: 'warning'
		});
		await axios.delete(`/api/jobs/${jobPostId}`);
		ElMessage.success('删除成功');
		await fetchJobs();
	} catch (error) {
		ElMessage.error('删除失败：' + error.message);
	}
};

// 初始化加载数据
onMounted(fetchJobs)
</script>

<style scoped>
.job-table-container {
	padding: 20px;
	box-sizing: border-box;
}

.search-bar {
	display: flex;
	justify-content: flex-end;
	margin-bottom: 20px;
	gap: 10px;
}

.job-table {
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.el-table {
	font-size: 14px;
}

.el-table :deep(.el-table__cell) {
	padding: 12px 0;
}

.pagination-container {
	margin-top: 20px;
	display: flex;
	justify-content: flex-end;
}
</style>
