import request from '@/utils/request'

export function getList(params) {
  return request({
    url: '/report/list',
    method: 'get',
    params
  })
}

export function getInfo(id) {
  return request({
    url: `/report/${id}`,
    method: 'get'
  })
}

export function add(data) {
  return request({
    url: '/report',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: '/report',
    method: 'put',
    data
  })
}

export function del(id) {
  return request({
    url: `/report/${id}`,
    method: 'delete'
  })
}

export function getPreviewUrl(id) {
  return request({
    url: `/report/preview/${id}`,
    method: 'get'
  })
}
