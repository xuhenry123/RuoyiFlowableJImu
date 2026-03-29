<template>
  <div class="online-container">
    <el-card>
      <template #header>
        <span>在线用户</span>
      </template>
      <el-table :data="tableData" border>
        <el-table-column prop="sessionId" label="会话ID" width="200" />
        <el-table-column prop="userName" label="用户名称" />
        <el-table-column prop="ipaddr" label="登录IP" />
        <el-table-column prop="loginLocation" label="登录地点" />
        <el-table-column prop="browser" label="浏览器" />
        <el-table-column prop="os" label="操作系统" />
        <el-table-column prop="loginTime" label="登录时间" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleForceLogout(row)">强退</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([
  { sessionId: 'ABC123', userName: 'admin', ipaddr: '127.0.0.1', loginLocation: '本地', browser: 'Chrome', os: 'Windows 10', loginTime: '2026-03-29 10:00:00' }
])

const handleForceLogout = (row) => {
  ElMessageBox.confirm(`确认强退用户 "${row.userName}" 吗？`, '提示', { type: 'warning' })
    .then(() => ElMessage.success('强退成功'))
}
</script>
