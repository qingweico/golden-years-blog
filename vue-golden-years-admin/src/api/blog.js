import request from '@/utils/request'

// ###################################### 文章API ######################################
export function getBlogList(params) {
  return request({
    url: process.env.ARTICLE_API + '/article/admin/query',
    method: 'get',
    params
  })
}
export function deleteBatchArticle(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/deleteBatch',
    method: 'post',
    data: params
  })
}

/**审核文章*/
export function reviewStatus(params) {
  return request({
    url: process.env.ARTICLE_API + '/article/admin/doReview',
    method: 'post',
    params
  })
}
/**重新审核文章*/
export function reReview(params) {
  return request({
    url: process.env.ARTICLE_API + '/article/admin/reReview',
    method: 'post',
    params
  })
}
/**删除文章*/
export function deleteArticle(params) {
  return request({
    url: process.env.ARTICLE_API + '/article/admin/delete',
    method: 'post',
    params
  })
}
/**撤回删除*/
export function withdrawDelete(params) {
  return request({
    url: process.env.ARTICLE_API + '/article/admin/withdrawDelete',
    method: 'post',
    params
  })
}
// ###################################### 标签API ######################################
/**新增标签*/
export function saveOrUpdateTag(params) {
  return request({
    url: process.env.ARTICLE_API + '/tag/saveOrUpdate',
    method: 'post',
    data: params
  })
}
/**获取标签列表*/
export function getTagList(params) {
  return request({
    url: process.env.ARTICLE_API + '/tag/query',
    method: 'get',
    params
  })
}
/**删除标签*/
export function deleteTag(params) {
  return request({
    url: process.env.ARTICLE_API + '/tag/delete/' + params,
    method: 'post'
  })
}
// ###################################### 类别API ######################################
/**文章类别*/
export function getBlogCategory (params) {
  return request({
    url: process.env.ADMIN_API + '/category/getCategoryList',
    method: 'get',
    params
  })
}
/**文章类别(redis)*/
export function getCategoryList (params) {
  return request({
    url: process.env.ARTICLE_API + '/portal/article/getCategoryList',
    method: 'get',
    params
  })
}
/**带有文章数量的文章类别*/
export function getBlogCategoryWithBlogAmount (params) {
  return request({
    url: process.env.ARTICLE_API + '/portal/article/category/articleCount',
    method: 'get',
    params
  })
}
/**更新或者添加文章类别*/
export function saveOrUpdateCategory (params) {
  return request({
    url: process.env.ADMIN_API + '/category/saveOrUpdateCategory',
    method: 'post',
    data: params
  })
}
/**删除文章类别*/
export function deleteCategory (params) {
  return request({
    url: process.env.ADMIN_API + '/category/deleteCategory/' + params,
    method: 'post',
  })
}