import request from '@/utils/request'

// ################################### 友情链接 ###################################
export function getFlList(params) {
    return request({
        url: process.env.GATEWAY_API + '/fl/list',
        method: 'get',
        params
    })
}

export function saveOrUpdate(params) {
    return request({
        url: process.env.GATEWAY_API + '/fl/saveOrUpdate',
        method: 'post',
        data: params
    })
}

export function deleteFl(params) {
    return request({
        url: process.env.GATEWAY_API + '/fl/delete/' + params,
        method: 'post',
    })
}

/**批量删除**/
export function batchDelete(params) {
    return request({
        url: process.env.GATEWAY_API + '/fl/delete/',
        method: 'post',
        data: params
    })
}