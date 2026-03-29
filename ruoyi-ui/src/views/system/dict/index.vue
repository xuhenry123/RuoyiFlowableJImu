<template>
  <div class="dict-container">
    <el-card>
      <template #header>
        <span>字典管理</span>
      </template>
      <el-table :data="tableData" border>
        <el-table-column prop="dictId" label="字典编号" width="100" />
        <el-table-column prop="dictName" label="字典名称" />
        <el-table-column prop="dictType" label="字典类型" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary">修改</el-button>
            <el-button link type="primary">数据</el-button>
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
  { dictId: 1, dictName: '用户性别', dictType: 'sys_user_sex', status: '0' },
  { dictId: 2, dictName: '菜单状态', dictType: 'sys_show_hide', status: '0' },
  { dictId: 3, dictName: '任务状态', dictType: 'sys_task_status', status: '0' }
])
</script>
