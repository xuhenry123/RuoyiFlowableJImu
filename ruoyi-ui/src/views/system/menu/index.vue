<template>
  <div class="menu-container">
    <el-card>
      <template #header>
        <span>菜单管理</span>
      </template>
      <el-tree :data="tableData" :props="{ label: 'menuName', children: 'children' }" default-expand-all />
    </el-card>
  </div>
</template>

<script setup>
const tableData = ref([
  {
    menuId: 1,
    menuName: '系统管理',
    children: [
      { menuId: 11, menuName: '用户管理' },
      { menuId: 12, menuName: '角色管理' },
      { menuId: 13, menuName: '菜单管理' },
      { menuId: 14, menuName: '部门管理' }
    ]
  },
  {
    menuId: 2,
    menuName: '流程管理',
    children: [
      { menuId: 21, menuName: '流程定义' },
      { menuId: 22, menuName: '流程实例' },
      { menuId: 23, menuName: '我的任务' }
    ]
  },
  {
    menuId: 3,
    menuName: '报表中心',
    children: [
      { menuId: 31, menuName: '报表管理' },
      { menuId: 32, menuName: '报表设计' }
    ]
  }
])

import { ref } from 'vue'
</script>
