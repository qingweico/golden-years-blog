import request from '@/utils/request'

export function getServerInfo(params) {
  return request({
    url: process.env.GATEWAY_API + '/monitor/getServerInfo',
    method: 'get',
    params
  })
}
