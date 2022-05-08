import request from "@/utils/request";

/**通过文章id查询文章详情*/
export function getBlogById(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/detail',
        method: 'get',
        params
    })
}

/**文章阅读数累加*/
export function readArticle(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/readArticle/' + params,
        method: 'post',
        data: {}
    })
}

// ############################################# 点赞 #############################################
/**点赞文章*/
export function praiseArticleById(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/praise',
        method: 'post',
        data: params
    })
}

/**取消点赞文章*/
export function cancelStar(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/unstar',
        method: 'post',
        data: params
    })
}

/**获取文章点赞数*/
export function getArticleStarCounts(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/getArticleStarCounts',
        method: 'get',
        params
    })
}

/**判断用户是否点赞了该文章*/
export function isStarThisArticle(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/isStar',
        method: 'post',
        data: params
    })
}

// ############################################# 收藏 #############################################
/**获取文章收藏数*/
export function getArticleCollectCounts(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/getArticleCollectCounts',
        method: 'get',
        params
    })
}

/**判断用户是否收藏了该文章*/
export function isCollectThisArticle(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/isCollect',
        method: 'post',
        data: params
    })
}

/**收藏文章*/
export function collect(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/collect',
        method: 'post',
        data: params
    })
}

/**取消收藏文章*/
export function cancelCollect(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/uncollect',
        method: 'post',
        data: params
    })
}

/**获取我的收藏夹*/
export function getFavorites(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/getFavorites',
        method: 'get',
        params
    })
}

/**创建收藏夹*/
export function createFavorites(params) {
    return request({
        url: process.env.GATEWAY_API + '/portal/article/createFavorites',
        method: 'post',
        data: params
    })
}
/**增加一条文章浏览历史*/
export function incArticleHistory(params) {
    return request({
        url: process.env.GATEWAY_API + '/article/history/inc',
        method: 'post',
        data: params
    })
}