import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: process.env.USER_API + '/user/query',
    method: 'get',
    params
  })
}
/**添加或者修改用户信息*/
export function saveOrUpdate(params) {
  return request({
    url: process.env.USER_API + '/user/updateUserInfo',
    method: 'post',
    data: params
  })
}
/**删除用户*/
export function deleteUser(params) {
  return request({
    url: process.env.ADMIN_API + '/user/delete',
    method: 'post',
    data: params
  })
}
/**重置用户密码*/
export function resetPassword(params) {
  return request({
    url: process.env.USER_API + '/user/resetPassword/' + params,
    method: 'post'
  })
}
/**用户头像上传*/
export function uploadFace(params) {
  return request({
    url: process.env.PIC_API + '/fs/uploadFace',
    headers: {'Content-Type': 'multipart/form-data',},
    method: 'post',
    data: params
  })
}
