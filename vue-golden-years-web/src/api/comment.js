import request from '@/utils/request'

export function deleteComment (params) {
  return request({
    url: process.env.ARTICLE_API + '/comment/user/delete',
    method: 'post',
    params,
    data: {}
  })
}
export function getCommentList (params) {
  return request({
    url: process.env.ARTICLE_API + '/comment/user/list',
    method: 'get',
    params
  })
}


