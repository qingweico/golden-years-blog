import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/home.vue'
import WritingCenterLayout from "@/components/Layouts/WritingCenterLayout";

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
        children: [
            {path: '/', component: () => import('@/views/index')},
            {path: '/detail', component: () => import('@/views/detail')},
            {path: '/homepage', component: () => import('@/views/homepage')},
            {path: '/time', component: () => import('@/views/time')},
            {path: '/archive', component: () => import('@/views/archive')},
            {path: '/classify', component: () => import('@/views/classify')},
        ]
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/User/Login'),
    },

    {
        path: '/center',
        component: WritingCenterLayout,
        hidden: true,
        redirect: '/center/manage',
        children: [
            {
                path: 'manage',
                name: 'manage',
                component: () => import('@/views/WritingCenter/manage')
            },
            {
                path: 'comment',
                name: 'comment',
                component: () => import('@/views/WritingCenter/comment')
            },
            {
                path: 'account',
                name: 'account',
                component: () => import('@/views/WritingCenter/account')
            },
            {
                path: 'loginlog',
                name: 'loginlog',
                component: () => import('@/views/WritingCenter/loginlog')
            },
            {
                path: 'fans',
                name: 'fans',
                component: () => import('@/views/WritingCenter/fans')
            },
            {
                path: 'fansCharts',
                name: 'fansCharts',
                component: () => import('@/views/WritingCenter/fansCharts')
            }
        ]
    }
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

export default router
