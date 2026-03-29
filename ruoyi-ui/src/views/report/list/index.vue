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
      
      <el-form :inline="true" class="search-form">
        <el-form-item label="报表名称">
          <el-input v-model="queryParams.name" placeholder="请输入报表名称" clearable />
        </el-form-item>
        <el-form-item label="报表类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable style="width: 150px">
            <el-option label="图表" value="chart" />
            <el-option label="表格" value="table" />
            <el-option label="混合" value="mixed" />
          </el-select>
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
      
      <el-row :gutter="20">
        <el-col v-for="item in tableData" :key="item.id" :span="6">
          <el-card class="report-card" shadow="hover">
            <div class="report-preview" @click="handlePreview(item)">
              <div class="preview-icon">
                <el-icon :size="48"><component :is="getIcon(item.type)" /></el-icon>
              </div>
              <el-progress
                v-if="item.status === 'running'"
                :percentage="item.percentage || 0"
                :status="item.percentage > 80 ? 'warning' : 'success'"
              />
            </div>
            <div class="report-info">
              <h4>{{ item.name }}</h4>
              <p class="report-type">
                <el-tag size="small" :type="getTypeTagType(item.type)">{{ getTypeName(item.type) }}</el-tag>
              </p>
              <p class="report-desc">{{ item.description }}</p>
              <p class="report-meta">
                <span><el-icon><User /></el-icon>{{ item.createBy }}</span>
                <span><el-icon><Clock /></el-icon>{{ item.createTime }}</span>
              </p>
            </div>
            <div class="report-actions">
              <el-button link type="primary" @click.stop="handlePreview(item)">预览</el-button>
              <el-button link type="success" @click.stop="handleDesign(item)">编辑</el-button>
              <el-button link type="warning" @click.stop="handleCopy(item)">复制</el-button>
              <el-button link type="danger" @click.stop="handleDelete(item)">删除</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <el-dialog v-model="previewVisible" title="报表预览" width="90%" top="5vh" fullscreen>
      <div class="preview-container">
        <el-result
          v-if="currentReport"
          icon="success"
          :title="currentReport.name"
          :sub-title="currentReport.description"
        >
          <template #extra>
            <div class="preview-chart">
              <div class="chart-placeholder">
                <el-icon :size="80"><DataLine /></el-icon>
                <p>图表展示区域</p>
                <p class="chart-tip">集成 JimuReport 引擎后可显示真实图表</p>
              </div>
            </div>
          </template>
        </el-result>
      </div>
    </el-dialog>

    <el-dialog v-model="designVisible" title="报表设计" width="95%" top="2vh" fullscreen>
      <div class="design-container">
        <el-alert type="info" :closable="false" show-icon>
          <template #title>
            <span>积木报表 JimuReport 在线设计器</span>
          </template>
          <template #default>
            <p>功能：可视化拖拽设计、图表配置、数据源管理、打印导出</p>
            <p>设计器地址：<el-link type="primary" href="http://jimureport.baike888.com" target="_blank">http://jimureport.baike888.com</el-link></p>
          </template>
        </el-alert>
        
        <div class="design-main">
          <el-card class="design-preview">
            <template #header>
              <span>报表预览</span>
            </template>
            <div class="preview-area">
              <el-empty description="选择左侧组件进行报表设计" />
            </div>
          </el-card>
        </div>
        
        <div class="design-toolbar">
          <el-button type="primary" @click="handleSaveDesign">
            <el-icon><FolderChecked /></el-icon>保存
          </el-button>
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>导出
          </el-button>
          <el-button @click="designVisible = false">
            <el-icon><Close /></el-icon>关闭
          </el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="createVisible" title="新建报表" width="600px">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
        <el-form-item label="报表名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入报表名称" />
        </el-form-item>
        <el-form-item label="报表编码" prop="code">
          <el-input v-model="createForm.code" placeholder="请输入报表编码" />
        </el-form-item>
        <el-form-item label="报表类型" prop="type">
          <el-radio-group v-model="createForm.type">
            <el-radio label="chart">图表报表</el-radio>
            <el-radio label="table">表格报表</el-radio>
            <el-radio label="mixed">混合报表</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数据源" prop="datasourceId">
          <el-select v-model="createForm.datasourceId" placeholder="请选择数据源" style="width: 100%">
            <el-option label="默认数据源" value="default" />
            <el-option label="业务数据源" value="business" />
            <el-option label="报表数据源" value="report" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, User, Clock, DataLine, FolderChecked, Download, Close, Chart1, List, Memo } from '@element-plus/icons-vue'

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const previewVisible = ref(false)
const designVisible = ref(false)
const createVisible = ref(false)
const currentReport = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  name: '',
  type: ''
})

