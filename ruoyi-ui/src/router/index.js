import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: '/workflow',
        name: 'Workflow',
        component: () => import('@/views/Workflow.vue')
      },
      {
        path: '/report',
        name: 'Report',
        component: () => import('@/views/Report.vue')
      },
      {
        path: '/system/user',
        name: 'User',
        component: () => import('@/views/system/User.vue')
      },
      {
        path: '/system/role',
        name: 'Role',
        component: () => import('@/views/system/Role.vue')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
