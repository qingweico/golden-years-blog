import router from './router'
import store from './store'
import {constantRouterMap} from './router/index'
import {getCookie} from "./utils/cookie";
// 不重定向白名单
const whiteList = ['/login']
const whiteListActiveList = ['/', '/401', '/404', '/500']
const allList = []

router.beforeEach((to, from, next) => {

    if (allList.length === 0) {
        for (let a = 0; a < constantRouterMap.length; a++) {
            if (constantRouterMap[a].children) {
                let childrenList = constantRouterMap[a].children
                for (let b = 0; b < childrenList.length; b++) {
                    allList.push(childrenList[b].path)
                }
            } else {
                allList.push(constantRouterMap[a].path)
            }
        }
    }

    // 向白名单中添加内容
    // 从cookie中获取可以访问的导航栏列表
    let webNavbarList = JSON.parse(getCookie("webNavbarList"))
    const activeList = []
    if (webNavbarList) {
        for (let c = 0; c < webNavbarList.length; c++) {
            activeList.push(webNavbarList[c].url)
        }
    }

    if (whiteListActiveList.indexOf(to.path) !== -1) {
        next()
    } else if (activeList.indexOf(to.path) !== -1) {
        next()
    } else if (whiteList.indexOf(to.path) !== -1) {
        next()
    } else {
        if (allList.indexOf(to.path) !== -1) {
            next({path: '/401'})
        } else {
            next({path: '/404'})
        }
    }
})
