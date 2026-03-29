<template>
  <div class="report-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报表管理</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>新建报表
          </el-button>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col v-for="item in tableData" :key="item.id" :span="6">
          <el-card class="report-card" shadow="hover">
            <div class="report-icon">
              <el-icon :size="40"><Chart /></el-icon>
            </div>
            <h4>{{ item.name }}</h4>
            <p class="report-type">{{ item.type }}</p>
            <p class="report-desc">{{ item.description }}</p>
            <div class="report-actions">
              <el-button link type="primary" @click="handlePreview(item)">预览</el-button>
              <el-button link type="success" @click="handleEdit(item)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(item)">删除</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Chart } from '@element-plus/icons-vue'

const tableData = ref([])

const getList = () => {
  tableData.value = [
    { id: '1', name: '销售报表', type: '图表', description: '月度销售数据统计' },
    { id: '2', name: '用户统计', type: '图表', description: '用户增长趋势' },
    { id: '3', name: '订单明细', type: '表格', description: '订单详细信息列表' },
    { id: '4', name: '财务汇总', type: '表格', description: '财务收支汇总' },
    { id: '5', name: '流程统计', type: '图表', description: '流程审批统计' },
    { id: '6', name: '业绩排行', type: '图表', description: '员工业绩排名' }
  ]
}

const handleCreate = () => ElMessage.info('新建报表')
const handlePreview = (item) => ElMessage.info(`预览报表: ${item.name}`)
const handleEdit = (item) => ElMessage.info(`编辑报表: ${item.name}`)
const handleDelete = (item) => {
  ElMessageBox.confirm(`确认删除报表 "${item.name}" 吗？`, '提示', { type: 'warning' })
    .then(() => { ElMessage.success('删除成功'); getList() })
}

onMounted(() => { getList() })
</script>

<style scoped>
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

.report-desc {
  color: #666;
  font-size: 12px;
  margin-bottom: 10px;
}

.report-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>
