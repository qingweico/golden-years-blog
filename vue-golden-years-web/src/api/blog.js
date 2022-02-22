import request from '@/utils/request'

export function getBlogById (params) {
  return request({
    url: process.env.VUE_APP_BASE_API + '/portal/article/detail',
    method: 'get',
    params
  })
}



export function praiseBlogByUid (params) {
  return request({
    url: process.env.WEB_API + '/content/praiseBlogByUid',
    method: 'get',
    params
  })
}

export function getBlogPraiseCountByUid (params) {
  return request({
    url: process.env.WEB_API + '/content/getBlogPraiseCountByUid',
    method: 'get',
    params
  })
}
export function uploadBlogCover (params) {
  return request({
    url: process.env.WEB_API + '/fs/uploadSomeFiles',
    method: 'post',
    params,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}