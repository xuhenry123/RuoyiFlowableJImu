<template>
  <div class="dept-container">
    <el-card>
      <template #header>
        <span>部门管理</span>
      </template>
      <el-tree :data="tableData" :props="{ label: 'deptName', children: 'children' }" default-expand-all />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const tableData = ref([
  {
    deptId: 1,
    deptName: '总公司',
    children: [
      { deptId: 101, deptName: '研发部' },
      { deptId: 102, deptName: '市场部' },
      { deptId: 103, deptName: '财务部' }
    ]
  }
])
</script>
