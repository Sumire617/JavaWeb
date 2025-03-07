import { createRouter, createWebHistory } from 'vue-router';
import LoginView from "@/views/LoginView.vue";
import Dashboard from "@/components/Dashboard/Dashboard.vue";
import RegisterView from "@/views/RegisterView.vue";
import UserInfo from "@/components/Content/UserInfo.vue";
import UserInfoChange from "@/components/Content/UserInfoChange.vue";

const routes = [
  { path: '/', component: LoginView },
  { path: '/register', component: RegisterView },

  { path: '/dashboard',
    component: Dashboard,
    children: [
      {
        path: 'userinfo',
        component: UserInfo
      },
      {
        path: 'userinfoChange',
        component: UserInfoChange
      }

    ]
  },
]
const router = createRouter({
  history: createWebHistory(),
  routes,
})
export default router;