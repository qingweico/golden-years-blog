import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)
/* Layout */
import Layout from '../views/layout/Layout'

export const constantRouterMap = [
    {path: '/login', component: () => import('../views/login/index'), hidden: true},
    {path: '/404', component: () => import('../views/404'), hidden: true},
    {path: '/face', component: () => import('../views/login/face'), hidden: true},

    {
        path: '/',
        component: Layout,
        redirect: '/dashboard',
        name: '首页',
        children: [{
            path: 'dashboard',
            component: () => import('@/views/dashboard/index'),
            meta: {title: '仪表盘', icon: 'dashboard'}
        }]
    },
    {
        path: '/blog',
        component: Layout,
        redirect: '/blog/article',
        name: '博客管理',
        meta: {title: '博客管理', icon: 'edit'},
        children: [
            {
                path: 'article',
                name: '文章管理',
                component: () => import('@/views/blog/article'),
                meta: {title: '文章管理', icon: 'edit'}
            },
            {
                path: 'category',
                name: '分类管理',
                component: () => import('@/views/blog/category'),
                meta: {title: '分类管理', icon: 'sort'}
            },
            {
                path: 'tag',
                name: '标签管理',
                component: () => import('@/views/blog/tag'),
                meta: {title: '标签管理', icon: 'sort'}
            }
        ]
    },


    {
        path: '/resource',
        component: Layout,
        redirect: '/resource/resourceSort',
        name: '资源管理',
        meta: {title: '资源管理', icon: 'resource'},
        children: [
            {
                path: 'file',
                name: '网盘管理',
                component: () => import('@/views/resource/file/File'),
                meta: {title: '网盘管理', icon: 'table'}
            }
        ]
    },
    {
        path: '/user',
        component: Layout,
        redirect: '/user/user',
        meta: {title: '用户管理'},
        children: [
            {
                path: 'user',
                name: '用户管理',
                component: () => import('@/views/user/user'),
                meta: {title: '用户管理', icon: 'table'}
            }
        ]
    },
    {
        path: '/message',
        component: Layout,
        redirect: '/message/comment',
        name: '消息管理',
        meta: {title: '消息管理'},
        children: [
            {
                path: 'comment',
                name: '评论管理',
                component: () => import('@/views/message/comment'),
                meta: {title: '评论管理', icon: 'table'}
            }
        ]
    },
    {
        path: '/monitor',
        component: Layout,
        name: '监控中心',
        meta: {title: '监控中心', icon: 'log'},
        children: [
            {
                path: 'server',
                name: '服务器监控',
                component: () => import('@/views/monitor/server'),
                meta: {title: '服务器监控'}
            },
            {
                path: 'cache',
                name: '缓存监控',
                component: () => import('@/views/monitor/cache'),
                meta: {title: '缓存监控'}
            },
            {
                path: 'druid',
                name: '数据监控',
                component: () => import('@/views/monitor/druid'),
                meta: {title: '数据监控'}
            },
            {
                path: 'elasticsearch',
                name: 'elasticsearch',
                component: () => import('@/views/monitor/elasticsearch'),
                meta: {title: 'elasticsearch'}
            },
        ]
    },
    {
        path: '/system',
        component: Layout,
        children: [
            {
                path: 'profile',
                name: '个人信息',
                component: () => import('@/views/system/profile'),
                meta: {title: '个人信息'}
            },
            {
                path: 'fl',
                name: '友情链接',
                component: () => import('@/views/system/fl'),
                meta: {title: '友情链接'}
            },
            {
                path: 'systemConfig',
                name: '系统配置',
                component: () => import('@/views/system/SystemConfig'),
                meta: {title: '系统配置'}
            },
            {
                path: 'webConfig',
                name: '网站配置',
                component: () => import('@/views/system/WebConfig'),
                meta: {title: '网站配置'}
            }
        ]
    },
    {path: '*', redirect: '/404', hidden: true}
]

const router = new VueRouter({
    // mode: 'history',
    scrollBehavior: () => ({y: 0}),
    routes: constantRouterMap
})

const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location, onResolve, onReject) {
    if (onResolve || onReject)
        return originalPush.call(this, location, onResolve, onReject);
    return originalPush.call(this, location).catch((err) => err);
}
export default router
