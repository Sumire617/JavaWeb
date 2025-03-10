<template>
	<div class="user-info">
		<h1 class="title">个人信息</h1>
		<div v-if="userInfo" class="info-container">
			<p class="info-item"><strong>用户 ID:</strong> {{ userInfo.userId }}</p>
			<p class="info-item"><strong>用户名:</strong> {{ userInfo.username }}</p>
			<p class="info-item"><strong>邮箱:</strong> {{ userInfo.email }}</p>
			<p class="info-item"><strong>电话号码:</strong> {{ userInfo.phoneNumber }}</p>
			<p class="info-item"><strong>用户类型:</strong> {{ userInfo.userType }}</p>
		</div>
		<div v-else class="loading">
			<p>加载中...</p>
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const userInfo = ref(null);

onMounted(async () => {
	// 从localStorage中获取用户信息
	const user = localStorage.getItem('user');
	if(user) {
		userInfo.value = JSON.parse(user);
	} else {
		console.error('未找到用户信息，请先登录');
	}
});
</script>

<style scoped>
.user-info {
	padding: 20px;
	max-width: 600px;
	margin: 0 auto;
}

.title {
	color: #333;
	font-size: 24px;
	margin-bottom: 20px;
	text-align: center;
}

.info-container {
	background-color: #f9f9f9;
	border-radius: 8px;
	padding: 20px;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.info-item {
	font-size: 16px;
	margin-bottom: 10px;
	color: #666;
}

.info-item strong {
	color: #333;
	margin-right: 10px;
}

.loading {
	text-align: center;
	color: #999;
	font-size: 16px;
}
</style>