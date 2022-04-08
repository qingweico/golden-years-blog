import request from '@/utils/request'

export function getCache(params) {
    return request({
        url: process.env.ADMIN_API + '/monitor/cache',
        method: 'get',
        params
    })
}
