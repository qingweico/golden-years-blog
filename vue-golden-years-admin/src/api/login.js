import request from '@/utils/request'

export function login(param) {
    return request({
        url: process.env.GATEWAY_API + '/auth/login',
        method: 'post',
        data: param
    })
}

export function getInfo(param) {
    return request({
        url: process.env.GATEWAY_API + '/auth/getInfo',
        method: 'get',
        param
    })
}

export function getMenu(param) {
    return request({
        url: process.env.GATEWAY_API + '/auth/getMenu?name=admin',
        method: 'get',
    })
}

export function logout() {
    return request({
        url: process.env.GATEWAY_API + '/auth/logout',
        method: 'post'
    })
}


export function face(param) {
    return request({
        url: process.env.GATEWAY_API + '/auth/face',
        method: 'post',
        data: param
    })
}

export function getWebSiteName() {
    return request({
        url: process.env.GATEWAY_API + '/auth/getWebSiteName',
        method: 'get'
    })
}
