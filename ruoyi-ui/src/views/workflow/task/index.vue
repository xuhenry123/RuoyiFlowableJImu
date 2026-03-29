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
          <el-tab-pane label="全部任务" name="all" />
        </el-tabs>
      </template>
      
      <el-form :inline="true" class="search-form" v-if="activeTab !== 'all'">
        <el-form-item label="流程名称">
          <el-input v-model="queryParams.processName" placeholder="请输入流程名称" clearable />
        </el-form-item>
        <el-form-item label="任务名称">
          <el-input v-model="queryParams.taskName" placeholder="请输入任务名称" clearable />
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
        <el-table-column prop="processName" label="所属流程" min-width="150" show-overflow-tooltip />
        <el-table-column prop="processInstanceId" label="流程实例" width="150" show-overflow-tooltip />
        <el-table-column prop="assigneeName" label="办理人" width="100" />
        <el-table-column prop="priority" label="优先级" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.priority === '100'" type="danger">最高</el-tag>
            <el-tag v-else-if="row.priority === '80'" type="warning">高</el-tag>
            <el-tag v-else-if="row.priority === '50'" type="info">普通</el-tag>
            <el-tag v-else type="success">低</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column prop="dueDate" label="到期时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right" v-if="activeTab === 'todo'">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleClaim(row)" v-if="!row.assignee">
              <el-icon><User /></el-icon>签收
            </el-button>
            <el-button link type="success" @click="handleComplete(row)">
              <el-icon><Check /></el-icon>办理
            </el-button>
            <el-button link type="info" @click="handleDetail(row)">
              <el-icon><View /></el-icon>详情
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" v-else>
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">
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
          <el-input :value="currentTask?.processName" disabled />
        </el-form-item>
        <el-form-item label="审批意见">
          <el-select v-model="completeForm意见" placeholder="请选择" style="width: 100%">
            <el-option label="同意" value="AGREE" />
            <el-option label="不同意" value="DISAGREE" />
            <el-option label="驳回" value="REJECT" />
            <el-option label="转办" value="TRANSFER" />
          </el-select>
        </el-form-item>
        <el-form-item label="办理备注">
          <el-input v-model="completeForm.comment" type="textarea" :rows="4" placeholder="请输入办理备注" />
        </el-form-item>
        <el-form-item label="下一节点">
          <el-select v-model="completeForm.nextNode" placeholder="请选择下一节点（可选）" style="width: 100%" clearable>
            <el-option label="部门经理审批" value="deptManager" />
            <el-option label="总经理审批" value="gm" />
            <el-option label="财务审批" value="finance" />
            <el-option label="HR审批" value="hr" />
          </el-select>
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
        <el-descriptions-item label="任务Key">{{ currentTask?.taskDefinitionKey }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ currentTask?.processName }}</el-descriptions-item>
        <el-descriptions-item label="流程实例ID">{{ currentTask?.processInstanceId }}</el-descriptions-item>
        <el-descriptions-item label="办理人">{{ currentTask?.assigneeName || '未签收' }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ currentTask?.priorityName || '普通' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTask?.createTime }}</el-descriptions-item>
        <el-descriptions-item label="到期时间">{{ currentTask?.dueDate || '无' }}</el-descriptions-item>
        <el-descriptions-item label="任务描述" :span="2">{{ currentTask?.description || '无' }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider>流程变量</el-divider>
      <el-table :data="processVariables" border size="small">
        <el-table-column prop="name" label="变量名" />
        <el-table-column prop="value" label="变量值" />
      </el-table>
      
      <el-divider>审批历史</el-divider>
      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in historyList"
          :key="index"
          :timestamp="item.time"
          :type="item.type"
          :icon="item.icon"
        >
          <p><strong>{{ item.userName }}</strong> {{ item.action }}</p>
          <p v-if="item.comment">{{ item.comment }}</p>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, User, Check, View } from '@element-plus/icons-vue'

const activeTab = ref('todo')
const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const selectedRows = ref([])
const completeDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentTask = ref(null)

