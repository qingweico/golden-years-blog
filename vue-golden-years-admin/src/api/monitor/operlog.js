import request from '@/utils/request'

// 查询操作日志列表
export function list(query) {
  return request({
    url: '/monitor/operation-log/list',
    method: 'get',
    params: query
  })
}

// 删除操作日志
export function delOperationLog(olId) {
  return request({
    url: '/monitor/operation-log/' + olId,
    method: 'delete'
  })
}

// 清空操作日志
export function cleanOperationLog() {
  return request({
    url: '/monitor/operation-log/clean',
    method: 'delete'
  })
}
