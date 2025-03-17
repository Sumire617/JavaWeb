import { createRouter, createWebHistory } from 'vue-router';
import LoginView from "@/views/LoginView.vue";
import Dashboard from "@/components/ADMIN/Dashboard/Dashboard.vue";
import RegisterView from "@/views/RegisterView.vue";
import UserInfo from "@/components/ADMIN/UserModule/UserInfo.vue";
import DashboardConsole from "@/components/ADMIN/Dashboard/DashboardConsole.vue";
import UserManage from "@/components/ADMIN/UserModule/UserManage.vue";
import JobManage from "@/components/ADMIN/JobModule/JobManage.vue";
import DataAnalysis from "@/components/ADMIN/DataModule/DataAnalysis.vue";
import Home from "@/components/USER/Home.vue";
import Interface from "@/components/EMPLOYER/Interface.vue";
import JobAudit from "@/components/ADMIN/JobModule/JobAudit.vue";
import Job from "@/components/ADMIN/JobModule/Job.vue";

const routes = [
  { path: '/', component: LoginView },
  { path: '/register', component: RegisterView },
  { path: '/Home', component: Home },
  { path: '/job/:id', component: () => import('@/components/USER/JobDetail.vue') },
  { path: '/recruiting-interface', component: Interface },
  {
    path: '/dashboard',
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
      {
        path: 'job',
        component: Job,
        children: [
          { path: '/dashboard/job/jobList', component: JobManage },
          { path: '/dashboard/job/JobAudit', component: JobAudit }
        ]
      },
      {
        path: 'dataAnalysis',
        component: DataAnalysis
      }
    ]
  },
  {
    path: '/employer',
    component: () => import('@/components/EMPLOYER/Interface.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'EmployerDashboard',
        component: () => import('@/components/EMPLOYER/Dashboard.vue')
      },
      {
        path: 'company',
        name: 'CompanyInfo',
        component: () => import('@/components/EMPLOYER/CompanyInfo.vue')
      },
      {
        path: 'staff',
        name: 'StaffManagement',
        component: () => import('@/components/EMPLOYER/StaffManagement.vue')
      },
      {
        path: 'jobs/post',
        name: 'PostJob',
        component: () => import('@/components/EMPLOYER/PostJob.vue')
      },
      {
        path: 'jobs/manage',
        name: 'ManageJobs',
        component: () => import('@/components/EMPLOYER/ManageJobs.vue')
      },
      {
        path: 'jobs/:jobId/applicants',
        name: 'JobApplicants',
        component: () => import('@/components/EMPLOYER/Applications.vue')
      },
      {
        path: 'jobs/edit/:jobId',
        name: 'EditJob',
        component: () => import('@/components/EMPLOYER/PostJob.vue'),
        props: (route) => ({ isEditing: true, jobId: route.params.jobId })
      },
      {
        path: 'analysis',
        name: 'DataAnalysis',
        component: () => import('@/components/EMPLOYER/DataAnalysis.vue')
      }
    ]
  },
]
const router = createRouter({
  history: createWebHistory(),
  routes,
})
export default router;