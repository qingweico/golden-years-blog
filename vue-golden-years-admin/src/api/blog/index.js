import request from '@/utils/request'

// ###################################### 文章API ######################################
export function conditionalQueryArticle(params) {
    return request({
        url: process.env.GATEWAY_API + '/article/admin/query',
        method: 'get',
        params
    })
}

export function deleteBatchArticle(params) {
    return request({
        url: process.env.GATEWAY_API + '/blog/deleteBatch',
        method: 'post',
        data: params
    })
}

/**审核文章*/
export function reviewStatus(params) {
    return request({
        url: process.env.GATEWAY_API + '/article/admin/doReview',
        method: 'post',
        params
    })
}

/**重新审核文章*/
export function reReview(params) {
    return request({
        url: process.env.GATEWAY_API + '/article/admin/reReview',
        method: 'post',
        params
    })
}

/**删除文章*/
export function deleteArticle(params) {
    return request({
        url: process.env.GATEWAY_API + '/article/admin/delete',
        method: 'post',
        params
    })
}

/**撤回删除*/
export function withdrawDelete(params) {
    return request({
        url: process.env.GATEWAY_API + '/article/admin/withdrawDelete',
        method: 'post',
        params
    })
}

// ###################################### 标签API ######################################
/**新增标签*/
export function saveOrUpdateTag(params) {
    return request({
        url: process.env.GATEWAY_API + '/tag/saveOrUpdate',
        method: 'post',
        data: params
    })
}

/**获取标签列表*/
export function getTagList(params) {
    return request({
        url: process.env.GATEWAY_API + '/tag/query',
        method: 'get',
        params
    })
}

/**删除标签*/
export function deleteTag(params) {
    return request({
        url: process.env.GATEWAY_API + '/tag/delete/' + params,
        method: 'post'
    })
}

/**批量删除标签*/
export function batchDeleteTag(params) {
    return request({
        url: process.env.GATEWAY_API + '/tag/delete',
        method: 'post',
        data: params
    })
}

// ###################################### 类别API ######################################
/**文章类别(带有文章数量的类别列表)*/
export function getCategoryListWithArticleCount(params) {
    return request({
        url: process.env.GATEWAY_API + '/category/getCategoryList',
        method: 'get',
        params
    })
}

/**文章类别(带有文章数量的类别列表)*/
export function getCategoryList(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/getCategoryList',
        method: 'get',
        params
    })
}

/**获取所有的标签*/
export function getAllTag(params) {
    return request({
        url: process.env.GATEWAY_API + '/tag/list',
        method: 'get',
        params
    })
}

/**更新或者添加文章类别*/
export function saveOrUpdateCategory(params) {
    return request({
        url: process.env.GATEWAY_API + '/category/saveOrUpdateCategory',
        method: 'post',
        data: params
    })
}

/**删除文章类别*/
export function deleteCategory(params) {
    return request({
        url: process.env.GATEWAY_API + '/category/deleteCategory/' + params,
        method: 'post',
    })
}

/**根据文章数量排序*/
export function sortArticleByCount(params) {
    return request({
        url: process.env.GATEWAY_API + '/category/sortByArticleCount',
        method: 'post',
        data: {}
    })
}

/**批量删除类别*/
export function batchDeleteCategory(params) {
    return request({
        url: process.env.GATEWAY_API + '/category/delete',
        method: 'post',
        data: params
    })
}