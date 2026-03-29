import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('dict_type', [
  { id: '1', dictName: '用户性别', dictType: 'sys_user_sex', status: '0', createTime: '2026-01-01 10:00:00', remark: '性别编码' },
  { id: '2', dictName: '菜单状态', dictType: 'sys_show_hide', status: '0', createTime: '2026-01-01 10:00:00', remark: '菜单可见性' },
  { id: '3', dictName: '系统状态', dictType: 'sys_normal_disable', status: '0', createTime: '2026-01-01 10:00:00', remark: '系统启用状态' },
  { id: '4', dictName: '任务状态', dictType: 'sys_task_status', status: '0', createTime: '2026-01-01 10:00:00', remark: '任务当前状态' },
  { id: '5', dictName: '流程分类', dictType: 'sys_process_category', status: '0', createTime: '2026-01-01 10:00:00', remark: '流程业务分类' }
])

initLocalData('dict_data', [
  { id: '1', dictSort: 1, dictLabel: '男', dictValue: '1', dictType: 'sys_user_sex', cssClass: '', listClass: 'primary', isDefault: 'Y', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '2', dictSort: 2, dictLabel: '女', dictValue: '2', dictType: 'sys_user_sex', cssClass: '', listClass: 'danger', isDefault: 'N', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '3', dictSort: 3, dictLabel: '未知', dictValue: '0', dictType: 'sys_user_sex', cssClass: '', listClass: 'info', isDefault: 'N', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '4', dictSort: 1, dictLabel: '显示', dictValue: '0', dictType: 'sys_show_hide', cssClass: '', listClass: 'primary', isDefault: 'Y', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '5', dictSort: 2, dictLabel: '隐藏', dictValue: '1', dictType: 'sys_show_hide', cssClass: '', listClass: 'danger', isDefault: 'N', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '6', dictSort: 1, dictLabel: '正常', dictValue: '0', dictType: 'sys_normal_disable', cssClass: '', listClass: 'primary', isDefault: 'Y', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '7', dictSort: 2, dictLabel: '停用', dictValue: '1', dictType: 'sys_normal_disable', cssClass: '', listClass: 'danger', isDefault: 'N', status: '0', createTime: '2026-01-01 10:00:00' }
])

export const dictTypeApi = createLocalApi('dict_type')

export const dictDataApi = createLocalApi('dict_data')

export default { dictTypeApi, dictDataApi }
