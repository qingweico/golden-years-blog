import request from '@/utils/request'

export function getBlogById(params) {
    return request({
        url: process.env.ARTICLE_API + '/portal/article/detail',
        method: 'get',
        params
    })
}

export function uploadBlogCover(params) {
    return request({
        url: process.env.PIC_API + '/fs/uploadSomeFiles',
        method: 'post',
        data: params,
        headers: {'Content-Type': 'multipart/form-data'}
    })
}

export function publish(params) {
    return request({
        url: process.env.ARTICLE_API + '/article/user/publish',
        method: 'post',
        data: params,
    })
}