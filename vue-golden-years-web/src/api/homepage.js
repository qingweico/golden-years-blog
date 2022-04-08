import request from '@/utils/request'

/**获取作者所有的文章根据作者id**/
export function getAuthorArticleList(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/homepage',
        method: 'get',
        params
    })
}

/**获取作者基本信息根据作者id**/
export function getAuthorInfo(params) {
    return request({
        url: process.env.USER_API + '/user/getUserBasicInfo',
        method: 'get',
        params
    })
}

/**关注作者**/
export function attention(params) {
    return request({
        url: process.env.USER_API + '/fans/follow',
        method: 'post',
        data: params
    })
}

/**取消关注**/
export function cancelAttention(params) {
    return request({
        url: process.env.USER_API + '/fans/unfollow',
        method: 'post',
        data: params
    })
}

/**查询是否关注此作者**/
export function hasFollowThisAuthorOrNot(params) {
    return request({
        url: process.env.USER_API + '/fans/hasFollowThisAuthorOrNot',
        method: 'get',
        params
    })
}

/**用户主页文章排行*/
export function rank(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/rank/' + params,
        method: 'get',
        params
    })
}
