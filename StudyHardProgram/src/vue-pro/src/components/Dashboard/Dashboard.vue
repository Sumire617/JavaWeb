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
		index: '/dashboard',
		label: '个人信息',
		children: [
				{ index: '/dashboard/userinfo', label: "查看信息"},
				{ index: '/dashboard/userinfoChange', label: "修改信息" }
		]
	}
]);
</script>