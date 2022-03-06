import request from '../utils/request'
export function getNewBlog (params) {
  return request({
    url: process.env.ARTICLE_API + '/portal/article/list',
    method: 'get',
    params
  })
}

export function getBlogByTime (params) {
  return request({
    url: process.env.ARTICLE_API + '/portal/article/getArticleByTime',
    method: 'get',
    params
  })
}
export function getBlogCategory () {
  return request({
    url: process.env.ARTICLE_API + '/portal/article/getCategoryList',
    method: 'get',
  })
}
export function getBlogCategoryWithBlogAmount () {
  return request({
    url: process.env.ARTICLE_API + '/portal/article/category',
    method: 'get',
  })
}
export function getHotBlog (params) {
  return request({
    url: process.env.WEB_API + '/index/getHotBlog',
    method: 'get',
    params
  })
}

export function getLink (params) {
  return request({
    url: process.env.ADMIN_API + '/fl/portal/list',
    method: 'get',
    params
  })
}