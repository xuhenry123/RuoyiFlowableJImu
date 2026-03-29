<template>
  <div class="operlog-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <el-form :inline="true" :model="queryParams" class="search-form">
            <el-form-item label="系统模块">
              <el-input v-model="queryParams.title" placeholder="请输入系统模块" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="操作人员">
              <el-input v-model="queryParams.operName" placeholder="请输入操作人员" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="操作状态">
              <el-select v-model="queryParams.status" placeholder="请选择" clearable>
                <el-option label="成功" value="0" />
                <el-option label="失败" value="1" />
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
        <el-table-column prop="id" label="日志编号" width="100" />
        <el-table-column prop="title" label="系统模块" show-overflow-tooltip />
        <el-table-column prop="businessType" label="操作类型" width="120" />
        <el-table-column prop="method" label="请求方法" show-overflow-tooltip />
        <el-table-column prop="operName" label="操作人员" width="120" />
        <el-table-column prop="operIp" label="操作IP" width="140" />
        <el-table-column prop="status" label="操作状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operTime" label="操作时间" width="160" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
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

initLocalData('oper_log', [
  { id: '1', title: '用户管理', businessType: '其它', method: 'UserController.updateUser()', requestMethod: 'PUT', operatorType: '后台用户', operName: 'admin', operIp: '127.0.0.1', operLocation: '内网IP', operTime: '2026-03-29 10:30:00', jsonResult: '{"code":200,"msg":"操作成功"}', status: '0' },
  { id: '2', title: '角色管理', businessType: '新增', method: 'RoleController.add()', requestMethod: 'POST', operatorType: '后台用户', operName: 'admin', operIp: '127.0.0.1', operLocation: '内网IP', operTime: '2026-03-29 10:15:00', jsonResult: '{"code":200,"msg":"操作成功"}', status: '0' },
  { id: '3', title: '菜单管理', businessType: '修改', method: 'MenuController.update()', requestMethod: 'PUT', operatorType: '后台用户', operName: 'admin', operIp: '127.0.0.1', operLocation: '内网IP', operTime: '2026-03-29 10:00:00', jsonResult: '{"code":200,"msg":"操作成功"}', status: '0' },
  { id: '4', title: '流程设计', businessType: '部署', method: 'FlowableProcessController.deploy()', requestMethod: 'POST', operatorType: '后台用户', operName: 'admin', operIp: '127.0.0.1', operLocation: '内网IP', operTime: '2026-03-29 09:45:00', jsonResult: '{"code":200,"msg":"部署成功"}', status: '0' },
  { id: '5', title: '报表管理', businessType: '删除', method: 'ReportController.delete()', requestMethod: 'DELETE', operatorType: '后台用户', operName: 'admin', operIp: '127.0.0.1', operLocation: '内网IP', operTime: '2026-03-29 09:30:00', jsonResult: '{"code":500,"msg":"删除失败"}', status: '1' }
])

const operLogApi = createLocalApi('oper_log')

const loading = ref(false)
const total = ref(0)
const tableData = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  operName: '',
  status: ''
})

const getList = async () => {
  loading.value = true
  try {
    const response = await operLogApi.list(queryParams)
    if (response.code === 200) {
      tableData.value = response.data.list || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('获取操作日志列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.title = ''
  queryParams.operName = ''
  queryParams.status = ''
  getList()
}

const handleDetail = (row) => {
  ElMessage.info(`操作详情：${row.title} - ${row.businessType}`)
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
