import request from '@/utils/request'

export function init() {
  return request({
    url: process.env.GATEWAY_API + '/index/init',
    method: 'get'
  })
}

export function getVisitByWeek() {
  return request({
    url: process.env.GATEWAY_API + '/index/getVisitByWeek',
    method: 'get'
  })
}

export function getBlogCountByTag() {
  return request({
    url: process.env.GATEWAY_API + '/index/getBlogCountByTag',
    method: 'get'
  })
}

export function getBlogCountByBlogCategory() {
  return request({
    url: process.env.GATEWAY_API + '/index/getBlogCountByCategory',
    method: 'get'
  })
}

export function getBlogContributeCount() {
  return request({
    url: process.env.GATEWAY_API + '/index/getBlogContributeCount',
    method: 'get'
  })
}
