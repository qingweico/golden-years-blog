import request from '@/utils/request'
// ################################### 友情链接 ###################################
export function getFlList(params) {
    return request({
        url: process.env.ADMIN_API + '/fl/list',
        method: 'get',
        params
    })
}

export function saveOrUpdate(params) {
    return request({
        url: process.env.ADMIN_API + '/fl/saveOrUpdate',
        method: 'post',
        data: params
    })
}

export function deleteFl(params) {
    return request({
        url: process.env.ADMIN_API + '/fl/delete/' + params,
        method: 'post',
    })
}
// ################################### 个人信息 ###################################
export function updateUserPwd(oldPassword, newPassword) {
    const data = {
        oldPassword,
        newPassword
    }
    return request({
        url: process.env.ADMIN_API + '/admin/updatePwd',
        method: 'post',
        data: data
    })
}
export function updateUserProfile(params) {
    return request({
        url: process.env.ADMIN_API + '/admin/updateProfile',
        method: 'post',
        data: params
    })
}
/**上传头像**/
export function uploadAvatar(params) {
    return request({
        url: process.env.PIC_API + '/fs/uploadFace',
        method: 'post',
        data: params
    })
}