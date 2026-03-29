<template>
  <div class="task-container">
    <el-card>
      <template #header>
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="待办任务" name="todo">
            <template #label>
              <el-badge :value="todoCount" :hidden="todoCount === 0" type="primary">
                <span>待办任务</span>
              </el-badge>
            </template>
          </el-tab-pane>
          <el-tab-pane label="已办任务" name="completed" />
          <el-tab-pane label="我发起的" name="initiated" />
        </el-tabs>
      </template>
      
      <el-form :inline="true" class="search-form">
        <el-form-item label="流程名称">
          <el-input v-model="queryParams.processName" placeholder="请输入流程名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="任务名称">
          <el-input v-model="queryParams.taskName" placeholder="请输入任务名称" clearable @keyup.enter="handleQuery" />
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

      <el-table :data="tableData" border v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" v-if="activeTab === 'todo'" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="taskName" label="任务名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="procDefName" label="所属流程" min-width="150" show-overflow-tooltip />
        <el-table-column prop="procInstId" label="流程实例" width="150" show-overflow-tooltip />
        <el-table-column prop="assigneeName" label="办理人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleClaim(row)" v-if="!row.assignee && activeTab === 'todo'">
              <el-icon><User /></el-icon>签收
            </el-button>
            <el-button link type="success" @click="handleComplete(row)" v-if="activeTab === 'todo'">
              <el-icon><Check /></el-icon>办理
            </el-button>
            <el-button link type="info" @click="handleDetail(row)">
              <el-icon><View /></el-icon>详情
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

    <el-dialog v-model="completeDialogVisible" title="办理任务" width="600px">
      <el-form ref="completeFormRef" :model="completeForm" label-width="100px">
        <el-form-item label="任务名称">
          <el-input :value="currentTask?.taskName" disabled />
        </el-form-item>
        <el-form-item label="流程名称">
          <el-input :value="currentTask?.procDefName" disabled />
        </el-form-item>
        <el-form-item label="审批意见">
          <el-select v-model="completeForm.opinion" placeholder="请选择" style="width: 100%">
            <el-option label="同意" value="AGREE" />
            <el-option label="不同意" value="DISAGREE" />
            <el-option label="驳回" value="REJECT" />
            <el-option label="转办" value="TRANSFER" />
          </el-select>
        </el-form-item>
        <el-form-item label="办理备注">
          <el-input v-model="completeForm.comment" type="textarea" :rows="4" placeholder="请输入办理备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmComplete">确认提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="任务详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务名称">{{ currentTask?.taskName }}</el-descriptions-item>
        <el-descriptions-item label="任务Key">{{ currentTask?.taskDefKey }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ currentTask?.procDefName }}</el-descriptions-item>
        <el-descriptions-item label="流程实例ID">{{ currentTask?.procInstId }}</el-descriptions-item>
        <el-descriptions-item label="办理人">{{ currentTask?.assigneeName || '未签收' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTask?.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, User, Check, View } from '@element-plus/icons-vue'
import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('flow_task', [
  { id: '1', taskId: 'task_001', taskName: '部门经理审批', taskDefKey: 'deptManagerAudit', procDefName: '请假流程', procInstId: 'pi_001', procDefKey: 'leaveProcess', businessKey: 'leave_001', createTime: '2026-03-29 09:00:00', assignee: 'manager01', assigneeName: '王经理', delegationState: null, suspensionState: '1' },
  { id: '2', taskId: 'task_002', taskName: '财务审批', taskDefKey: 'financeAudit', procDefName: '报销流程', procInstId: 'pi_002', procDefKey: 'expenseProcess', businessKey: 'expense_001', createTime: '2026-03-28 14:00:00', assignee: '', assigneeName: '', delegationState: null, suspensionState: '1' },
  { id: '3', taskId: 'task_003', taskName: 'HR审批', taskDefKey: 'hrAudit', procDefName: '请假流程', procInstId: 'pi_004', procDefKey: 'leaveProcess', businessKey: 'leave_002', createTime: '2026-03-29 08:00:00', assignee: 'hr01', assigneeName: '赵HR', delegationState: null, suspensionState: '1' }
])

const taskApi = createLocalApi('flow_task')

const activeTab = ref('todo')
const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const todoCount = ref(0)
const selectedRows = ref([])
const completeDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentTask = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  processName: '',
  taskName: ''
})

const completeForm = reactive({
  opinion: 'AGREE',
  comment: '',
  nextNode: ''
})

const getList = async () => {
  loading.value = true
  try {
    const response = await taskApi.list(queryParams)
    if (response.code === 200) {
      tableData.value = response.data.list || []
      total.value = response.data.total || 0
      if (activeTab.value === 'todo') {
        todoCount.value = tableData.value.filter(t => !t.assignee).length
      }
    }
  } catch (error) {
    console.error('获取任务列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  queryParams.pageNum = 1
  getList()
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.processName = ''
  queryParams.taskName = ''
  getList()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleClaim = async (row) => {
  ElMessageBox.confirm(`确认签收任务「${row.taskName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      row.assignee = 'admin'
      row.assigneeName = '管理员'
      ElMessage.success('签收成功（本地模式）')
      getList()
    } catch (error) {
      ElMessage.error('签收失败')
    }
  })
}

const handleComplete = (row) => {
  currentTask.value = row
  completeForm.opinion = 'AGREE'
  completeForm.comment = ''
  completeForm.nextNode = ''
  completeDialogVisible.value = true
}

const confirmComplete = () => {
  ElMessage.success('任务办理成功（本地模式）')
  completeDialogVisible.value = false
  getList()
}

const handleDetail = (row) => {
  currentTask.value = row
  detailDialogVisible.value = true
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.search-form {
  margin-bottom: 15px;
}
</style>
