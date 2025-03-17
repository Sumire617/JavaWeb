<template>
	<div class="user-table-container">
		<!-- 搜索框 -->
		<div style="display: flex; justify-content: flex-end; margin-bottom: 20px; margin-right: 20px">
			<el-input v-model="searchQuery" placeholder="根据 ID 或用户名搜索" @keyup.enter="searchUsers" style="width: 300px; height: 36px; margin-right: 10px"></el-input>
			<el-button @click="searchUsers" style="height: 36px;">搜索</el-button>
			<el-button type="primary" @click="addUser" style="height: 36px; margin-left: 10px;">添加</el-button>
		</div>
		<!--用户数据表格-->
		<el-card class="user-table">
			<el-table :data="searchedUsers" style="width: 100%; border: 1px solid #e4e7ed; border-radius: 4px;" border>
				<el-table-column prop="userId" label="用户ID" width="200px"/>
				<el-table-column prop="username" label="用户名" width="180px"/>
				<el-table-column prop="email" label="邮箱" width="210px"/>
				<el-table-column prop="phoneNumber" label="手机号" width="150px"/>
				<el-table-column prop="userStatus" label="状态" width="80px">
					<template #default="scope">
						<el-tag :type="statusTagType(scope.row.userStatus)">
							{{ scope.row.userStatus }}
						</el-tag>
					</template>
				</el-table-column>
				<el-table-column prop="createdAt" label="创建日期"/>
				<el-table-column prop="updatedAt" label="登录日期"/>
				<el-table-column label="操作" width="200px">
					<template #default="scope">
						<el-button type="primary" size="small" @click="editUser(scope.row.userId)" style="margin-right: 8px;">
							 修改
						</el-button>
						<el-button type="danger" size="small" @click="deleteUser(scope.row.userId)" style="margin-right: 8px;">
							删除
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
const searchedUsers = ref([]); // 用于存储搜索结果
// 状态标签样式映射
const statusTagType = (status) => {
	switch(status) {
		case '正常': return 'success';
		case '冻结': return 'warning';
		default: return 'success';
	}
};

// 获取用户数据
const fetchUsers = async () => {
	try {
		const response = await axios.get('http://localhost:8080/api/users');
		users.value = response.data;
		searchedUsers.value = response.data; // 初始化搜索结果为所有用户
	} catch (error) {
		ElMessage.error('获取用户数据失败：' + error.message);
	}
};

