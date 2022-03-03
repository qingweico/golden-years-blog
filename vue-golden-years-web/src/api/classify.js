import request from '@/utils/request'

export function getBlogCategoryList(params) {
  return request({
    url: process.env.WEB_API + '/classify/getBlogSortList',
    method: 'get',
    params
  })
}

export function getBlogListByCategoryId(params) {
  return request({
    url: process.env.WEB_API + '/classify/getArticleByBlogSortUid',
    method: 'get',
    params
  })
}