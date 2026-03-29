<template>
  <div class="operlog-container">
    <el-card>
      <template #header>
        <span>操作日志</span>
      </template>
      <el-table :data="tableData" border>
        <el-table-column prop="operId" label="日志编号" width="100" />
        <el-table-column prop="title" label="系统模块" />
        <el-table-column prop="businessType" label="操作类型" width="120" />
        <el-table-column prop="operName" label="操作人员" />
        <el-table-column prop="operIp" label="操作IP" width="140" />
        <el-table-column prop="status" label="操作状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.status === 0 ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operTime" label="操作时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const tableData = ref([
  { operId: 1, title: '用户管理', businessType: '新增', operName: 'admin', operIp: '127.0.0.1', status: 0, operTime: '2026-03-29 10:00:00' },
  { operId: 2, title: '角色管理', businessType: '修改', operName: 'admin', operIp: '127.0.0.1', status: 0, operTime: '2026-03-29 11:30:00' }
])
</script>
