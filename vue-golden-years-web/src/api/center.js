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

/**创作中心; 添加新文章后或者修改文章*/
export function publishOrUpdate(params) {
    return request({
        url: process.env.ARTICLE_API + '/article/user/publishOrUpdate',
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
        method: 'post',
        params,
    })
}

/**创作中心;删除文章*/
export function deleteBlog(params) {
    return request({
        url: process.env.ARTICLE_API + '/article/user/delete',
        method: 'post',
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

/**创作中心;删除我的评论*/
export function deleteComment(params) {
    return request({
        url: process.env.ARTICLE_API + '/comment/user/delete/' + params,
        method: 'post'
    })
}

/**创作中心;获取标签列表*/
export function getTagList(params) {
    return request({
        url: process.env.ARTICLE_API + '/tag/list',
        method: 'get',
        params,
    })
}

// ################################## 账户设置 ##################################
/**修改用户密码*/
export function alterPwd(params) {
    return request({
        url: process.env.USER_API + '/user/alterPwd',
        method: 'post',
        data: params,
    })
}