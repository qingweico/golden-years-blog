import request from '@/utils/request'

/**文章下删除评论**/
export function deleteComment (params) {
  return request({
    url: process.env.GATEWAY_API + '/comment/delete',
    method: 'post',
    params,
    data: {}
  })
}
/**查看文章下评论列表**/
export function getCommentList (params) {
  return request({
    url: process.env.GATEWAY_API + '/comment/list',
    method: 'get',
    params
  })
}
/**发表新列表**/
export function publishComment (params) {
  return request({
    url: process.env.GATEWAY_API + '/comment/publish',
    method: 'post',
    data: params
  })
}


