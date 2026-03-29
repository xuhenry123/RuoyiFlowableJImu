import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('menu', [
  { id: '1', menuName: '系统管理', parentId: '0', orderNum: 1, path: '/system', component: null, menuType: 'M', icon: 'system', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '100', menuName: '用户管理', parentId: '1', orderNum: 1, path: 'user', component: '/system/user/index', menuType: 'C', icon: 'user', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '101', menuName: '角色管理', parentId: '1', orderNum: 2, path: 'role', component: '/system/role/index', menuType: 'C', icon: 'role', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '102', menuName: '菜单管理', parentId: '1', orderNum: 3, path: 'menu', component: '/system/menu/index', menuType: 'C', icon: 'tree', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '103', menuName: '部门管理', parentId: '1', orderNum: 4, path: 'dept', component: '/system/dept/index', menuType: 'C', icon: 'dept', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '104', menuName: '岗位管理', parentId: '1', orderNum: 5, path: 'post', component: '/system/post/index', menuType: 'C', icon: 'post', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '105', menuName: '字典管理', parentId: '1', orderNum: 6, path: 'dict', component: '/system/dict/index', menuType: 'C', icon: 'dict', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '106', menuName: '参数设置', parentId: '1', orderNum: 7, path: 'config', component: '/system/config/index', menuType: 'C', icon: 'config', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '2', menuName: '流程管理', parentId: '0', orderNum: 2, path: '/workflow', component: null, menuType: 'M', icon: 'workflow', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '200', menuName: '流程设计', parentId: '2', orderNum: 1, path: 'designer', component: '/workflow/designer/index', menuType: 'C', icon: 'designer', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '201', menuName: '流程定义', parentId: '2', orderNum: 2, path: 'definition', component: '/workflow/definition/index', menuType: 'C', icon: 'process', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '202', menuName: '流程实例', parentId: '2', orderNum: 3, path: 'instance', component: '/workflow/instance/index', menuType: 'C', icon: 'instance', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '203', menuName: '任务管理', parentId: '2', orderNum: 4, path: 'task', component: '/workflow/task/index', menuType: 'C', icon: 'task', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '3', menuName: '报表中心', parentId: '0', orderNum: 3, path: '/report', component: null, menuType: 'M', icon: 'report', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '300', menuName: '报表管理', parentId: '3', orderNum: 1, path: 'list', component: '/report/list/index', menuType: 'C', icon: 'list', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '301', menuName: '报表设计', parentId: '3', orderNum: 2, path: 'design', component: '/report/design/index', menuType: 'C', icon: 'design', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '4', menuName: '系统监控', parentId: '0', orderNum: 4, path: '/monitor', component: null, menuType: 'M', icon: 'monitor', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '400', menuName: '在线用户', parentId: '4', orderNum: 1, path: 'online', component: '/monitor/online/index', menuType: 'C', icon: 'online', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '401', menuName: '操作日志', parentId: '4', orderNum: 2, path: 'operlog', component: '/monitor/operlog/index', menuType: 'C', icon: 'log', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '402', menuName: '登录日志', parentId: '4', orderNum: 3, path: 'logininfor', component: '/monitor/logininfor/index', menuType: 'C', icon: 'login', status: '0', createTime: '2026-01-01 10:00:00' }
])

export const menuApi = createLocalApi('menu')

export default menuApi
