import request from '@/utils/request'

export function getSystemConfig(params) {
    return request({
        url: process.env.GATEWAY_API + '/systemConfig/getSystemConfig',
        method: 'get',
        params
    })
}

export function cleanRedisByKey(params) {
    return request({
        url: process.env.GATEWAY_API + '/systemConfig/cleanRedisByKey',
        method: 'post',
        data: params
    })
}

export function alterSystemConfig(params) {
    return request({
        url: process.env.GATEWAY_API + '/systemConfig/alterSystemConfig',
        method: 'post',
        data: params
    })
}
