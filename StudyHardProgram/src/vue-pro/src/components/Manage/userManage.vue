<template>
	<div class="user-table-container">
		<!-- 搜索框 -->
		<div style="display: flex; justify-content: flex-end; margin-bottom: 20px; margin-right: 20px">
			<el-input v-model="searchQuery" placeholder="根据 ID 或用户名搜索" @keyup.enter="searchUsers" style="width: 300px; height: 36px;"></el-input>
			<el-button @click="searchUsers" style="height: 36px;">搜索</el-button>
			<!-- 添加按钮 -->
			<el-button type="primary" @click="addUser" style="height: 36px; margin-left: 10px;">添加</el-button>
		</div>
		<el-card class="user-table">
			<el-table :data="searchedUsers" style="width: 100%; border: 1px solid #e4e7ed; border-radius: 4px;" border>
				<el-table-column prop="shortUserId" label="用户ID"/>
				<el-table-column prop="username" label="用户名" />
				<el-table-column prop="password" label="用户密码" />
				<el-table-column prop="email" label="邮箱" />
				<el-table-column prop="phoneNumber" label="手机号" />
				<el-table-column label="操作">
					<template #default="scope">
						<el-button type="primary" size="small" @click="editUser(scope.row)" style="margin-right: 8px;">
							<i class="el-icon-edit"></i> 修改
						</el-button>
						<el-button type="danger" size="small" @click="deleteUser(scope.row.id)" style="margin-right: 8px;">
							<i class="el-icon-delete"></i> 删除
						</el-button>
						<el-button type="warning" size="small" @click="freezeUser(scope.row.id)">
							<i class="el-icon-lock"></i> 冻结
						</el-button>
					</template>
				</el-table-column>
			</el-table>
		</el-card>
	</div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';

// 定义响应式数据
const users = ref([]);
const searchQuery = ref('');

// 获取用户数据
const fetchUsers = async () => {
	try {
		const response = await axios.get('http://localhost:8080/api/users');
		users.value = response.data.map(user => ({
			...user,
			shortUserId: user.userId
		}));
	} catch (error) {
		ElMessage.error('获取用户数据失败：' + error.message);
	}
};

// 搜索用户
const searchUsers = () => {
	// 若搜索框为空，则展示所有用户
	if (!searchQuery.value) {
		searchedUsers.value = users.value;
		return;
	}
	searchedUsers.value = users.value.filter(user => {
		return user.id.includes(searchQuery.value) || user.username.includes(searchQuery.value);
	});
};

// 计算属性，用于展示搜索结果
const searchedUsers = computed(() => {
	if (!searchQuery.value) {
		return users.value;
	}
	return users.value.filter(user => {
		return user.id.includes(searchQuery.value) || user.username.includes(searchQuery.value);
	});
});

// 编辑用户
const editUser = (user) => {
	ElMessageBox.alert(`编辑用户：${user.username} (ID: ${user.id})`, '编辑用户', {
		confirmButtonText: '确定',
		callback: () => {
			console.log('编辑用户：', user);
		},
	});
};

// 删除用户
const deleteUser = (userId) => {
	ElMessageBox.confirm('确定要删除该用户吗？', '删除用户', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
			.then(async () => {
				try {
					await axios.delete(`/api/users/${userId}`);
					ElMessage.success('用户删除成功！');
					fetchUsers(); // 刷新用户列表
				} catch (error) {
					ElMessage.error('删除用户失败：' + error.message);
				}
			})
			.catch(() => {
				ElMessage.info('已取消删除操作');
			});
};

// 冻结用户
const freezeUser = (userId) => {
	ElMessageBox.confirm('确定要冻结该用户吗？', '冻结用户', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
			.then(async () => {
				try {
					await axios.post(`/api/users/${userId}/freeze`);
					ElMessage.success('用户冻结成功！');
					fetchUsers(); // 刷新用户列表
				} catch (error) {
					ElMessage.error('冻结用户失败：' + error.message);
				}
			})
			.catch(() => {
				ElMessage.info('已取消冻结操作');
			});
};

// 添加用户
const addUser = () => {
	const form = {
		username: '',
		password: '',
		email: '',
		phoneNumber: ''
	};
	ElMessageBox.prompt({
		title: '添加用户',
		message: `
        <div>
            <input v-model="form.username" type="text" placeholder="用户名" style="width: 100%; margin-bottom: 10px;">
            <input v-model="form.password" type="password" placeholder="密码" style="width: 100%; margin-bottom: 10px;">
            <input v-model="form.email" type="email" placeholder="邮箱" style="width: 100%; margin-bottom: 10px;">
            <input v-model="form.phoneNumber" type="text" placeholder="手机号" style="width: 100%;">
        </div>
    `,
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		inputValidator: () => {
			return form.username && form.password && form.email && form.phoneNumber;
		},
		inputErrorMessage: '请填写所有必填项'
	}).then(async () => {
		try {
			await axios.post('http://localhost:8080/api/users/register', form);
			ElMessage.success('用户添加成功！');
			fetchUsers(); // 刷新用户列表
		} catch (error) {
			ElMessage.error('用户添加失败：' + error.message);
		}
	}).catch(() => {
		ElMessage.info('已取消添加操作');
	});
};

// 页面加载时获取用户数据
onMounted(fetchUsers);
</script>

<style scoped>
.user-table-container {
	padding: 20px;
	width: 100%; /* 确保占据剩余宽度空间 */
	box-sizing: border-box; /* 确保内边距和边框包含在宽度内 */
}

.user-table {
	box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-table th {
	background-color: #f5f7fa;
}
</style>