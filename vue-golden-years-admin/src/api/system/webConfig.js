import request from '@/utils/request'

// ################################### 网站配置 ###################################

export function getWebConfig(params) {
    return request({
        url: process.env.GATEWAY_API + '/webConfig/getWebConfig',
        method: 'get',
        params
    })
}

export function alterWebConfig(params) {
    return request({
        url: process.env.GATEWAY_API + '/webConfig/alterWebConfig',
        method: 'post',
        data: params
    })
}
