<!--侧边栏选项框-->
<!--要实现的内容:-->
<!--1.主控台界面-->
<!--2.系统管理-->
<!--2.1 用户管理-->
<!--2.2 权限管理-->
<!--2.3 岗位管理-->
<!--2.4 登录日志-->
<!--2.5 通知公告-->
<!--2.6 个人中心-->
<!--3.系统监控-->
<!--3.1 安全预警-->
<!--3.2 数据库表-->
<!--3.3 操作日志-->
<template>
	<div class="left-bar">
		<el-scrollbar>
			<el-menu router>
				<template v-for="item in menuData" :key="item.index">
					<el-menu-item v-if="!item.children" :index="item.index">
						{{ item.label }}
					</el-menu-item>
					<el-sub-menu v-else :index="item.index">
						<template #title>{{ item.label }}</template>
						<el-menu-item v-for="child in item.children" :key="child.index" :index="child.index">
							{{ child.label }}
						</el-menu-item>
					</el-sub-menu>
				</template>
			</el-menu>
		</el-scrollbar>
	</div>
	<div class="content">
		<router-view></router-view>
	</div>
</template>
<script setup>
import {ref} from "vue";

const menuData = ref([
	{
		index: '/dashboard/console',
		label: '主控台',
	},
	{
		index: '/dashboard/userInfo',
		label: '个人信息',
	},
	{
		index: '/dashboard/userManage',
		label: '用户管理',
	},
]);
</script>
<style scoped>
.content {
	box-sizing: border-box;
	width: 100%;
}
</style>