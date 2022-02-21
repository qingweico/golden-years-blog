import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/home.vue'
import UserLayout from "@/components/layouts/UserLayout";

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
        children: [
            {path: '/', component: () => import('@/views/index')},
            { path: '/detail', component: () => import('@/views/detail') },
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
                component: () => import(/* webpackChunkName: "user" */ '@/views/user/Login')
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
