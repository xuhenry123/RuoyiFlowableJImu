import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/index',
    children: [
      {
        path: '/index',
        name: 'Index',
        component: () => import('@/views/index/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  },
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: '/system/user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: '/system/role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'Role' }
      },
      {
        path: '/system/menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu' }
      },
      {
        path: '/system/dept',
        name: 'Dept',
        component: () => import('@/views/system/dept/index.vue'),
        meta: { title: '部门管理', icon: 'Office' }
      },
      {
        path: '/system/post',
        name: 'Post',
        component: () => import('@/views/system/post/index.vue'),
        meta: { title: '岗位管理', icon: 'Post' }
      },
      {
        path: '/system/dict',
        name: 'Dict',
        component: () => import('@/views/system/dict/index.vue'),
        meta: { title: '字典管理', icon: 'Dict' }
      },
      {
        path: '/system/config',
        name: 'Config',
        component: () => import('@/views/system/config/index.vue'),
        meta: { title: '参数管理', icon: 'Config' }
      }
    ]
  },
  {
    path: '/workflow',
    component: () => import('@/layout/index.vue'),
    meta: { title: '流程管理', icon: 'Connection' },
    children: [
      {
        path: '/workflow/designer',
        name: 'Designer',
        component: () => import('@/views/workflow/designer/index.vue'),
        meta: { title: '流程设计', icon: 'Edit' }
      },
      {
        path: '/workflow/definition',
        name: 'ProcessDefinition',
        component: () => import('@/views/workflow/definition/index.vue'),
        meta: { title: '流程定义', icon: 'FlowChart' }
      },
      {
        path: '/workflow/instance',
        name: 'ProcessInstance',
        component: () => import('@/views/workflow/instance/index.vue'),
        meta: { title: '流程实例', icon: 'Flow' }
      },
      {
        path: '/workflow/task',
        name: 'Task',
        component: () => import('@/views/workflow/task/index.vue'),
        meta: { title: '我的任务', icon: 'Ticket' }
      }
    ]
  },
  {
    path: '/report',
    component: () => import('@/layout/index.vue'),
    meta: { title: '报表中心', icon: 'DataLine' },
    children: [
      {
        path: '/report/list',
        name: 'ReportList',
        component: () => import('@/views/report/list/index.vue'),
        meta: { title: '报表管理', icon: 'Chart' }
      },
      {
        path: '/report/design',
        name: 'ReportDesign',
        component: () => import('@/views/report/design/index.vue'),
        meta: { title: '报表设计', icon: 'Edit' }
      }
    ]
  },
  {
    path: '/monitor',
    component: () => import('@/layout/index.vue'),
    meta: { title: '系统监控', icon: 'Monitor' },
    children: [
      {
        path: '/monitor/online',
        name: 'Online',
        component: () => import('@/views/monitor/online/index.vue'),
        meta: { title: '在线用户', icon: 'User' }
      },
      {
        path: '/monitor/operlog',
        name: 'Operlog',
        component: () => import('@/views/monitor/operlog/index.vue'),
        meta: { title: '操作日志', icon: 'Operation' }
      },
      {
        path: '/monitor/logininfor',
        name: 'Logininfor',
        component: () => import('@/views/monitor/logininfor/index.vue'),
        meta: { title: '登录日志', icon: 'Login' }
      }
    ]
  },
  {
    path: '/tool',
    component: () => import('@/layout/index.vue'),
    meta: { title: '系统工具', icon: 'Tool' },
    children: [
      {
        path: '/tool/build',
        name: 'Build',
        component: () => import('@/views/tool/build/index.vue'),
        meta: { title: '表单构建', icon: 'Form' }
      },
      {
        path: '/tool/gen',
        name: 'Gen',
        component: () => import('@/views/tool/gen/index.vue'),
        meta: { title: '代码生成', icon: 'Code' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
