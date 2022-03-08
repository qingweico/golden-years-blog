import request from '@/utils/request'
/**创作中心; 上传文章封面*/
export function uploadBlogCover(params) {
    return request({
        url: process.env.PIC_API + '/fs/uploadSomeFiles',
        method: 'post',
        data: params,
        headers: {'Content-Type': 'multipart/form-data'}
    })
}
/**创作中心; 添加新文章*/
export function publish(params) {
    return request({
        url: process.env.ARTICLE_API + '/article/user/publish',
        method: 'post',
        data: params,
    })
}
/**创作中心;查询文章列表*/
export function queryArticleList(params) {
    return request({
        url: process.env.ARTICLE_API + '/article/user/query',
        method: 'get',
        params,
    })
}
/**创作中心;撤回文章*/
export function withdrawBlog(params) {
    return request({
        url: process.env.ARTICLE_API + '/article/user/withdraw',
        method: 'get',
        params,
    })
}
/**创作中心;删除文章*/
export function deleteBlog(params) {
    return request({
        url: process.env.ARTICLE_API + '/article/user/delete',
        method: 'get',
        params,
    })
}
/**创作中心;获取我的评论列表*/
export function getUserCommentList(params) {
    return request({
        url: process.env.ARTICLE_API + '/comment/user/list',
        method: 'get',
        params,
    })
}
/**创作中心;删除我的品论*/
export function deleteComment(params) {
    return request({
        url: process.env.ARTICLE_API + '/comment/user/delete',
        method: 'get',
        params,
    })
}

