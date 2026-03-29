import store from '@/store'

const files = import.meta.glob('/src/views/**/*.vue')

export function getMenu() {
  return new Promise((resolve) => {
    resolve([
      {
        path: '/system',
        title: '系统管理',
        icon: 'Setting',
        children: [
          { path: '/system/user', title: '用户管理', icon: 'User' },
          { path: '/system/role', title: '角色管理', icon: 'Role' },
          { path: '/system/menu', title: '菜单管理', icon: 'Menu' },
          { path: '/system/dept', title: '部门管理', icon: 'Office' }
        ]
      },
      {
        path: '/workflow',
        title: '流程管理',
        icon: 'Connection',
        children: [
          { path: '/workflow/definition', title: '流程定义', icon: 'FlowChart' },
          { path: '/workflow/instance', title: '流程实例', icon: 'Flow' },
          { path: '/workflow/task', title: '我的任务', icon: 'Ticket' }
        ]
      },
      {
        path: '/report',
        title: '报表中心',
        icon: 'DataLine',
        children: [
          { path: '/report/list', title: '报表管理', icon: 'Chart' },
          { path: '/report/design', title: '报表设计', icon: 'Edit' }
        ]
      },
      {
        path: '/monitor',
        title: '系统监控',
        icon: 'Monitor',
        children: [
          { path: '/monitor/online', title: '在线用户', icon: 'User' },
          { path: '/monitor/operlog', title: '操作日志', icon: 'Operation' }
        ]
      },
      {
        path: '/tool',
        title: '系统工具',
        icon: 'Tool',
        children: [
          { path: '/tool/build', title: '表单构建', icon: 'Form' },
          { path: '/tool/gen', title: '代码生成', icon: 'Code' }
        ]
      }
    ])
  })
}

export function getFileComponent(path) {
  const key = path.replace('/', '')
  const filePath = `/src/views${path}/index.vue`
  return files[filePath] || files[`/src/views${path}.vue`]
}
