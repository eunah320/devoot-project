import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import('@/views/HomePage.vue'),
        },
        {
            path: '/lecture',
            children: [
                {
                    path: '',
                    name: 'lectureSearch',
                    component: () => import('@/views/Lecture/LectureSearchPage.vue'),
                },
                {
                    path: ':id(\\d+)',
                    name: 'lectureDetail',
                    component: () => import('@/views/Lecture/LectureDetailPage.vue'),
                },
            ],
        },
        {
            path: '/timeline',
            name: 'timeline',
            component: () => import('@/views/TimelinePage.vue'),
        },
        {
            path: '/profile',
            children: [
                {
                    path: ':id',
                    name: 'profile',
                    component: () => import('@/views/ProfilePage.vue'),
                },
                {
                    path: 'edit',
                    name: 'profileEdit',
                    component: () => import('@/views/ProfileEditPage.vue'),
                },
            ],
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/LogInPage.vue'),
        },
    ],
})

export default router
