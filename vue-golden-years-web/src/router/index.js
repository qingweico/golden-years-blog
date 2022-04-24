import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '@/views/home.vue'
import WritingCenterLayout from "@/components/Layouts/WritingCenterLayout";

Vue.use(VueRouter)

export const constantRouterMap = [
    {
        path: '/',
        component: Home,
        children: [
            {path: '/', component: () => import('@/views/index')},
            {path: '/detail', component: () => import('@/views/detail')},
            {path: '/homepage', component: () => import('@/views/homepage')},
            {path: '/time', component: () => import('@/views/time')},
            {path: '/archive', component: () => import('@/views/archive')},
            {path: '/classify', component: () => import('@/views/classify')},
            {path: '/rank', component: () => import('@/views/rank')},
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
                path: 'collect',
                name: 'collect',
                component: () => import('@/views/WritingCenter/collect')
            },
            {
                path: 'history',
                name: 'history',
                component: () => import('@/views/WritingCenter/history')
            },
            {
                path: 'account',
                name: 'account',
                redirect: '/center/account/setting',
                component: () => import('@/views/WritingCenter/account'),
                children: [
                    {
                        path: 'setting',
                        name: 'setting',
                        component: () => import('@/views/WritingCenter/account/setting')
                    },
                    {
                        path: 'password',
                        name: 'password',
                        component: () => import('@/views/WritingCenter/account/password')
                    },
                    {
                        path: 'cellphone',
                        name: 'cellphone',
                        component: () => import('@/views/WritingCenter/account/cellphone')
                    },
                    {
                        path: 'logout',
                        name: 'logout',
                        component: () => import('@/views/WritingCenter/account/logout')
                    },
                ]
            },

            {
                path: 'profile',
                name: 'profile',
                component: () => import('@/views/WritingCenter/profile')
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
    },
    {path: '/404', component: () => import('@/views/404')},
    {path: '/500', component: () => import('@/views/500')},
    {path: '/502', component: () => import('@/views/502')},
    {path: '/*', component: () => import('@/views/404')}
]

const router = new VueRouter({
    // mode: 'history',
    scrollBehavior: () => ({y: 0}),
    routes: constantRouterMap
})
const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(to) {
    return VueRouterPush.call(this, to).catch(err => err)
}
export default router
