import request from '../utils/request'

/** 验证用户token*/
export function authVerify(params) {
    return request({
        url: process.env.USER_API + '/auth/verify?token=' + params,
        method: 'get',
    })
}

/**更新用户密码*/
export function updateUserPwd(params) {
    return request({
        url: process.env.USER_API + '/user/updateUserPwd',
        method: 'post',
        data: params
    })
}

/**更新用户信息*/
export function updateUserInfo(params) {
    return request({
        url: process.env.USER_API + '/user/updateUserInfo',
        method: 'post',
        data: params
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

/**删除用户token*/
export function deleteUserAccessToken(params) {
    return request({
        url: process.env.USER_API + '/auth/delete/' + params,
        method: 'get',
    })
}

/**获取用户登陆日志*/
export function getLoginLogList(params) {
    return request({
        url: process.env.USER_API + '/user/getLoginLogList',
        method: 'get',
        params
    })
}

/**账户密码登录*/
export function localLogin(params) {
    return request({
        url: process.env.USER_API + '/auth/passwd',
        method: 'post',
        data: params
    })
}

/**手机号登陆*/
export function phoneLogin(params) {
    return request({
        url: process.env.USER_API + '/auth/mobile',
        method: 'post',
        data: params
    })
}

/**获取手机验证码*/
export function getSmsCode(params) {
    return request({
        url: process.env.USER_API + '/auth/getSmsCode?mobile=' + params,
        method: 'get',
    })
}
