<template>
  <div class="instance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程实例</span>
          <el-form :inline="true" :model="queryParams" class="search-form">
            <el-form-item label="流程名称">
              <el-input v-model="queryParams.processName" placeholder="请输入流程名称" clearable @keyup.enter="handleQuery" />
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
        <el-table-column type="selection" width="55" />
        <el-table-column prop="instanceId" label="实例ID" width="120" />
        <el-table-column prop="procDefName" label="流程名称" />
        <el-table-column prop="businessKey" label="业务键" />
        <el-table-column prop="startUserName" label="申请人" width="100" />
        <el-table-column prop="suspensionState" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.suspensionState === '1' ? 'success' : 'warning'">
              {{ row.suspensionState === '1' ? '激活' : '挂起' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="danger" @click="handleCancel(row)">取消</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('flow_instance', [
  { id: '1', instanceId: 'pi_001', procDefId: 'leaveProcess:1', procDefName: '请假流程', procDefKey: 'leaveProcess', businessKey: 'leave_001', startTime: '2026-03-29 09:00:00', startUserId: 'admin', startUserName: '管理员', currentActivityId: 'UserTask_1', currentActivityName: '部门经理审批', suspensionState: '1' },
  { id: '2', instanceId: 'pi_002', procDefId: 'expenseProcess:1', procDefName: '报销流程', procDefKey: 'expenseProcess', businessKey: 'expense_001', startTime: '2026-03-28 14:00:00', startUserId: 'user01', startUserName: '张三', currentActivityId: 'UserTask_2', currentActivityName: '财务审批', suspensionState: '1' },
  { id: '3', instanceId: 'pi_003', procDefId: 'purchaseProcess:1', procDefName: '采购流程', procDefKey: 'purchaseProcess', businessKey: 'purchase_001', startTime: '2026-03-27 10:30:00', startUserId: 'admin', startUserName: '管理员', currentActivityId: 'EndEvent_1', currentActivityName: '已结束', suspensionState: '2' }
])

const instanceApi = createLocalApi('flow_instance')

const loading = ref(false)
const total = ref(0)
const tableData = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  processName: ''
})

const getList = async () => {
  loading.value = true
  try {
    const response = await instanceApi.list(queryParams)
    if (response.code === 200) {
      tableData.value = response.data.list || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('获取流程实例列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.processName = ''
  getList()
}

const handleDetail = (row) => {
  ElMessage.info(`查看流程实例详情: ${row.instanceId}`)
}

const handleCancel = (row) => {
  ElMessageBox.confirm(`确认取消流程实例「${row.procDefName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    ElMessage.success('取消成功（本地模式）')
    getList()
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
