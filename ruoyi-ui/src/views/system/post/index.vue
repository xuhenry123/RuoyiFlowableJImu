<template>
  <div class="post-container">
    <el-card>
      <template #header>
        <span>岗位管理</span>
      </template>
      <el-table :data="tableData" border>
        <el-table-column prop="postId" label="岗位编号" width="100" />
        <el-table-column prop="postCode" label="岗位编码" />
        <el-table-column prop="postName" label="岗位名称" />
        <el-table-column prop="sort" label="显示顺序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary">修改</el-button>
            <el-button link type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const tableData = ref([
  { postId: 1, postCode: 'CEO', postName: '首席执行官', sort: 1, status: '0' },
  { postId: 2, postCode: ' CTO', postName: '技术总监', sort: 2, status: '0' },
  { postId: 3, postCode: 'Developer', postName: '开发工程师', sort: 3, status: '0' }
])
</script>
