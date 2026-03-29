import request from '@/utils/request'

export function getList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

export function getInfo(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'get'
  })
}

export function add(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

export function del(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'delete'
  })
}

export function authUserAll(data) {
  return request({
    url: '/system/role/authUserAll',
    method: 'put',
    data
  })
}

export function cancelAuthUser(data) {
  return request({
    url: '/system/role/cancelAuthUser',
    method: 'put',
    data
  })
}
