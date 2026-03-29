<template>
  <div class="logininfor-container">
    <el-card>
      <template #header>
        <span>登录日志</span>
      </template>
      <el-table :data="tableData" border>
        <el-table-column prop="infoId" label="访问编号" width="100" />
        <el-table-column prop="userName" label="用户名称" />
        <el-table-column prop="ipaddr" label="登录IP" />
        <el-table-column prop="loginLocation" label="登录地点" />
        <el-table-column prop="browser" label="浏览器" />
        <el-table-column prop="os" label="操作系统" />
        <el-table-column prop="status" label="登录状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="loginTime" label="操作时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const tableData = ref([
  { infoId: 1, userName: 'admin', ipaddr: '127.0.0.1', loginLocation: '本地', browser: 'Chrome', os: 'Windows 10', status: '0', loginTime: '2026-03-29 10:00:00' }
])
</script>