const createForm = reactive({
  name: '',
  code: '',
  type: 'chart',
  datasourceId: 'default',
  description: ''
})

const createRules = {
  name: [{ required: true, message: '请输入报表名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入报表编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择报表类型', trigger: 'change' }]
}

const mockData = [
  { id: '1', name: '销售业绩报表', code: 'sales_report', type: 'chart', description: '月度销售业绩统计图表', createBy: 'admin', createTime: '2026-03-20', status: 'running', percentage: 75 },
  { id: '2', name: '用户增长统计', code: 'user_growth', type: 'chart', description: '用户增长趋势分析', createBy: 'admin', createTime: '2026-03-18', status: 'success' },
  { id: '3', name: '订单明细表', code: 'order_detail', type: 'table', description: '订单详细信息列表', createBy: 'admin', createTime: '2026-03-15', status: 'success' },
  { id: '4', name: '财务收支汇总', code: 'finance_summary', type: 'mixed', description: '财务收支统计报表', createBy: 'admin', createTime: '2026-03-10', status: 'success' },
  { id: '5', name: '员工考勤表', code: 'attendance', type: 'table', description: '员工考勤记录明细', createBy: 'admin', createTime: '2026-03-08', status: 'success' },
  { id: '6', name: '库存监控报表', code: 'inventory', type: 'chart', description: '实时库存监控图表', createBy: 'admin', createTime: '2026-03-05', status: 'running', percentage: 45 }
]

const getIcon = (type) => {
  const iconMap = { chart: 'Chart1', table: 'List', mixed: 'Memo' }
  return iconMap[type] || 'Chart1'
}

const getTypeName = (type) => {
  const nameMap = { chart: '图表', table: '表格', mixed: '混合' }
  return nameMap[type] || type
}

const getTypeTagType = (type) => {
  const typeMap = { chart: 'primary', table: 'success', mixed: 'warning' }
  return typeMap[type] || 'info'
}

const getList = () => {
  loading.value = true
  setTimeout(() => {
    let data = [...mockData]
    if (queryParams.name) {
      data = data.filter(item => item.name.includes(queryParams.name))
    }
    if (queryParams.type) {
      data = data.filter(item => item.type === queryParams.type)
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
  queryParams.name = ''
  queryParams.type = ''
  handleQuery()
}

const handleCreate = () => {
  createForm.name = ''
  createForm.code = ''
  createForm.type = 'chart'
  createForm.datasourceId = 'default'
  createForm.description = ''
  createVisible.value = true
}

const confirmCreate = () => {
  ElMessage.success('报表创建成功')
  createVisible.value = false
  getList()
}

const handlePreview = (item) => {
  currentReport.value = item
  previewVisible.value = true
}

const handleDesign = (item) => {
  currentReport.value = item
  designVisible.value = true
}

const handleSaveDesign = () => {
  ElMessage.success('报表保存成功')
}

const handleExport = () => {
  ElMessage.success('报表导出成功')
}

const handleCopy = (item) => {
  ElMessageBox.confirm(`确认复制报表「${item.name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage.success('复制成功')
    getList()
  }).catch(() => {})
}

const handleDelete = (item) => {
  ElMessageBox.confirm(`确认删除报表「${item.name}」吗？删除后不可恢复！`, '警告', {
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
.report-list-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 15px;
}

.report-card {
  margin-bottom: 20px;
  cursor: pointer;
}

.report-preview {
  text-align: center;
  padding: 20px 0;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 15px;
}

.preview-icon {
  color: #409eff;
}

.report-info h4 {
  margin: 0 0 8px;
  font-size: 16px;
}

.report-type {
  margin: 0 0 8px;
}

.report-desc {
  color: #909399;
  font-size: 12px;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.report-meta {
  color: #c0c4cc;
  font-size: 12px;
  display: flex;
  gap: 15px;
  margin: 0;
}

.report-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.report-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.preview-container {
  min-height: 60vh;
}

.preview-chart {
  margin-top: 20px;
}

.chart-placeholder {
  padding: 60px;
  text-align: center;
  background: #f5f7fa;
  border-radius: 8px;
  color: #909399;
}

.chart-tip {
  font-size: 12px;
  margin-top: 10px;
}

.design-container {
  height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
}

.design-main {
  flex: 1;
  margin-top: 20px;
}

.preview-area {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
}

.design-toolbar {
  padding: 15px 0;
  text-align: center;
  border-top: 1px solid #e4e7ed;
  margin-top: 20px;
}
</style>
