<template>
	<div class="profile-container">
		<!-- 顶部导航栏 -->
		<el-header class="header">
			<div class="header-left">

			</div>
			<div class="header-right">
				<el-button type="primary" plain @click="router.push('/Home')">返回首页</el-button>
			</div>
		</el-header>

		<el-card class="profile-card">
			<template #header>
				<div class="card-header">
					<h2>个人信息</h2>
					<el-button type="primary" @click="handleEdit">编辑</el-button>
				</div>
			</template>
			
			<el-form
				ref="formRef"
				:model="userForm"
				:rules="rules"
				label-width="100px"
				:disabled="!isEditing"
			>
				<el-form-item label="头像">
					<el-upload
						class="avatar-uploader"
						action="/api/users/avatar"
						:show-file-list="false"
						:on-success="handleAvatarSuccess"
						:before-upload="beforeAvatarUpload"
						:disabled="!isEditing"
					>
						<img v-if="userForm.avatar" :src="userForm.avatar" class="avatar" />
						<el-icon v-else class="avatar-uploader-icon"><plus /></el-icon>
					</el-upload>
				</el-form-item>
				
				<el-form-item label="用户名" prop="name">
					<el-input v-model="userForm.name" />
				</el-form-item>
				
				<el-form-item label="手机号" prop="phone">
					<el-input v-model="userForm.phone" />
				</el-form-item>
				
				<el-form-item label="邮箱" prop="email">
					<el-input v-model="userForm.email" />
				</el-form-item>
				
				<el-form-item label="个人简介" prop="introduction">
					<el-input
						v-model="userForm.introduction"
						type="textarea"
						:rows="4"
					/>
				</el-form-item>
				
				<el-form-item v-if="isEditing">
					<el-button type="primary" @click="handleSave">保存</el-button>
					<el-button @click="handleCancel">取消</el-button>
				</el-form-item>
			</el-form>
		</el-card>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const formRef = ref(null);
const isEditing = ref(false);
const userForm = ref({
	name: '',
	studentId: '',
	phone: '',
	email: '',
	introduction: '',
	avatar: ''
});

const rules = {
	name: [
		{ required: true, message: '请输入用户名', trigger: 'blur' },
		{ min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
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

const router = useRouter();

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
		const userData = JSON.parse(userStr);
		console.log('本地存储的用户信息:', userData);
		
		// 预先填充用户信息，防止API调用失败时页面空白
		userForm.value = {
			name: userData.name || '',
			phone: userData.phone || '',
			email: userData.email || '',
			introduction: userData.introduction || '',
			avatar: userData.avatar || ''
		};
		
		// 从后端获取最新用户信息
		console.log('尝试获取用户信息，用户ID:', userData.userId);
		const response = await axios.get(`/api/users/id=${userData.userId}`);
		console.log('从后端获取的用户信息:', response.data);
		
		if (response.data) {
			// 更新表单数据
			userForm.value = {
				name: response.data.name || userData.name || '',
				phone: response.data.phone || userData.phone || '',
				email: response.data.email || userData.email || '',
				introduction: response.data.introduction || userData.introduction || '',
				avatar: response.data.avatar || userData.avatar || ''
			};
			
			// 更新本地存储
			const updatedUserData = { ...userData, ...response.data };
			localStorage.setItem('user', JSON.stringify(updatedUserData));
		}
	} catch (error) {
		console.error('获取用户信息失败:', error);
		
		if (error.response) {
			console.error('错误状态码:', error.response.status);
			console.error('错误数据:', error.response.data);
			ElMessage.error(`获取用户信息失败: ${error.response.data.error || '服务器错误'}`);
		} else if (error.request) {
			console.error('请求已发送但没有收到响应:', error.request);
			ElMessage.error('服务器没有响应，请检查网络连接');
		} else {
			console.error('请求设置时发生错误:', error.message);
			ElMessage.error(`请求错误: ${error.message}`);
		}
		
		// 尝试使用本地存储的数据
		const userStr = localStorage.getItem('user');
		if (userStr) {
			try {
				const userData = JSON.parse(userStr);
				userForm.value = {
					name: userData.name || '',
					phone: userData.phone || '',
					email: userData.email || '',
					introduction: userData.introduction || '',
					avatar: userData.avatar || ''
				};
			} catch (e) {
				console.error('解析本地用户信息失败:', e);
			}
		}
	}
};

// 处理编辑
const handleEdit = () => {
	isEditing.value = true;
};

// 处理取消
const handleCancel = () => {
	isEditing.value = false;
	fetchUserInfo();
};

// 处理保存
const handleSave = async () => {
	if (!formRef.value) return;
	
	try {
		await formRef.value.validate();
		
		const userStr = localStorage.getItem('user');
		if (!userStr) {
			ElMessage.error('请先登录');
			return;
		}
		
		const userData = JSON.parse(userStr);
		console.log('要更新的用户数据:', userForm.value);
		
		// 组装要发送的数据
		const updateData = {
			...userData,
			name: userForm.value.name,
			phone: userForm.value.phone,
			email: userForm.value.email,
			introduction: userForm.value.introduction,
			avatar: userForm.value.avatar
		};
		
		// 发送更新请求
		const response = await axios.patch(`/api/users/changeId=${userData.userId}`, updateData);
		console.log('保存用户信息响应:', response.data);
		
		ElMessage.success('保存成功');
		isEditing.value = false;
		
		// 更新本地存储的用户信息
		const updatedUser = { ...userData, ...userForm.value };
		localStorage.setItem('user', JSON.stringify(updatedUser));
	} catch (error) {
		console.error('保存用户信息失败:', error);
		
		if (error.response) {
			console.error('错误状态码:', error.response.status);
			console.error('错误数据:', error.response.data);
			ElMessage.error(`保存失败: ${error.response.data.error || '服务器错误'}`);
		} else if (error.request) {
			console.error('请求已发送但没有收到响应:', error.request);
			ElMessage.error('服务器没有响应，请检查网络连接');
		} else {
			console.error('请求设置时发生错误:', error.message);
			ElMessage.error(`请求错误: ${error.message}`);
		}
	}
};

// 处理头像上传
const handleAvatarSuccess = (response) => {
	userForm.value.avatar = response.url;
	ElMessage.success('头像上传成功');
};

const beforeAvatarUpload = (file) => {
	const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
	const isLt2M = file.size / 1024 / 1024 < 2;

	if (!isJPG) {
		ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
	}
	if (!isLt2M) {
		ElMessage.error('上传头像图片大小不能超过 2MB!');
	}
	return isJPG && isLt2M;
};

onMounted(() => {
	fetchUserInfo();
});
</script>

<style scoped>
.profile-container {
	max-width: 800px;
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

.profile-card {
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

.avatar-uploader {
	text-align: center;
}

.avatar-uploader .avatar {
	width: 100px;
	height: 100px;
	display: block;
}

.avatar-uploader .el-upload {
	border: 1px dashed #d9d9d9;
	border-radius: 6px;
	cursor: pointer;
	position: relative;
	overflow: hidden;
	transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
	border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
	font-size: 28px;
	color: #8c939d;
	width: 100px;
	height: 100px;
	text-align: center;
	line-height: 100px;
}
</style> 