<template>
  <div class="task-container">
    <el-card>
      <template #header>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="待办任务" name="todo" />
          <el-tab-pane label="已办任务" name="completed" />
          <el-tab-pane label="我发起的" name="initiated" />
        </el-tabs>
      </template>
      
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="任务名称" />
        <el-table-column prop="processName" label="所属流程" />
        <el-table-column prop="assignee" label="办理人" />
        <el-table-column prop="priority" label="优先级" width="80" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="success" @click="handleClaim(row)" v-if="activeTab === 'todo'">签收</el-button>
            <el-button link type="primary" @click="handleComplete(row)">办理</el-button>
            <el-button link type="warning" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('todo')
const loading = ref(false)
const tableData = ref([])

const getTodoList = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = [
      { id: 'T001', name: '审批请假', processName: '请假流程', assignee: '张三', priority: '高', createTime: '2026-03-29 10:00:00' },
      { id: 'T002', name: '审批报销', processName: '报销流程', assignee: '李四', priority: '中', createTime: '2026-03-29 09:30:00' }
    ]
    loading.value = false
  }, 300)
}

const getCompletedList = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = [
      { id: 'T003', name: '填写请假单', processName: '请假流程', assignee: '王五', priority: '高', createTime: '2026-03-28 10:00:00' }
    ]
    loading.value = false
  }, 300)
}

const handleClaim = (row) => {
  ElMessage.success(`签收任务: ${row.name}`)
  getTodoList()
}

const handleComplete = (row) => {
  ElMessage.success(`完成任务: ${row.name}`)
  getTodoList()
}

const handleDetail = (row) => {
  ElMessage.info(`查看详情: ${row.name}`)
}

onMounted(() => {
  getTodoList()
})
</script>
