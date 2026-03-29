<template>
  <div class="instance-container">
    <el-card>
      <template #header>
        <span>流程实例</span>
      </template>
      <el-table :data="tableData" border>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="instanceId" label="实例ID" width="120" />
        <el-table-column prop="processName" label="流程名称" />
        <el-table-column prop="businessKey" label="业务键" />
        <el-table-column prop="applicant" label="申请人" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'RUNNING' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary">详情</el-button>
            <el-button link type="danger">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const tableData = ref([
  { instanceId: 'PI001', processName: '请假流程', businessKey: 'HOLIDAY_001', applicant: '张三', status: 'RUNNING', startTime: '2026-03-28 10:00:00' },
  { instanceId: 'PI002', processName: '报销流程', businessKey: 'EXPENSE_001', applicant: '李四', status: 'COMPLETED', startTime: '2026-03-25 09:00:00' },
  { instanceId: 'PI003', processName: '请假流程', businessKey: 'HOLIDAY_002', applicant: '王五', status: 'RUNNING', startTime: '2026-03-29 08:30:00' }
])
</script>
