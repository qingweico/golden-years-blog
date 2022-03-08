import request from '@/utils/request'
/**上传文章封面*/
export function uploadBlogCover(params) {
    return request({
        url: process.env.PIC_API + '/fs/uploadSomeFiles',
        method: 'post',
        data: params,
        headers: {'Content-Type': 'multipart/form-data'}
    })
}
/**添加新文章*/
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
