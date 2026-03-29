import request from './request'

export const flowableApi = {
  getDefinitionList: (params) => request.get('/flowable/process/definition/list', params),
  getDefinition: (id) => request.get(`/flowable/process/definition/${id}`),
  deployProcess: (data) => request.post('/flowable/process/definition/deploy', data),
  startProcess: (data) => request.post('/flowable/process/instance/start', data),
  getTodoTasks: (params) => request.get('/flowable/process/task/todo/list', params),
  getCompletedTasks: (params) => request.get('/flowable/process/task/completed/list', params),
  completeTask: (taskId, data) => request.post('/flowable/process/task/complete', { taskId, ...data }),
  claimTask: (taskId, userId) => request.post('/flowable/process/task/claim', { taskId, userId })
}

export const reportApi = {
  getList: () => request.get('/report/list'),
  getReport: (id) => request.get(`/report/${id}`),
  getPreviewUrl: (id) => request.get(`/report/preview/${id}`)
}
