<template>
	<div class="register-container">
		<div class="register-box">
			<h1>注册</h1>
			<form @submit.prevent="register">
				<input v-model="username" placeholder="用户名" />
				<input v-model="password" placeholder="密码" type="password" />
				<input v-model="phoneNumber" type="number" placeholder="手机号">
				<input v-model="email" type="email" placeholder="邮箱">
				<select v-model="userType">
					<option value="NORMAL">普通用户</option>
					<option value="EMPLOYER">用人单位</option>
				</select>
				<button type="submit">注册</button>
			</form>
			<!-- 提示信息 -->
			<div v-if="message" class="message">{{ message }}</div>
		</div>
	</div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import router from "@/router/index.js";

const phoneNumber = ref('');
const email = ref('');
const username = ref('');
const password = ref('');
const userType = ref('NORMAL');
const message = ref(''); // 用于显示提示信息

const register = async () => {
	try {
		const response = await axios.post('http://localhost:8080/api/users/register', {
			username: username.value,
			password: password.value,
			userType: userType.value,
			email: email.value,
			phoneNumber: phoneNumber.value,
		});
		console.log(response.data);

		// 注册成功后显示提示信息
		message.value = "注册成功，等待返回登录界面";
		// 延迟跳转到登录页面
		setTimeout(() => {
			router.push('/');
		}, 2000); // 2秒后跳转
	} catch (error) {
		console.error(error);
		message.value = "注册失败，请稍后重试或联系管理员";
	}
};
</script>

<style scoped>
.register-container {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100vh;
	width: 100%;
	background-color: #f4f4f9;
}

.register-box {
	background-color: #fff;
	padding: 40px;
	border-radius: 8px;
	box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
	width: 300px;
	text-align: center;
}

.register-box h1 {
	margin-bottom: 20px;
	color: #333;
}

.register-box input,
.register-box select {
	width: 100%;
	padding: 10px;
	margin-bottom: 15px;
	border: 1px solid #ccc;
	border-radius: 4px;
	box-sizing: border-box;
}

.register-box button {
	width: 100%;
	padding: 10px;
	background-color: #007bff;
	color: #fff;
	border: none;
	border-radius: 4px;
	cursor: pointer;
}

.register-box button:hover {
	background-color: #0056b3;
}

/* 提示信息样式 */
.message {
	margin-top: 20px;
	padding: 10px;
	background-color: #d4edda;
	border: 1px solid #c3e6cb;
	color: #155724;
	border-radius: 4px;
	font-size: 14px;
}
</style>