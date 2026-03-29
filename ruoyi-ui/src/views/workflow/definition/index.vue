<template>
  <div class="definition-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程定义</span>
          <el-button type="primary" @click="handleDeploy">
            <el-icon><Upload /></el-icon>部署流程
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="流程名称" />
        <el-table-column prop="key" label="流程Key" />
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column prop="category" label="分类" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.suspended ? 'danger' : 'success'">
              {{ row.suspended ? '已挂起' : '已激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deploymentTime" label="部署时间" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleStart(row)">启动</el-button>
            <el-button link type="success" @click="handleSuspend(row)">
              {{ row.suspended ? '激活' : '挂起' }}
            </el-button>
            <el-button link type="warning" @click="handleDiagram(row)">流程图</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="deployVisible" title="部署流程" width="500px">
      <el-form>
        <el-form-item label="流程名称">
          <el-input v-model="deployForm.name" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程分类">
          <el-select v-model="deployForm.category" placeholder="请选择">
            <el-option label="办公类" value="OFFICE" />
            <el-option label="财务类" value="FINANCE" />
            <el-option label="人事类" value="HR" />
          </el-select>
        </el-form-item>
        <el-form-item label="BPMN文件">
          <el-upload ref="uploadRef" :auto-upload="false" :limit="1" accept=".bpmn,.xml">
            <el-button>选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deployVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDeploy">确认部署</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const deployVisible = ref(false)
const deployForm = reactive({ name: '', category: '' })

const getList = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = [
      { id: 'holiday:1', name: '请假流程', key: 'holiday', version: 1, category: '人事类', suspended: false, deploymentTime: '2026-03-20 10:00:00' },
      { id: 'expense:1', name: '报销流程', key: 'expense', version: 1, category: '财务类', suspended: false, deploymentTime: '2026-03-15 14:30:00' },
      { id: 'leave:2', name: '离职流程', key: 'leave', version: 2, category: '人事类', suspended: true, deploymentTime: '2026-03-10 09:00:00' }
    ]
    loading.value = false
  }, 300)
}

const handleDeploy = () => {
  deployVisible.value = true
}

const confirmDeploy = () => {
  ElMessage.success('流程部署成功')
  deployVisible.value = false
  getList()
}

const handleStart = (row) => {
  ElMessage.success(`启动流程: ${row.name}`)
}

const handleSuspend = (row) => {
  row.suspended = !row.suspended
  ElMessage.success(`${row.suspended ? '挂起' : '激活'}成功`)
}

const handleDiagram = (row) => {
  ElMessage.info(`查看流程图: ${row.name}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除流程 "${row.name}" 吗？`, '提示', { type: 'warning' })
    .then(() => { ElMessage.success('删除成功'); getList() })
}

import { ElMessageBox } from 'element-plus'

onMounted(() => { getList() })
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