const todoCount = ref(3)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  processName: '',
  taskName: ''
})

const completeForm = reactive({
 意见: 'AGREE',
  comment: '',
  nextNode: ''
})

const processVariables = ref([
  { name: 'days', value: '5' },
  { name: 'reason', value: '年假' },
  { name: 'applyUser', value: '张三' }
])

const historyList = ref([
  { time: '2026-03-29 10:00:00', userName: '张三', action: '提交申请', comment: '因私事需要请假5天', type: 'success', icon: 'Check' },
  { time: '2026-03-29 10:05:00', userName: '系统', action: '自动审批', comment: '天数小于3天，自动通过', type: 'primary', icon: 'CircleCheck' },
  { time: '2026-03-29 10:10:00', userName: '李四', action: '审批中', comment: '', type: 'warning', icon: 'Clock' }
])

const mockTodoData = [
  { id: 'T001', taskName: '审批请假', taskDefinitionKey: 'userTask', processName: '请假流程', processInstanceId: 'PI001', assignee: 'admin', assigneeName: '管理员', priority: '80', priorityName: '高', createTime: '2026-03-29 10:00:00', dueDate: '2026-03-30 10:00:00', description: '张三申请请假5天' },
  { id: 'T002', taskName: '审批报销', taskDefinitionKey: 'userTask', processName: '报销流程', processInstanceId: 'PI002', assignee: '', assigneeName: '', priority: '50', priorityName: '普通', createTime: '2026-03-29 09:30:00', dueDate: '', description: '李四申请报销1000元' },
  { id: 'T003', taskName: '合同审核', taskDefinitionKey: 'userTask', processName: '合同审批', processInstanceId: 'PI003', assignee: 'admin', assigneeName: '管理员', priority: '100', priorityName: '最高', createTime: '2026-03-29 08:00:00', dueDate: '2026-03-29 18:00:00', description: '王五的采购合同需要审核' }
]

const mockCompletedData = [
  { id: 'T010', taskName: '填写请假单', taskDefinitionKey: 'startEvent', processName: '请假流程', processInstanceId: 'PI001', assignee: 'zhangsan', assigneeName: '张三', priority: '50', createTime: '2026-03-28 10:00:00', description: '' },
  { id: 'T011', taskName: '部门审批', taskDefinitionKey: 'userTask', processName: '请假流程', processInstanceId: 'PI001', assignee: 'lisi', assigneeName: '李四', priority: '50', createTime: '2026-03-28 10:30:00', description: '' }
]

const handleTabChange = () => {
  queryParams.pageNum = 1
  getList()
}

const getList = () => {
  loading.value = true
  setTimeout(() => {
    if (activeTab.value === 'todo') {
      tableData.value = mockTodoData
      todoCount.value = mockTodoData.length
    } else if (activeTab.value === 'completed') {
      tableData.value = mockCompletedData
    } else if (activeTab.value === 'initiated') {
      tableData.value = mockCompletedData
    } else {
      tableData.value = [...mockTodoData, ...mockCompletedData]
    }
    total.value = tableData.value.length
    loading.value = false
  }, 300)
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.processName = ''
  queryParams.taskName = ''
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleClaim = (row) => {
  ElMessageBox.confirm(`确认签收任务「${row.taskName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    row.assignee = 'admin'
    row.assigneeName = '管理员'
    ElMessage.success('签收成功')
  }).catch(() => {})
}

const handleComplete = (row) => {
  currentTask.value = row
  completeForm.意见 = 'AGREE'
  completeForm.comment = ''
  completeForm.nextNode = ''
  completeDialogVisible.value = true
}

const confirmComplete = () => {
  ElMessage.success('任务办理成功')
  completeDialogVisible.value = false
  getList()
}

const handleDetail = (row) => {
  currentTask.value = row
  detailDialogVisible.value = true
}

getList()
</script>

<style scoped>
.search-form {
  margin-bottom: 15px;
}
</style>
