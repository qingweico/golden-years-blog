import request from '@/utils/request'
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
