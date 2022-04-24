import router from './router'
import {constantRouterMap} from './router/index'
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
    if (whiteListActiveList.indexOf(to.path) !== -1) {
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