// 搜索用户
const searchUsers = async () => {
	if (!searchQuery.value) {
		// 如果搜索框为空，则展示所有用户
		searchedUsers.value = users.value;
		return;
	}
	try {
		// 判断搜索关键词是 UUID 格式的 ID 还是用户名
		const isIdSearch = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/.test(searchQuery.value);

		if (isIdSearch) {
			// 根据 ID 搜索
			const response = await axios.get(`http://localhost:8080/api/users/id=${searchQuery.value}`);
			searchedUsers.value = [response.data]; // 假设接口返回单个用户对象，需要将其包装为数组
		} else {
			// 根据用户名搜索
			const response = await axios.get(`http://localhost:8080/api/users/username=${searchQuery.value}`);
			searchedUsers.value = [response.data]; // 假设接口返回单个用户对象，需要将其包装为数组
		}
	} catch (error) {
		ElMessage.error('搜索用户失败：' + error.message);
		searchedUsers.value = []; // 清空搜索结果，避免显示旧数据
	}
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
					await axios.delete(`http://localhost:8080/api/users/deleteId=${userId}`);
					ElMessage.success('用户删除成功！');
					await fetchUsers(); // 刷新用户列表
				} catch (error) {
					ElMessage.error('删除用户失败：' + error.message);
				}
			})
			.catch(() => {
				ElMessage.info('已取消删除操作');
			});
};
// 编辑用户
const editUser = async (userId) => {
	try {
		// 先获取当前用户信息
		const response = await axios.get(`http://localhost:8080/api/users/id=${userId}`);
		const user = response.data;

		// 弹出编辑对话框，dangerouslyUseHTMLString 用于渲染自定义 HTML 内容
		await ElMessageBox({
			title: '修改信息',
			message: `
        <div class="custom-message-box">
          <form>
            <label for="username">用户名:</label>
            <input type="text" id="username" value="${user.username}" style="width: 100%; margin-bottom: 10px;">
            <label for="password">用户密码:</label>
            <input type="password" id="password" value="${user.password}" style="width: 100%; margin-bottom: 10px;">
            <label for="email">邮箱:</label>
            <input type="email" id="email" value="${user.email}" style="width: 100%; margin-bottom: 10px;">
            <label for="phoneNumber">手机号:</label>
            <input type="text" id="phoneNumber" value="${user.phoneNumber}" style="width: 100%; margin-bottom: 10px;">
            <label for="userStatus">用户状态:</label>
            <select id="userStatus" style="width: 100%;">
              <option value="正常" ${user.userStatus === '正常' ? 'selected' : ''}>正常</option>
              <option value="冻结" ${user.userStatus === '冻结' ? 'selected' : ''}>冻结</option>
            </select>
          </form>
        </div>
      `,
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			showCancelButton: true,
			dangerouslyUseHTMLString: true,
			beforeClose: async (action, instance, done) => {
				// 当用户点击"确定"时，收集表单数据并发起 PATCH 请求
				if (action === 'confirm') {
					const usernameEl = document.getElementById('username');
					const passwordEl = document.getElementById('password');
					const emailEl = document.getElementById('email');
					const phoneNumberEl = document.getElementById('phoneNumber');
					const userStatusEl = document.getElementById('userStatus');
					const now = new Date().toISOString();
					if (usernameEl && passwordEl && emailEl && phoneNumberEl && userStatusEl) {
						// 收集更新后的数据，注意不要与外层变量重名
						const updatedFormData = {
							username: usernameEl.value,
							password: passwordEl.value,
							email: emailEl.value,
							phoneNumber: phoneNumberEl.value,
							userStatus: userStatusEl.value,
							updatedAt: now
						};

						try {
							const patchResponse = await axios.patch(`http://localhost:8080/api/users/changeId=${userId}`, updatedFormData);
							if (patchResponse.status === 200) {
								ElMessage.success('用户信息修改成功！');
								await fetchUsers(); // 修改成功后刷新用户列表
							} else {
								ElMessage.error('用户信息修改失败！');
							}
						} catch (patchError) {
							ElMessage.error('用户信息修改失败：' + patchError.message);
						}
					} else {
						ElMessage.error('无法获取表单元素');
					}
				} else if(action === 'cancel') {
					ElMessage.info('已取消修改操作');
				}
				done();
			},
		});
	} catch (error) {
		console.log(error.message);
	}
};
// 添加用户
const addUser = async () => {
	try {
		await ElMessageBox({
			title: '添加用户',
			message: `
        <div class="custom-message-box">
          <form>
            <label for="username">用户名:</label>
            <input type="text" id="username" placeholder="请输入用户名" style="width: 100%; margin-bottom: 10px;">

            <label for="password">密码:</label>
            <input type="password" id="password" placeholder="请输入密码" style="width: 100%; margin-bottom: 10px;">

            <label for="email">邮箱:</label>
            <input type="email" id="email" placeholder="请输入邮箱" style="width: 100%; margin-bottom: 10px;">

            <label for="phoneNumber">手机号:</label>
            <input type="text" id="phoneNumber" placeholder="请输入手机号" style="width: 100%; margin-bottom: 10px;">

            <label for="userType">用户类型:</label>
            <select id="userType" style="width: 100%; margin-bottom: 10px;">
              <option value="NORMAL">NORMAL</option>
              <option value="EMPLOYER">EMPLOYER</option>
              <option value="ADMIN">ADMIN</option>
            </select>

            <label for="userStatus">用户状态:</label>
            <select id="userStatus" style="width: 100%;">
              <option value="正常">正常</option>
              <option value="冻结">冻结</option>
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
					const usernameEl = document.getElementById('username');
					const passwordEl = document.getElementById('password');
					const emailEl = document.getElementById('email');
					const phoneNumberEl = document.getElementById('phoneNumber');
					const userTypeEl = document.getElementById('userType');
					const userStatusEl = document.getElementById('userStatus');

					if (usernameEl && passwordEl && emailEl && phoneNumberEl && userTypeEl && userStatusEl) {
						// 获取当前时间的 ISO 格式字符串
						const now = new Date().toISOString();
						const formData = {
							username: usernameEl.value,
							password: passwordEl.value,
							email: emailEl.value,
							phoneNumber: phoneNumberEl.value,
							userType: userTypeEl.value,
							userStatus: userStatusEl.value,
							createdAt: now,
							updatedAt: now
						};

						try {
							const postResponse = await axios.post('http://localhost:8080/api/users/register', formData);
							if (postResponse.status === 200 || postResponse.status === 201) {
								ElMessage.success('用户添加成功！');
								await fetchUsers(); // 添加成功后刷新用户列表
							} else {
								ElMessage.error('用户添加失败！');
							}
						} catch (error) {
							ElMessage.error('用户添加失败：' + error.message);
						}
					} else {
						ElMessage.error('无法获取表单元素');
					}
				} else if(action === 'cancel') {
					ElMessage.info('已取消添加操作');
				}
				done();
			}
		});
	} catch (error) {
		console.log(error.message);
	}
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