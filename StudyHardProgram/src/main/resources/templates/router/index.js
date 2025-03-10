import Vue from 'vue';
import VueRouter from 'vue-router';
import JobList from '../components/JobList.vue';

Vue.use(VueRouter);

const routes = [
    {
        path: '/',
        name: 'Home',
        component: JobList
    },
    {
        path: '/jobs',
        name: 'Jobs',
        component: JobList
    },
    {
        path: '/jobs/:id',
        name: 'JobDetail',
        component: () => import('../components/JobDetail.vue')
    },
    {
        path: '/jobs/:id/apply',
        name: 'JobApply',
        component: () => import('../components/JobApply.vue'),
        meta: { requiresAuth: true }
    }
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        // 检查用户是否已登录
        if (!store.state.user) {
            next({
                path: '/login',
                query: { redirect: to.fullPath }
            });
        } else {
            next();
        }
    } else {
        next();
    }
});

export default router; 