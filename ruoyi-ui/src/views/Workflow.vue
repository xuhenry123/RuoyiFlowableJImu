<template>
  <div class="workflow-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程管理</span>
          <el-button type="primary" @click="handleDeploy">
            <el-icon><Upload /></el-icon>
            部署流程
          </el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="流程定义" name="definition">
          <el-table :data="processDefinitions" border style="width: 100%">
            <el-table-column prop="name" label="流程名称" />
            <el-table-column prop="key" label="流程Key" />
            <el-table-column prop="version" label="版本" width="80" />
            <el-table-column prop="category" label="分类" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isSuspended ? 'danger' : 'success'">
                  {{ row.isSuspended ? '已挂起' : '已激活' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleStart(row)">启动</el-button>
                <el-button link type="danger" @click="handleSuspend(row)">
                  {{ row.isSuspended ? '激活' : '挂起' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="待办任务" name="todo">
          <el-table :data="todoTasks" border style="width: 100%">
            <el-table-column prop="name" label="任务名称" />
            <el-table-column prop="processInstanceId" label="流程实例" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column prop="priority" label="优先级" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleClaim(row)">签收</el-button>
                <el-button link type="success" @click="handleComplete(row)">办理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="已办任务" name="completed">
          <el-table :data="completedTasks" border style="width: 100%">
            <el-table-column prop="name" label="任务名称" />
            <el-table-column prop="processInstanceId" label="流程实例" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column prop="assignee" label="办理人" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="deployDialogVisible" title="部署流程" width="500px">
      <el-form>
        <el-form-item label="流程名称">
          <el-input v-model="deployForm.name" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="BPMN文件">
          <el-upload ref="uploadRef" :auto-upload="false" :limit="1">
            <el-button>
              <el-icon><Upload /></el-icon>
              选择文件
            </el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deployDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDeploy">确认部署</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'

const activeTab = ref('definition')
const processDefinitions = ref([])
const todoTasks = ref([])
const completedTasks = ref([])
const deployDialogVisible = ref(false)
const deployForm = ref({ name: '' })

const loadData = () => {
  processDefinitions.value = [
    { id: '1', name: '请假流程', key: 'leave', version: 1, category: '行政', isSuspended: false },
    { id: '2', name: '报销流程', key: 'expense', version: 1, category: '财务', isSuspended: false }
  ]
  todoTasks.value = [
    { id: '1', name: '审批请假', processInstanceId: 'PI001', createTime: '2026-03-29 10:00', priority: '高' }
  ]
  completedTasks.value = [
    { id: '2', name: '填写请假单', processInstanceId: 'PI001', createTime: '2026-03-28 10:00', assignee: '张三' }
  ]
}

const handleDeploy = () => {
  deployDialogVisible.value = true
}

const confirmDeploy = () => {
  ElMessage.success('流程部署成功')
  deployDialogVisible.value = false
}

const handleStart = (row) => {
  ElMessage.success(`启动流程: ${row.name}`)
}

const handleSuspend = (row) => {
  ElMessage.success(`${row.isSuspended ? '激活' : '挂起'}流程: ${row.name}`)
}

const handleClaim = (row) => {
  ElMessage.success(`签收任务: ${row.name}`)
}

const handleComplete = (row) => {
  ElMessage.success(`完成任务: ${row.name}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
