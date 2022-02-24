import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)
/* Layout */
import Layout from '../views/layout/Layout'
const routes = [
    { path: '/login', component: () => import('@/views/login/index'), hidden: true },
    { path: '/404', component: () => import('@/views/404'), hidden: true },

    {
        path: '/',
        component: Layout,
        redirect: '/dashboard',
        name: '首页',
        children: [{
            path: 'dashboard',
            component: () => import('@/views/dashboard/index'),
            meta: { title: '仪表盘', icon: 'dashboard' }
        }]
    },
    {
        path: '/blog',
        component: Layout,
        redirect: '/blog/blog',
        name: '博客管理',
        meta: { title: '博客管理', icon: 'edit' },
        children: [
            {
                path: 'blog',
                name: '博客管理',
                component: () => import('@/views/blog/blog'),
                meta: { title: '博客管理', icon: 'edit' }
            },
            {
                path: 'category',
                name: '分类管理',
                component: () => import('@/views/blog/blogSort'),
                meta: { title: '分类管理', icon: 'sort' }
            },
            {
                path: 'friendLink',
                name: '友情链接',
                component: () => import('@/views/blog/friendLink'),
                meta: { title: '友情链接', icon: 'table' }
            }
        ]
    },


    {
        path: '/resource',
        component: Layout,
        redirect: '/resource/resourceSort',
        name: '资源管理',
        meta: { title: '资源管理', icon: 'resource' },
        children: [
            {
                path: 'file',
                name: '网盘管理',
                component: () => import('@/views/resource/file/File'),
                meta: { title: '网盘管理', icon: 'table' }
            }
        ]
    },
    {
        path: '/user',
        component: Layout,
        redirect: '/user/user',
        name: '用户管理',
        meta: { title: '用户管理', icon: 'user1' },
        children: [
            {
                path: 'user',
                name: '用户管理',
                component: () => import('@/views/user/user'),
                meta: { title: '用户管理', icon: 'table' }
            },
        ]
    },
    {
        path: '/restapi',
        component: Layout,
        redirect: '/restapi/adminRestApi',
        name: '接口管理',
        meta: { title: '接口管理', icon: 'restapi' },
        children: [
            {
                path: 'adminRestApi',
                name: 'Admin接口',
                component: () => import('@/views/restapi/adminRestApi'),
                meta: { title: 'Admin接口', icon: 'table' }
            },
            {
                path: 'pictureRestApi',
                name: 'Picture接口',
                component: () => import('@/views/restapi/pictureRestApi'),
                meta: { title: 'Picture接口', icon: 'table' }
            },
            {
                path: 'webRestApi',
                name: 'Web接口',
                component: () => import('@/views/restapi/webRestApi'),
                meta: { title: 'Web接口', icon: 'table' }
            }
        ]
    },
    {
        path: '/monitor',
        component: Layout,
        redirect: '/monitor/springBootAdmin',
        name: '监控中心',
        meta: { title: '监控中心', icon: 'log' },
        children: [
            {
                path: 'OnlineAdmin',
                name: '在线用户',
                component: () => import('@/views/monitor/OnlineAdmin'),
                meta: { title: '在线用户', icon: 'log' }
            },
            {
                path: 'ServerMonitor',
                name: '服务器监控',
                component: () => import('@/views/monitor/ServerMonitor'),
                meta: { title: '服务器监控', icon: 'exception' }
            },
            {
                path: 'ElasticSearch',
                name: 'ElasticSearch',
                component: () => import('@/views/monitor/ElasticSearch'),
                meta: { title: 'ElasticSearch', icon: 'exception' }
            },
        ]
    },



    { path: '*', redirect: '/404', hidden: true }
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

export default router
