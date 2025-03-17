<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const isCollapse = ref(false);

const handleCommand = (command) => {
  if (command === 'logout') {
    // 实现登出逻辑
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    router.push('/');
  }
};
</script>

<template>
  <el-container class="interface-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <div class="logo-container">
        <img src="@/assets/logo.svg" alt="Logo" class="logo" v-if="!isCollapse">
        <img src="@/assets/logo.svg" alt="Logo" class="logo-small" v-else>
      </div>
      
      <el-menu
        :default-active="$route.path"
        class="el-menu-vertical"
        :collapse="isCollapse"
        router
      >
        <el-menu-item index="/employer/dashboard">
          <el-icon><Monitor /></el-icon>
          <template #title>主控板</template>
        </el-menu-item>

        <el-menu-item index="/employer/company">
          <el-icon><Office /></el-icon>
          <template #title>单位信息</template>
        </el-menu-item>

        <el-menu-item index="/employer/staff">
          <el-icon><User /></el-icon>
          <template #title>员工管理</template>
        </el-menu-item>

        <el-sub-menu index="jobs">
          <template #title>
            <el-icon><Briefcase /></el-icon>
            <span>岗位管理</span>
          </template>
          <el-menu-item index="/employer/jobs/post">发布岗位</el-menu-item>
          <el-menu-item index="/employer/jobs/manage">岗位管理</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/employer/analysis">
          <el-icon><TrendCharts /></el-icon>
          <template #title>数据分析</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主要内容区域 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            type="text"
            @click="isCollapse = !isCollapse"
            class="collapse-btn"
          >
            <el-icon>
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
          </el-button>
          <breadcrumb />
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="username">{{ userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.interface-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
}

.logo {
  height: 32px;
  margin: 14px;
}

.logo-small {
  height: 32px;
  margin: 14px 16px;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  padding: 0;
  margin-right: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
  color: #606266;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu--collapse) {
  width: 64px;
}

:deep(.el-menu-item.is-active) {
  background-color: #1890ff;
  color: #fff;
}

:deep(.el-menu-item:hover) {
  background-color: #263445;
}
</style>