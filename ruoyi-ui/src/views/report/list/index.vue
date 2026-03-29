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
          <el-input v-model="queryParams.name" placeholder="请输入报表名称" clearable @keyup.enter="handleQuery" />
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
                <el-icon :size="48"><component :is="getIcon(item.type)" />
                </el-icon>
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
      />
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="报表预览" width="90%" top="5vh" fullscreen>
      <div class="preview-container">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-card shadow="never">
              <template #header>
                <span>报表数据</span>
              </template>
              <div v-if="currentReport">
                <el-row :gutter="20">
                  <el-col :span="24">
                    <div class="chart-wrapper">
                      <div v-if="currentReport.type === 'chart'" class="chart-container">
                        <div id="mainChart" style="width: 100%; height: 400px;"></div>
                      </div>
                      <div v-else class="table-container">
                        <el-table :data="tableData" border stripe>
                          <el-table-column prop="date" label="日期" />
                          <el-table-column prop="value" label="数值" />
                          <el-table-column prop="status" label="状态">
                            <template #default="{ row }">
                              <el-tag :type="row.status === 'success' ? 'success' : 'warning'">{{ row.status }}</el-tag>
                            </template>
                          </el-table-column>
                        </el-table>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <!-- 设计对话框 -->
    <el-dialog v-model="designVisible" title="报表设计器" width="95%" top="2vh" fullscreen>
      <div class="designer-container">
        <el-container style="height: calc(100vh - 180px)">
          <el-aside width="200px">
            <el-card shadow="never">
              <template #header>组件库</template>
              <div class="widget-list">
                <el-collapse v-model="activeNames" accordion>
                  <el-collapse-item title="图表组件" name="1">
                    <div class="widget-item" draggable @dragstart="onWidgetDragStart($event, 'line')">
                      <el-icon><TrendCharts /></el-icon> 折线图
                    </div>
                    <div class="widget-item" draggable @dragstart="onWidgetDragStart($event, 'bar')">
                      <el-icon><Histogram /></el-icon> 柱状图
                    </div>
                    <div class="widget-item" draggable @dragstart="onWidgetDragStart($event, 'pie')">
                      <el-icon><PieChart /></el-icon> 饼图
                    </div>
                    <div class="widget-item" @click="addWidget('gauge')">
                      <el-icon><Odometer /></el-icon> 仪表盘
                    </div>
                  </el-collapse-item>
                  <el-collapse-item title="表格组件" name="2">
                    <div class="widget-item" @click="addWidget('table')">
                      <el-icon><Grid /></el-icon> 数据表格
                    </div>
                    <div class="widget-item" @click="addWidget('pivot')">
                      <el-icon><DataBoard /></el-icon> 透视表
                    </div>
                  </el-collapse-item>
                  <el-collapse-item title="其他组件" name="3">
                    <div class="widget-item" @click="addWidget('text')">
                      <el-icon><Comment /></el-icon> 文本框
                    </div>
                    <div class="widget-item" @click="addWidget('image')">
                      <el-icon><Picture /></el-icon> 图片
                    </div>
                    <div class="widget-item" @click="addWidget('number')">
                      <el-icon><DataAnalysis /></el-icon> 数字翻牌器
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </el-card>
          </el-aside>
          <el-main>
            <el-card shadow="never">
              <template #header>
                <span>报表画布 - {{ designTitle }}</span>
              </template>
              <div class="canvas-area" @drop="onDrop" @dragover.prevent>
                <div v-if="widgets.length === 0" class="empty-canvas">
                  <el-empty description="从左侧拖拽组件到此处开始设计" />
                </div>
                <div v-else class="widget-canvas">
                  <div
                    v-for="(widget, index) in widgets"
                    :key="index"
                    class="canvas-widget"
                    :style="widget.style"
                  >
                    <div class="widget-content">
                      <component :is="getWidgetComponent(widget.type)" :data="widget.data" />
                      <el-button class="widget-delete" link type="danger" @click="removeWidget(index)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </el-card>
          </el-main>
          <el-aside width="280px">
            <el-card shadow="never">
              <template #header>属性配置</template>
              <el-form label-position="top" size="small">
                <el-form-item label="组件ID">
                  <el-input v-model="selectedWidget.id" placeholder="组件ID" />
                </el-form-item>
                <el-form-item label="组件名称">
                  <el-input v-model="selectedWidget.name" placeholder="组件名称" />
                </el-form-item>
                <el-form-item label="数据源">
                  <el-select v-model="selectedWidget.datasourceId" placeholder="请选择数据源">
                    <el-option label="默认数据源" value="default" />
                    <el-option label="业务数据源" value="business" />
                  </el-select>
                </el-form-item>
                <el-form-item v-if="selectedWidget.type === 'line' || selectedWidget.type === 'bar'" label="图表标题">
                  <el-input v-model="selectedWidget.config.title" placeholder="图表标题" />
                </el-form-item>
                <el-form-item v-if="selectedWidget.type === 'line' || selectedWidget.type === 'bar'" label="数据字段">
                  <el-input v-model="selectedWidget.dataField" placeholder="数据字段" />
                </el-form-item>
              </el-form>
            </el-card>
          </el-aside>
        </el-container>
      </div>
      <template #footer>
        <el-button @click="designVisible = false">关闭</el-button>
        <el-button type="primary" @click="saveDesign">保存</el-button>
        <el-button type="success" @click="exportReport">导出</el-button>
      </template>
    </el-dialog>

    <!-- 新建报表对话框 -->
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
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, User, Clock, Delete, TrendCharts, Histogram, PieChart, Odometer, Grid, DataBoard, Comment, Picture, DataAnalysis } from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const previewVisible = ref(false)
const designVisible = ref(false)
const createVisible = ref(false)
const currentReport = ref(null)
const activeNames = ref(['1'])
const widgets = ref([])
const selectedWidget = reactive({
  id: '',
  name: '',
  type: '',
  datasourceId: 'default',
  config: {},
  data: []
})

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
  code: [{ required: true, message: '请输入报表编码', trigger: 'blur' }]
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
  const iconMap = { chart: 'TrendCharts', table: 'Grid', mixed: 'DataBoard' }
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

