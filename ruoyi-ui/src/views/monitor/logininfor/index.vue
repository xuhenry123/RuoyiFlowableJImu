<template>
  <div class="logininfor-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>登录日志</span>
          <el-form :inline="true" :model="queryParams" class="search-form">
            <el-form-item label="用户名称">
              <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="登录状态">
              <el-select v-model="queryParams.status" placeholder="请选择" clearable>
                <el-option label="成功" value="0" />
                <el-option label="失败" value="5" />
              </el-select>
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
        <el-table-column prop="infoId" label="访问编号" width="100" />
        <el-table-column prop="userName" label="用户名称" />
        <el-table-column prop="ipaddr" label="登录IP" />
        <el-table-column prop="loginLocation" label="登录地点" />
        <el-table-column prop="browser" label="浏览器" />
        <el-table-column prop="os" label="操作系统" />
        <el-table-column prop="status" label="登录状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="msg" label="提示消息" show-overflow-tooltip />
        <el-table-column prop="loginTime" label="登录时间" width="160" />
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('login_log', [
  { id: '1', infoId: '1', userName: 'admin', ipaddr: '127.0.0.1', loginLocation: '内网IP', browser: 'Chrome', os: 'Windows 10', status: '0', msg: '登录成功', loginTime: '2026-03-29 10:00:00' },
  { id: '2', infoId: '2', userName: 'user01', ipaddr: '192.168.1.100', loginLocation: '内网IP', browser: 'Firefox', os: 'macOS', status: '0', msg: '登录成功', loginTime: '2026-03-29 09:30:00' },
  { id: '3', infoId: '3', userName: 'test', ipaddr: '10.0.0.50', loginLocation: '内网IP', browser: 'Safari', os: 'iOS', status: '0', msg: '登录成功', loginTime: '2026-03-29 08:45:00' },
  { id: '4', infoId: '4', userName: 'admin', ipaddr: '192.168.1.50', loginLocation: '内网IP', browser: 'Chrome', os: 'Windows 10', status: '5', msg: '密码错误', loginTime: '2026-03-28 16:20:00' },
  { id: '5', infoId: '5', userName: 'guest', ipaddr: '192.168.1.80', loginLocation: '内网IP', browser: 'Edge', os: 'Windows 11', status: '5', msg: '账号不存在', loginTime: '2026-03-28 15:10:00' }
])

const loginLogApi = createLocalApi('login_log')

const loading = ref(false)
const total = ref(0)
const tableData = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  status: ''
})

const getList = async () => {
  loading.value = true
  try {
    const response = await loginLogApi.list(queryParams)
    if (response.code === 200) {
      tableData.value = response.data.list || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('获取登录日志列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.userName = ''
  queryParams.status = ''
  getList()
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
