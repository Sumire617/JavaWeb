import { createRouter, createWebHistory } from 'vue-router';
import LoginView from "@/views/LoginView.vue";
import Dashboard from "@/components/Dashboard/Dashboard.vue";
import RegisterView from "@/views/RegisterView.vue";
import UserInfo from "@/components/Content/UserInfo.vue";
import DashboardConsole from "@/components/Console/DashboardConsole.vue";
import UserManage from "@/components/Manage/userManage.vue";

const routes = [
  { path: '/', component: LoginView },
  { path: '/register', component: RegisterView },

  { path: '/dashboard',
    component: Dashboard,
    redirect: 'dashboard/console',
    children: [
      {
        path: 'console',
        component: DashboardConsole
      },
      {
        path: 'userInfo',
        component: UserInfo
      },
      {
        path: 'userManage',
        component: UserManage
      },
    ]
  },
]
const router = createRouter({
  history: createWebHistory(),
  routes,
})
export default router;