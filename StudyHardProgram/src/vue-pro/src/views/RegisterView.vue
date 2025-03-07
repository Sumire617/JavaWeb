<!-- G:\Sumire\JavaWeb\StudyHardProgram\src\vue-pro\src\views\RegisterView.vue -->
<template>
	<div>
		<h1>注册</h1>
		<input v-model="username" placeholder="用户名" />
		<input v-model="password" placeholder="密码" type="password" />
		<input v-model="phoneNumber" type="number" placeholder="手机号">
		<input v-model="email" type="email">
		<select v-model="userType">
			<option value="NORMAL">普通用户</option>
			<option value="EMPLOYER">用人单位</option>
		</select>
		<button @click="register">注册</button>
	</div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import router from "@/router/index.js";
const userId = ref('');
const phoneNumber = ref('');
const email = ref('');
const username = ref('');
const password = ref('');
const userType = ref('NORMAL');
function formatDate(milliseconds) {
	const date = new Date(milliseconds);
	const year = date.getFullYear();
	// 月份是从 0 开始计数的，所以要加 1，并且不足两位时前面补 0
	const month = String(date.getMonth() + 1).padStart(2, '0');
	const day = String(date.getDate()).padStart(2, '0');
	const hour = String(date.getHours()).padStart(2, '0');
	const minute = String(date.getMinutes()).padStart(2, '0');
	const second = String(date.getSeconds()).padStart(2, '0');
	return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
}

const now = Date.now();
const createdAt = formatDate(now);
const register = async () => {
	try {
		const response = await axios.post('/api/register', {
			userId: userId.value,
			username: username.value,
			password: password.value,
			userType: userType.value,
			email: email.value,
			phoneNumber: phoneNumber.value,
			createdAt: createdAt
		});
		console.log(response.data);
		// 注册成功后跳转到登录页面
		await router.push('/login');
	} catch (error) {
		console.error(error);
	}
};
</script>

<style scoped>
/* 样式代码 */
</style>