<template>
  <div class="online-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>在线用户</span>
          <el-form :inline="true" :model="queryParams" class="search-form">
            <el-form-item label="用户名称">
              <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuery">
                <el-icon><Search /></el-icon>搜索
              </el-button>
              <el-button @click="resetQuery">
                <el-icon><Refresh /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>
      
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="tokenId" label="会话ID" width="200" show-overflow-tooltip />
        <el-table-column prop="userName" label="用户名称" />
        <el-table-column prop="ipaddr" label="登录IP" />
        <el-table-column prop="loginLocation" label="登录地点" />
        <el-table-column prop="browser" label="浏览器" />
        <el-table-column prop="os" label="操作系统" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag type="success">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="loginTime" label="登录时间" width="160" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('online_user', [
  { id: '1', tokenId: 'abc123', userName: 'admin', ipaddr: '127.0.0.1', loginLocation: '内网IP', browser: 'Chrome', os: 'Windows 10', status: '在线', loginTime: '2026-03-29 10:00:00' },
  { id: '2', tokenId: 'def456', userName: 'user01', ipaddr: '192.168.1.100', loginLocation: '内网IP', browser: 'Firefox', os: 'macOS', status: '在线', loginTime: '2026-03-29 09:30:00' },
  { id: '3', tokenId: 'ghi789', userName: 'test', ipaddr: '10.0.0.50', loginLocation: '内网IP', browser: 'Safari', os: 'iOS', status: '在线', loginTime: '2026-03-29 08:45:00' }
])

const onlineApi = createLocalApi('online_user')

const loading = ref(false)
const tableData = ref([])

const queryParams = reactive({
  userName: ''
})

const getList = async () => {
  loading.value = true
  try {
    const response = await onlineApi.list(queryParams)
    if (response.code === 200) {
      tableData.value = response.data.list || []
    }
  } catch (error) {
    console.error('获取在线用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryParams.userName = ''
  getList()
}

const handleForceLogout = (row) => {
  ElMessageBox.confirm(`确认强退用户 "${row.userName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await onlineApi.delete(row.id)
      if (response.code === 200) {
        ElMessage.success('强退成功')
        getList()
      }
    } catch (error) {
      ElMessage.error('强退失败')
    }
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 0;
}
</style>
