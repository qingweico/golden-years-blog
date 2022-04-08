import request from '../utils/request'

/**主站文章列表*/
export function getNewBlog(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/search',
        method: 'get',
        params
    })
}

/**根据年月查询文章*/
export function getBlogByTime(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/getArticleByTime',
        method: 'get',
        params
    })
}

/**归档时间列表*/
export function getArchiveTimeList(userId) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/getArchiveTimeList?userId=' + userId,
        method: 'get',
    })
}

/**主页时间线*/
export function timeline(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/timeline',
        method: 'get',
        params
    })
}
/**获取标签列表*/
export function getHotTag(params) {
    return request({
        url: process.env.ARTICLE_API + '/tag/list',
        method: 'get',
        params
    })
}
/**文章类别*/
export function getBlogCategory() {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/getCategoryList',
        method: 'get',
    })
}

/**带有文章数量的文章类别*/
export function getBlogCategoryWithBlogAmount() {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/category/articleCount',
        method: 'get',
    })
}

/**友情链接*/
export function getLink(params) {
    return request({
        url: process.env.ADMIN_API + '/fl/portal/list',
        method: 'get',
        params
    })
}
/**主页分类; 根据类别id查询文章列表*/
export function getBlogListByCategoryId(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/getArticleListByCategoryId',
        method: 'get',
        params
    })
}

/**通过文章id查询文章详情*/
export function getBlogById(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/detail',
        method: 'get',
        params
    })
}
/**主页文章排行榜*/
export function rank(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/rank',
        method: 'get',
        params
    })
}

// ##################################### 搜索服务 #####################################
/**使用 sql 搜索文章*/
export function searchBlog(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/search',
        method: 'get',
        params
    })
}

/**使用 es 搜索文章*/
export function searchBlogByES(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/es/search',
        method: 'get',
        params
    })
}