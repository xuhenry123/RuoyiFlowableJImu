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
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.suspended ? 'danger' : 'success'">
              {{ row.suspended ? '已挂起' : '已激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deploymentTime" label="部署时间" width="160" />
        <el-table-column label="操作" width="320" fixed="right">
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

    <el-dialog v-model="deployDialogVisible" title="部署流程" width="500px">
      <el-form ref="deployFormRef" :model="deployForm" :rules="deployRules" label-width="100px">
        <el-form-item label="流程名称" prop="name">
          <el-input v-model="deployForm.name" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程分类" prop="category">
          <el-select v-model="deployForm.category" placeholder="请选择">
            <el-option label="办公类" value="OFFICE" />
            <el-option label="财务类" value="FINANCE" />
            <el-option label="人事类" value="HR" />
            <el-option label="审批类" value="APPROVAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="BPMN文件" prop="file">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".bpmn,.xml,.zip"
            :file-list="fileList"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
          >
            <el-button>
              <el-icon><Upload /></el-icon>选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">支持 .bpmn、.xml、.zip 格式文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deployDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDeploy">确认部署</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="diagramDialogVisible" title="流程图" width="80%" top="5vh">
      <div class="diagram-container">
        <iframe v-if="diagramUrl" :src="diagramUrl" frameborder="0" class="diagram-iframe" />
        <el-empty v-else description="暂无流程图" />
      </div>
    </el-dialog>

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
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Upload, Delete, VideoPlay, Switch, View } from '@element-plus/icons-vue'

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const selectedRows = ref([])
const deployDialogVisible = ref(false)
const diagramDialogVisible = ref(false)
const startDialogVisible = ref(false)
const diagramUrl = ref('')
const currentProcess = ref(null)
const fileList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  category: ''
})

const deployForm = reactive({
  name: '',
  category: 'OFFICE',
  file: null
})

const deployRules = {
  name: [{ required: true, message: '请输入流程名称', trigger: 'blur' }],
  file: [{ required: true, message: '请选择BPMN文件', trigger: 'change' }]
}

const startForm = reactive({
  businessKey: '',
  assignee: '',
  comment: ''
})

const mockData = [
  { id: 'holiday:1', name: '请假流程', key: 'holiday', version: 1, category: 'HR', description: '员工请假申请流程', suspended: false, deploymentTime: '2026-03-20 10:00:00' },
  { id: 'expense:1', name: '报销流程', key: 'expense', version: 1, category: 'FINANCE', description: '日常费用报销流程', suspended: false, deploymentTime: '2026-03-18 14:30:00' },
  { id: 'leave:2', name: '离职流程', key: 'leave', version: 2, category: 'HR', description: '员工离职办理流程', suspended: true, deploymentTime: '2026-03-15 09:00:00' },
  { id: 'purchase:1', name: '采购流程', key: 'purchase', version: 1, category: 'OFFICE', description: '办公用品采购流程', suspended: false, deploymentTime: '2026-03-10 16:00:00' },
  { id: 'contract:1', name: '合同审批', key: 'contract', version: 1, category: 'APPROVAL', description: '合同审批流程', suspended: false, deploymentTime: '2026-03-05 11:00:00' }
]

const getList = () => {
  loading.value = true
  setTimeout(() => {
    let data = [...mockData]
    if (queryParams.keyword) {
      data = data.filter(item => 
        item.name.includes(queryParams.keyword) || item.key.includes(queryParams.keyword)
      )
    }
    if (queryParams.category) {
      data = data.filter(item => item.category === queryParams.category)
    }
    tableData.value = data
    total.value = data.length
    loading.value = false
  }, 300)
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.category = ''
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleFileChange = (file) => {
  deployForm.file = file.raw
}

const handleFileRemove = () => {
  deployForm.file = null
}

const handleDeploy = () => {
  deployForm.name = ''
  deployForm.category = 'OFFICE'
  deployForm.file = null
  fileList.value = []
  deployDialogVisible.value = true
}

const confirmDeploy = () => {
  if (!deployForm.file) {
    ElMessage.warning('请选择BPMN文件')
    return
  }
  ElMessage.success('流程部署成功')
  deployDialogVisible.value = false
  getList()
}

const handleStart = (row) => {
  currentProcess.value = row
  startForm.businessKey = ''
  startForm.assignee = ''
  startForm.comment = ''
  startDialogVisible.value = true
}

const confirmStart = () => {
  ElMessage.success(`流程「${currentProcess.value.name}」启动成功`)
  startDialogVisible.value = false
}

const handleSuspend = (row) => {
  const action = row.suspended ? '激活' : '挂起'
  ElMessageBox.confirm(`确认${action}流程「${row.name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    row.suspended = !row.suspended
    ElMessage.success(`${action}成功`)
  }).catch(() => {})
}

const handleDiagram = (row) => {
  diagramUrl.value = `/flowable/process/definition/diagram/${row.id}`
  diagramDialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除流程「${row.name}」吗？删除后不可恢复！`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

getList()
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.diagram-container {
  height: 70vh;
}

.diagram-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
