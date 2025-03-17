{
    path: '/employer',
        component: () => import('@/components/EMPLOYER/Interface.vue'),
            children: [
                {
                    path: 'notifications/publish',
                    name: 'PublishNotification',
                    component: () => import('@/views/employer/notification/NotificationPublish.vue')
                },
            ]
} 