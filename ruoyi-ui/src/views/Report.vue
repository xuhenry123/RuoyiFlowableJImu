<template>
  <div class="report-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报表中心</span>
          <el-button type="primary" @click="handleDesign">
            <el-icon><Edit /></el-icon>
            新建报表
          </el-button>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col v-for="report in reports" :key="report.id" :span="6">
          <el-card class="report-card" shadow="hover" @click="handlePreview(report)">
            <div class="report-icon">
              <el-icon :size="40"><DataLine /></el-icon>
            </div>
            <h4>{{ report.name }}</h4>
            <p class="report-type">{{ report.type }}</p>
            <div class="report-actions">
              <el-button link type="primary" @click.stop="handlePreview(report)">预览</el-button>
              <el-button link type="success" @click.stop="handleEdit(report)">编辑</el-button>
              <el-button link type="danger" @click.stop="handleDelete(report)">删除</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="previewVisible" title="报表预览" width="80%">
      <div class="preview-container">
        <iframe v-if="previewUrl" :src="previewUrl" frameborder="0" class="preview-frame"></iframe>
        <div v-else class="preview-placeholder">
          <p>报表预览区域</p>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="designVisible" title="积木报表设计器" width="90%" fullscreen>
      <div class="design-container">
        <el-alert type="info" :closable="false">
          <p>集成 JimuReport 积木报表设计器</p>
          <p>在线设计器地址: <a href="http://jimureport.baike888.com" target="_blank">http://jimureport.baike888.com</a></p>
        </el-alert>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, DataLine } from '@element-plus/icons-vue'

const reports = ref([])
const previewVisible = ref(false)
const designVisible = ref(false)
const previewUrl = ref('')

const loadReports = () => {
  reports.value = [
    { id: '1', name: '销售报表', type: '图表', code: 'sales' },
    { id: '2', name: '用户统计', type: '图表', code: 'user_stats' },
    { id: '3', name: '订单明细', type: '表格', code: 'order_detail' },
    { id: '4', name: '财务汇总', type: '表格', code: 'finance' }
  ]
}

const handlePreview = (report) => {
  previewVisible.value = true
  previewUrl.value = `/report/preview/${report.id}`
}

const handleEdit = (report) => {
  ElMessage.info(`编辑报表: ${report.name}`)
}

const handleDelete = (report) => {
  ElMessageBox.confirm(`确认删除报表 "${report.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadReports()
  })
}

const handleDesign = () => {
  designVisible.value = true
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-card {
  text-align: center;
  margin-bottom: 20px;
  cursor: pointer;
}

.report-icon {
  color: #409eff;
  margin-bottom: 10px;
}

.report-type {
  color: #909399;
  font-size: 12px;
  margin: 5px 0;
}

.report-actions {
  margin-top: 10px;
}

.preview-container {
  height: 60vh;
}

.preview-frame {
  width: 100%;
  height: 100%;
}

.preview-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.design-container {
  height: calc(100vh - 150px);
}
</style>
