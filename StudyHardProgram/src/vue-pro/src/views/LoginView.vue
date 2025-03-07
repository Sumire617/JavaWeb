<template>
	<div>
		<h1>登录</h1>
		<form @submit.prevent="login">
			<input v-model="username" type="text" placeholder="用户名" />
			<input v-model="password" type="password" placeholder="密码" />
			<button type="submit">登录</button>
		</form>
		<router-link to="/register">注册</router-link>
	</div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import router from "@/router/index.js";

const username = ref('');
const password = ref('');

const login = async () => {
	try {
		const response = await axios.post('http://localhost:8080/api/login', {
			username: username.value,
			password: password.value
		});
		const userType = response.data.userType;
		await router.push('/dashboard')
	} catch (error) {
		console.error(error);
	}
};
</script>

<style scoped>
/* 可以添加一些样式 */
</style>