const getWidgetComponent = (type) => {
  return 'div'
}

const onWidgetDragStart = (event, type) => {
  event.dataTransfer.setData('widgetType', type)
}

const onDrop = (event) => {
  const type = event.dataTransfer.getData('widgetType')
  if (type) {
    addWidget(type)
  }
}

const addWidget = (type) => {
  const widget = {
    id: `widget_${Date.now()}`,
    type: type,
    name: `组件_${widgets.value.length + 1}`,
    datasourceId: 'default',
    config: { title: '' },
    style: {
      width: '300px',
      height: '200px',
      position: 'relative'
    }
  }
  widgets.value.push(widget)
  Object.assign(selectedWidget, widget)
}

const removeWidget = (index) => {
  widgets.value.splice(index, 1)
}

const saveDesign = () => {
  ElMessage.success('报表设计保存成功')
}

const exportReport = () => {
  ElMessage.success('报表导出成功')
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
  if (!createForm.name || !createForm.code) {
    ElMessage.warning('请填写完整信息')
    return
  }
  const newReport = {
    id: Date.now().toString(),
    name: createForm.name,
    code: createForm.code,
    type: createForm.type,
    datasourceId: createForm.datasourceId,
    description: createForm.description,
    createBy: 'admin',
    createTime: new Date().toLocaleDateString(),
    status: 'success'
  }
  tableData.value.push(newReport)
  ElMessage.success('报表创建成功')
  createVisible.value = false
}

const handlePreview = (item) => {
  currentReport.value = item
  previewVisible.value = true
  nextTick(() => {
    if (item.type === 'chart') {
      // 模拟图表渲染
    }
  })
}

const handleDesign = (item) => {
  router.push({
    path: '/report/design',
    query: { id: item.id, name: item.name }
  })
}

const handleCopy = (item) => {
  ElMessageBox.confirm(`确认复制报表「${item.name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage.success('复制成功')
  }).catch(() => {})
}

const handleDelete = (item) => {
  ElMessageBox.confirm(`确认删除报表「${item.name}」吗？删除后不可恢复！`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    const index = tableData.value.findIndex(t => t.id === item.id)
    if (index > -1) {
      tableData.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  }).catch(() => {})
}

onMounted(() => {
  getList()
})
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
  margin-bottom: 10px;
}

.report-info h4 {
  margin: 0 0 8px;
  font-size: 16px;
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
  gap: 8px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.preview-container {
  min-height: 60vh;
}

.chart-wrapper {
  padding: 20px;
}

.widget-list {
  padding: 10px;
}

.widget-item {
  padding: 10px;
  margin-bottom: 8px;
  background: #f5f7fa;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.widget-item:hover {
  background: #ecf5ff;
  color: #409eff;
}

.widget-item[draggable="true"] {
  cursor: move;
}

.canvas-area {
  min-height: 400px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  padding: 20px;
}

.empty-canvas {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}

.widget-canvas {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.canvas-widget {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 10px;
  position: relative;
}

.widget-delete {
  position: absolute;
  top: 5px;
  right: 5px;
}

.designer-container {
  height: 100%;
}

.designer-container .el-container {
  height: calc(100vh - 180px);
}

.el-aside {
  background: #fff;
  padding: 0;
}

.el-aside .el-card {
  height: 100%;
  border: none;
}

.widget-content {
  position: relative;
}
</style>
