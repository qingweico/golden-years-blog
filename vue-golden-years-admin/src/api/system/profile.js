import request from '@/utils/request'

// ################################### 个人信息 ###################################
export function updateUserPwd(oldPassword, newPassword) {
    const data = {
        oldPassword,
        newPassword
    }
    return request({
        url: process.env.GATEWAY_API + '/admin/updatePwd',
        method: 'post',
        data: data
    })
}

export function updateUserProfile(params) {
    return request({
        url: process.env.GATEWAY_API + '/admin/updateProfile',
        method: 'post',
        data: params
    })
}

/**上传头像**/
export function uploadAvatar(params) {
    return request({
        url: process.env.GATEWAY_API + '/fs/uploadFace',
        method: 'post',
        data: params
    })
}

/**
 * 上传人脸信息至GridFs
 */
export function uploadToGridFs(params) {
    return request({
        url: process.env.GATEWAY_API + '/fs/uploadToGridFs',
        method: 'post',
        data: params
    })
}
/**删除人脸信息**/
export function deleteFaceInfo(params) {
    return request({
        url: process.env.GATEWAY_API + '/admin/deleteFaceInfo/' + params,
        method: 'post',
        data: {}
    })
}
/**查看人脸信息**/
export function seeFace(params) {
    return request({
        url: process.env.GATEWAY_API + '/fs/readFace64InGridFS',
        method: 'get',
        params
    })
}