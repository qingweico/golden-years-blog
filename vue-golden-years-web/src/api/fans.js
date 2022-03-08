import request from '@/utils/request'
/**被动更新粉丝信息**/
export function passive(params) {
    return request({
        url: process.env.USER_API + '/fans/passive',
        method: 'post',
        params,
        data: {}
    })
}
/**查询我的粉丝列表**/
export function queryFansList(params) {
    return request({
        url: process.env.USER_API + '/fans/query',
        method: 'get',
        params,
    })
}
/**根据地区分布查询粉丝性别比例**/
export function queryRatioByRegion(params) {
    return request({
        url: process.env.USER_API + '/fans/queryRatioByRegion',
        method: 'get',
        params,
    })
}
/**查询粉丝性别比例**/
export function queryRatio(params) {
    return request({
        url: process.env.USER_API + '/fans/queryRatio',
        method: 'get',
        params,
    })
}

