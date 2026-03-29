import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('oper_log', [
  { id: '1', title: '用户管理', businessType: '其它', method: 'UserController.updateUser()', requestMethod: 'PUT', operatorType: '后台用户', operName: 'admin', operIp: '127.0.0.1', operLocation: '内网IP', operTime: '2026-03-29 10:30:00', jsonResult: '{"code":200,"msg":"操作成功"}', status: '0' },
  { id: '2', title: '角色管理', businessType: '新增', method: 'RoleController.add()', requestMethod: 'POST', operatorType: '后台用户', operName: 'admin', operIp: '127.0.0.1', operLocation: '内网IP', operTime: '2026-03-29 10:15:00', jsonResult: '{"code":200,"msg":"操作成功"}', status: '0' },
  { id: '3', title: '菜单管理', businessType: '修改', method: 'MenuController.update()', requestMethod: 'PUT', operatorType: '后台用户', operName: 'admin', operIp: '127.0.0.1', operLocation: '内网IP', operTime: '2026-03-29 10:00:00', jsonResult: '{"code":200,"msg":"操作成功"}', status: '0' },
  { id: '4', title: '流程设计', businessType: '部署', method: 'FlowableProcessController.deploy()', requestMethod: 'POST', operatorType: '后台用户', operName: 'admin', operIp: '127.0.0.1', operLocation: '内网IP', operTime: '2026-03-29 09:45:00', jsonResult: '{"code":200,"msg":"部署成功"}', status: '0' },
  { id: '5', title: '报表管理', businessType: '删除', method: 'ReportController.delete()', requestMethod: 'DELETE', operatorType: '后台用户', operName: 'admin', operIp: '127.0.0.1', operLocation: '内网IP', operTime: '2026-03-29 09:30:00', jsonResult: '{"code":500,"msg":"删除失败"}', status: '1' }
])

export const operLogApi = createLocalApi('oper_log')

export default operLogApi
