import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/home.vue'
import UserLayout from "@/components/Layouts/UserLayout";
import WritingCenterLayout from "@/components/Layouts/WritingCenterLayout";

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
        children: [
            {path: '/', component: () => import('@/views/index')},
            {path: 'detail', component: () => import('@/views/detail')},
            {path: 'homepage', component: () => import('@/views/homepage')},
        ]
    },
    {
        path: '/user',
        component: UserLayout,
        redirect: '/user/login',
        hidden: true,
        children: [
            {
                path: 'login',
                name: 'login',
                component: () => import(/* webpackChunkName: "user" */ '@/views/User/Login')
            }
        ]
    },
    {
        path: '/center',
        component: WritingCenterLayout,
        hidden: true,
        children: [
            {
                path: 'manage',
                name: 'manage',
                component: () => import(/* webpackChunkName: "user" */ '@/views/WritingCenter/manage')
            },
            {
                path: 'newly',
                name: 'newly',
                component: () => import(/* webpackChunkName: "user" */ '@/views/WritingCenter/newly')
            },
            {
                path: 'comment',
                name: 'comment',
                component: () => import(/* webpackChunkName: "user" */ '@/views/WritingCenter/comment')
            },
            {
                path: 'account',
                name: 'account',
                component: () => import(/* webpackChunkName: "user" */ '@/views/WritingCenter/account')
            },
            {
                path: 'loginlog',
                name: 'loginlog',
                component: () => import(/* webpackChunkName: "user" */ '@/views/WritingCenter/loginlog')
            },
            {
                path: 'fans',
                name: 'fans',
                component: () => import(/* webpackChunkName: "user" */ '@/views/WritingCenter/fans')
            },
            {
                path: 'fansCharts',
                name: 'fansCharts',
                component: () => import(/* webpackChunkName: "user" */ '@/views/WritingCenter/fansCharts')
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
