import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('config', [
  { id: '1', configName: '主框架页-默认皮肤', configKey: 'sys.index.skinName', configValue: 'skin-blue', configType: 'Y', isGlobal: 'Y', status: '0', createTime: '2026-01-01 10:00:00', remark: '默认皮肤：蓝色' },
  { id: '2', configName: '用户管理-初始密码', configKey: 'sys.user.initPassword', configValue: '123456', configType: 'Y', isGlobal: 'Y', status: '0', createTime: '2026-01-01 10:00:00', remark: '初始密码' },
  { id: '3', configName: '主框架页-侧边栏主题', configKey: 'sys.index.sideTheme', configValue: 'theme-dark', configType: 'Y', isGlobal: 'Y', status: '0', createTime: '2026-01-01 10:00:00', remark: '暗色侧边栏' },
  { id: '4', configName: '用户头像', configKey: 'sys.user.avatar', configValue: '', configType: 'N', isGlobal: 'N', status: '0', createTime: '2026-01-01 10:00:00', remark: '用户默认头像' },
  { id: '5', configName: '导出的文件路径', configKey: 'sys.export.path', configValue: '/tmp/export', configType: 'N', isGlobal: 'Y', status: '0', createTime: '2026-01-01 10:00:00', remark: '导出文件路径' }
])

export const configApi = createLocalApi('config')

export default configApi
