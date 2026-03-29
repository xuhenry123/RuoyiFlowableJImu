<template>
  <div class="definition-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程定义</span>
          <el-space>
            <el-input v-model="queryParams.keyword" placeholder="流程名称/Key" style="width: 200px" clearable @keyup.enter="handleQuery" />
            <el-select v-model="queryParams.category" placeholder="流程分类" style="width: 150px" clearable>
              <el-option label="办公类" value="OFFICE" />
              <el-option label="财务类" value="FINANCE" />
              <el-option label="人事类" value="HR" />
              <el-option label="审批类" value="APPROVAL" />
            </el-select>
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon>搜索
            </el-button>
            <el-button @click="resetQuery">
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-space>
        </div>
      </template>
      
      <el-table :data="tableData" border v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="name" label="流程名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="key" label="流程Key" width="150" />
        <el-table-column prop="version" label="版本" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="info">v{{ row.version }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.category === 'OFFICE'" type="success">办公类</el-tag>
            <el-tag v-else-if="row.category === 'FINANCE'" type="warning">财务类</el-tag>
            <el-tag v-else-if="row.category === 'HR'" type="danger">人事类</el-tag>
            <el-tag v-else type="info">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.suspended ? 'danger' : 'success'">
              {{ row.suspended ? '已挂起' : '已激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deploymentTime" label="部署时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleStart(row)">
              <el-icon><VideoPlay /></el-icon>启动
            </el-button>
            <el-button link :type="row.suspended ? 'success' : 'warning'" @click="handleSuspend(row)">
              <el-icon><Switch /></el-icon>{{ row.suspended ? '激活' : '挂起' }}
            </el-button>
            <el-button link type="primary" @click="handleDiagram(row)">
              <el-icon><View /></el-icon>流程图
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
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

    <el-dialog v-model="startDialogVisible" title="启动流程" width="500px">
      <el-form ref="startFormRef" :model="startForm" label-width="100px">
        <el-form-item label="流程名称">
          <el-input :value="currentProcess?.name" disabled />
        </el-form-item>
        <el-form-item label="业务键">
          <el-input v-model="startForm.businessKey" placeholder="请输入业务键（可选）" />
        </el-form-item>
        <el-form-item label="办理人">
          <el-select v-model="startForm.assignee" placeholder="请选择办理人" style="width: 100%">
            <el-option label="张三" value="zhangsan" />
            <el-option label="李四" value="lisi" />
            <el-option label="王五" value="wangwu" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="startForm.comment" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="startDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStart">确认启动</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="diagramDialogVisible" title="流程图" width="80%" top="5vh">
      <div class="diagram-container">
        <el-empty description="流程图功能需要后端支持" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, VideoPlay, Switch, View } from '@element-plus/icons-vue'
import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('flow_definition', [
  { id: '1', definitionId: 'leaveProcess:1', name: '请假流程', key: 'leaveProcess', category: 'HR', version: 1, deploymentId: 'd1', suspended: false, tenantId: '', deploymentTime: '2026-03-20 10:00:00' },
  { id: '2', definitionId: 'expenseProcess:1', name: '报销流程', key: 'expenseProcess', category: 'FINANCE', version: 1, deploymentId: 'd2', suspended: false, tenantId: '', deploymentTime: '2026-03-18 14:30:00' },
  { id: '3', definitionId: 'purchaseProcess:1', name: '采购流程', key: 'purchaseProcess', category: 'OFFICE', version: 1, deploymentId: 'd3', suspended: false, tenantId: '', deploymentTime: '2026-03-15 09:00:00' },
  { id: '4', definitionId: 'leaveProcess:2', name: '请假流程', key: 'leaveProcess', category: 'HR', version: 2, deploymentId: 'd4', suspended: false, tenantId: '', deploymentTime: '2026-03-25 11:00:00' }
])

const definitionApi = createLocalApi('flow_definition')

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const selectedRows = ref([])
const diagramDialogVisible = ref(false)
const startDialogVisible = ref(false)
const currentProcess = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  category: ''
})

const startForm = reactive({
  businessKey: '',
  assignee: '',
  comment: ''
})

const getList = async () => {
  loading.value = true
  try {
    const response = await definitionApi.list(queryParams)
    if (response.code === 200) {
      tableData.value = response.data.list || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('获取流程定义列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.category = ''
  getList()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleStart = (row) => {
  currentProcess.value = row
  startForm.businessKey = ''
  startForm.assignee = ''
  startForm.comment = ''
  startDialogVisible.value = true
}

const confirmStart = async () => {
  ElMessage.success(`流程「${currentProcess.value.name}」启动成功（本地模式）`)
  startDialogVisible.value = false
}

const handleSuspend = async (row) => {
  const action = row.suspended ? '激活' : '挂起'
  ElMessageBox.confirm(`确认${action}流程「${row.name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      row.suspended = !row.suspended
      ElMessage.success(`${action}成功`)
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  })
}

const handleDiagram = (row) => {
  diagramDialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除流程「${row.name}」吗？删除后不可恢复！`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(async () => {
    try {
      const response = await definitionApi.delete(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      ElMessage.error('删除失败')
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

.diagram-container {
  height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
