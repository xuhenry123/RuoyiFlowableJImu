import request from '@/utils/request'

export function getProcessDefinitionPage(params) {
  return request({
    url: '/flowable/process/definition/list',
    method: 'get',
    params
  })
}

export function getProcessDefinition(id) {
  return request({
    url: `/flowable/process/definition/${id}`,
    method: 'get'
  })
}

export function deployProcess(file, name) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('name', name)
  return request({
    url: '/flowable/process/definition/deploy',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function deployXml(xml, name) {
  return request({
    url: '/flowable/process/definition/deployXml',
    method: 'post',
    data: { xml, name }
  })
}

export function suspendProcess(id) {
  return request({
    url: `/flowable/process/definition/${id}`,
    method: 'put',
    data: { action: 1 }
  })
}

export function activateProcess(id) {
  return request({
    url: `/flowable/process/definition/${id}`,
    method: 'put',
    data: { action: 0 }
  })
}

export function getTodoTaskPage(params) {
  return request({
    url: '/flowable/process/task/todo/list',
    method: 'get',
    params
  })
}

export function getCompletedTaskPage(params) {
  return request({
    url: '/flowable/process/task/completed/list',
    method: 'get',
    params
  })
}

export function startProcess(data) {
  return request({
    url: '/flowable/process/instance/start',
    method: 'post',
    data
  })
}

export function completeTask(data) {
  return request({
    url: '/flowable/process/task/complete',
    method: 'post',
    data
  })
}

export function claimTask(taskId, userId) {
  return request({
    url: '/flowable/process/task/claim',
    method: 'post',
    data: { taskId, userId }
  })
}
