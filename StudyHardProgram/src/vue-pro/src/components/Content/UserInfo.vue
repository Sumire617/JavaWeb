<template>
	<div class="user-info">
		<h1>个人信息</h1>
		<div v-if="userInfo">
			<p><strong>用户 ID:</strong> {{ userInfo.userId }}</p>
			<p><strong>用户名:</strong> {{ userInfo.username }}</p>
			<p><strong>邮箱:</strong> {{ userInfo.email }}</p>
			<p><strong>电话号码:</strong> {{ userInfo.phoneNumber }}</p>
			<p><strong>用户类型:</strong> {{ userInfo.userType }}</p>
		</div>
		<div v-else>
			<p>加载中...</p>
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const userInfo = ref(null);

onMounted(async () => {
	try {
		if (username) {
			// 动态生成请求 URL
			const response = await axios.get(`http://localhost:8080/api/users/username=${username}`);
			userInfo.value = response.data;
		} else {
			console.error('未找到用户名，请先登录');
		}
	} catch (error) {
		console.error('获取用户信息失败:', error);
	}
});
</script>

<style scoped>
.user-info {
	padding: 20px;
}
</style>