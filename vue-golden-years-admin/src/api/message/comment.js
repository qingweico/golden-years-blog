import request from '@/utils/request'

// ###################################### 评论API ######################################
export function getCommentList(params) {
    return request({
        url: process.env.GATEWAY_API + '/article/admin/query',
        method: 'get',
        params
    })
}
export function deleteComment(params) {
    return request({
        url: process.env.GATEWAY_API + '/article/admin/query',
        method: 'get',
        params
    })
}
export function deleteBatchComment(params) {
    return request({
        url: process.env.GATEWAY_API + '/article/admin/query',
        method: 'get',
        params
    })
}