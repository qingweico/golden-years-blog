import request from '@/utils/request'

export function passive(params) {
    return request({
        url: process.env.USER_API + '/fans/passive',
        method: 'post',
        params,
        data: {}
    })
}

export function queryFansList(params) {
    return request({
        url: process.env.USER_API + '/fans/query',
        method: 'get',
        params,
    })
}

export function queryRatioByRegion(params) {
    return request({
        url: process.env.USER_API + '/fans/queryRatioByRegion',
        method: 'get',
        params,
    })
}

export function queryRatio(params) {
    return request({
        url: process.env.USER_API + '/fans/queryRatio',
        method: 'get',
        params,
    })
}

