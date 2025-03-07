<template>
	<div class="login-container">
		<div class="login-box">
			<h1>登录</h1>
			<form @submit.prevent="login">
				<input v-model="username" type="text" placeholder="用户名" />
				<input v-model="password" type="password" placeholder="密码" />
				<button type="submit">登录</button>
			</form>
			<router-link to="/register" >注册</router-link>
			<p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
		</div>
	</div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import router from "@/router/index.js";

const username = ref('');
const password = ref('');
const errorMessage = ref('');
const login = async () => {
	// 检查用户名和密码是否为空
	if (!username.value || !password.value) {
		errorMessage.value = '用户名和密码不能为空';
		return;
	}
	try {
		const response = await axios.post('http://localhost:8080/api/users/login', {
			username: username.value,
			password: password.value
		},{
			headers: {
				'Content-Type': 'application/json'
			}
		});
		const userType = response.data.userType;
		errorMessage.value = '';
		await router.push('/dashboard')
	} catch (error) {
		console.error(error);
	}
};
</script>

<style scoped>
.login-container {
	display: flex;
	justify-content: center;
	align-items: center;
	width: 100%;
	height: 100vh;
	background-color: #f4f4f9;
}

.login-box {
	background-color: #fff;
	padding: 40px;
	border-radius: 8px;
	box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
	width: 300px;
	text-align: center;
}

.login-box h1 {
	margin-bottom: 20px;
	color: #333;
}

.login-box input {
	width: 100%;
	padding: 10px;
	margin-bottom: 15px;
	border: 1px solid #ccc;
	border-radius: 4px;
	box-sizing: border-box;
}

.login-box button {
	width: 100%;
	padding: 10px;
	background-color: #007bff;
	color: #fff;
	border: none;
	border-radius: 4px;
	cursor: pointer;
}

.login-box button:hover {
	background-color: #0056b3;
}

.login-box router-link {
	display: block;
	margin-top: 20px;
	color: #007bff;
	text-decoration: none;
}

.login-box router-link:hover {
	text-decoration: underline;
}
/* 错误提示样式 */
.error-message {
	color: red;
	margin-bottom: 10px;
}
</style>