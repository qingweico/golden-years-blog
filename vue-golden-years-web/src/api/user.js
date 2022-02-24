import request from '../utils/request'

/**
 * 第三方登录
 * @param params
 */
export function login(params) {
  return request({
    url: process.env.WEB_API + '/oauth/render',
    method: 'post',
    params
  })
}

export function authVerify(params) {
  return request({
    url: process.env.USER_API + '/auth/verify/' + params,
    method: 'get',
  })
}

export function editUser(params) {
  return request({
    url: process.env.WEB_API + '/oauth/editUser',
    method: 'post',
    data: params
  })
}

/**
 * 更新用户密码
 * @param params
 */
export function updateUserPwd(params) {
  return request({
    url: process.env.USER_API + '/oauth/updateUserPwd',
    method: 'post',
    data: params
  })
}

/**
 * 更新用户信息
 * @param params
 */
export function updateUserInfo(params) {
  return request({
    url: process.env.USER_API + '/user/updateUserInfo',
    method: 'post',
    data: params
  })
}
export function deleteUserAccessToken(params) {
  return request({
    url: process.env.VUE_APP_BASE_API + '/auth/delete/' + params,
    method: 'get',
  })
}
/**
 * 获取用户登陆日志
 * @param params
 */
export function getLoginLogList(params) {
  return request({
    url: process.env.USER_API + '/user/getLoginLogList',
    method: 'get',
    params
  })
}
/**
 * 本地登录
 * @param params
 */
export function localLogin(params) {
  return request({
    url: process.env.USER_API + '/auth/passwd',
    method: 'post',
    data: params
  })
}

/**
 * 本地注册
 * @param params
 */
export function localRegister(params) {
  return request({
    url: process.env.WEB_API + '/login/register',
    method: 'post',
    data: params
  })
}

export function logout(params) {
  return request({
    url: process.env.WEB_API + '/user/logout',
    method: 'post',
    data: params
  })
}
