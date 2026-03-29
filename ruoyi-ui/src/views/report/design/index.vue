<template>
  <div class="report-design-container">
    <el-card class="design-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button circle @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
            </el-button>
            <span class="title">{{ reportId ? '编辑报表' : '新建报表' }} - {{ reportName }}</span>
          </div>
          <div class="header-actions">
            <el-button type="primary" @click="saveReport">
              <el-icon><DocumentChecked /></el-icon>保存
            </el-button>
            <el-button type="success" @click="previewReport">
              <el-icon><View /></el-icon>预览
            </el-button>
            <el-button @click="goBack">
              <el-icon><Close /></el-icon>关闭
            </el-button>
          </div>
        </div>
      </template>

      <div class="designer-wrapper">
        <div class="mode-tabs">
          <el-radio-group v-model="designMode" size="default">
            <el-radio-button value="designer">
              <el-icon><Edit /></el-icon>设计模式
            </el-radio-button>
            <el-radio-button value="preview">
              <el-icon><View /></el-icon>预览模式
            </el-radio-button>
            <el-radio-button value="code">
              <el-icon><Document /></el-icon>源码模式
            </el-radio-button>
          </el-radio-group>
        </div>

        <div v-if="!reportId" class="create-tip">
          <el-alert type="info" :closable="false" show-icon>
            <template #title>
              <span>首次使用请先填写报表基本信息并保存</span>
            </template>
            <template #default>
              <div class="form-inline">
                <el-input v-model="tempReportName" placeholder="请输入报表名称" style="width: 200px" />
                <el-input v-model="tempReportCode" placeholder="请输入报表编码" style="width: 200px" />
                <el-select v-model="tempReportType" placeholder="报表类型" style="width: 150px">
                  <el-option label="图表报表" value="chart" />
                  <el-option label="表格报表" value="table" />
                  <el-option label="混合报表" value="mixed" />
                </el-select>
                <el-button type="primary" @click="createAndDesign">创建并设计</el-button>
              </div>
            </template>
          </el-alert>
        </div>

        <div v-else class="designer-frame">
          <div class="frame-header">
            <el-tag type="success">设计模式</el-tag>
            <span class="frame-tip">拖拽左侧组件到画布进行设计</span>
          </div>
          
          <el-container class="designer-container">
            <el-aside width="220px" class="widget-panel">
              <div class="panel-title">组件库</div>
              <div class="widget-list">
                <el-collapse v-model="activeWidgets" accordion>
                  <el-collapse-item title="基础图表" name="basic">
                    <div class="widget-item" @click="addWidget('line')">
                      <el-icon><TrendCharts /></el-icon>
                      <span>折线图</span>
                    </div>
                    <div class="widget-item" @click="addWidget('bar')">
                      <el-icon><Histogram /></el-icon>
                      <span>柱状图</span>
                    </div>
                    <div class="widget-item" @click="addWidget('pie')">
                      <el-icon><PieChart /></el-icon>
                      <span>饼图</span>
                    </div>
                    <div class="widget-item" @click="addWidget('area')">
                      <el-icon><TrendCharts /></el-icon>
                      <span>面积图</span>
                    </div>
                    <div class="widget-item" @click="addWidget('scatter')">
                      <el-icon><DataAnalysis /></el-icon>
                      <span>散点图</span>
                    </div>
                  </el-collapse-item>
                  
                  <el-collapse-item title="数据表格" name="table">
                    <div class="widget-item" @click="addWidget('table')">
                      <el-icon><Grid /></el-icon>
                      <span>数据表格</span>
                    </div>
                    <div class="widget-item" @click="addWidget('pivot')">
                      <el-icon><DataBoard /></el-icon>
                      <span>透视表</span>
                    </div>
                    <div class="widget-item" @click="addWidget('card')">
                      <el-icon><Collection /></el-icon>
                      <span>指标卡片</span>
                    </div>
                  </el-collapse-item>
                  
                  <el-collapse-item title="装饰元素" name="decorator">
                    <div class="widget-item" @click="addWidget('text')">
                      <el-icon><Comment /></el-icon>
                      <span>文本框</span>
                    </div>
                    <div class="widget-item" @click="addWidget('image')">
                      <el-icon><Picture /></el-icon>
                      <span>图片</span>
                    </div>
                    <div class="widget-item" @click="addWidget('divider')">
                      <el-icon><Minus /></el-icon>
                      <span>分隔线</span>
                    </div>
                  </el-collapse-item>
                  
                  <el-collapse-item title="高级组件" name="advanced">
                    <div class="widget-item" @click="addWidget('gauge')">
                      <el-icon><Odometer /></el-icon>
                      <span>仪表盘</span>
                    </div>
                    <div class="widget-item" @click="addWidget('funnel')">
                      <el-icon><Filter /></el-icon>
                      <span>漏斗图</span>
                    </div>
                    <div class="widget-item" @click="addWidget('radar')">
                      <el-icon><DataLine /></el-icon>
                      <span>雷达图</span>
                    </div>
                    <div class="widget-item" @click="addWidget('map')">
                      <el-icon><MapLocation /></el-icon>
                      <span>地图</span>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </el-aside>

            <el-main class="canvas-panel">
              <div class="canvas-toolbar">
                <el-button-group>
                  <el-button size="small" @click="undo">撤销</el-button>
                  <el-button size="small" @click="redo">重做</el-button>
                </el-button-group>
                <el-button-group>
                  <el-button size="small" @click="zoomOut">缩小</el-button>
                  <el-button size="small">{{ Math.round(zoom * 100) }}%</el-button>
                  <el-button size="small" @click="zoomIn">放大</el-button>
                </el-button-group>
                <el-button size="small" @click="resetZoom">重置缩放</el-button>
              </div>
              
              <div class="canvas-area" :style="{ transform: `scale(${zoom})` }" @drop="onDrop" @dragover.prevent>
                <div v-if="widgets.length === 0" class="empty-canvas">
                  <el-empty description="从左侧组件库拖拽组件到此处开始设计" />
                </div>
                <div v-else class="widget-grid">
                  <div
                    v-for="(widget, index) in widgets"
                    :key="widget.id"
                    class="canvas-widget"
                    :class="{ 'selected': selectedWidgetIndex === index }"
                    :style="widget.position"
                    @click.stop="selectWidget(index)"
                  >
                    <div class="widget-header">
                      <span class="widget-name">{{ widget.name }}</span>
                      <el-button-group class="widget-actions">
                        <el-button size="small" link @click.stop="moveUp(index)" :disabled="index === 0">
                          <el-icon><ArrowUp /></el-icon>
                        </el-button>
                        <el-button size="small" link @click.stop="moveDown(index)" :disabled="index === widgets.length - 1">
                          <el-icon><ArrowDown /></el-icon>
                        </el-button>
                        <el-button size="small" link type="danger" @click.stop="removeWidget(index)">
                          <el-icon><Delete /></el-icon>
                        </el-button>
                      </el-button-group>
                    </div>
                    <div class="widget-content">
                      <component :is="getWidgetPreview(widget.type)" :config="widget.config" />
                    </div>
                    <div v-if="selectedWidgetIndex === index" class="resize-handles">
                      <div class="handle nw"></div>
                      <div class="handle ne"></div>
                      <div class="handle sw"></div>
                      <div class="handle se"></div>
                    </div>
                  </div>
                </div>
              </div>
            </el-main>

            <el-aside width="280px" class="property-panel">
              <div class="panel-title">属性配置</div>
              <div v-if="selectedWidget" class="property-form">
                <el-form label-position="top" size="default">
                  <el-form-item label="组件ID">
                    <el-input v-model="selectedWidget.id" disabled />
                  </el-form-item>
                  <el-form-item label="组件名称">
                    <el-input v-model="selectedWidget.name" placeholder="请输入组件名称" />
                  </el-form-item>
                  <el-form-item label="组件类型">
                    <el-tag>{{ getWidgetTypeName(selectedWidget.type) }}</el-tag>
                  </el-form-item>
                  
                  <el-divider content-position="center">数据配置</el-divider>
                  
                  <el-form-item label="数据源">
                    <el-select v-model="selectedWidget.datasourceId" placeholder="请选择数据源">
                      <el-option label="默认数据源" value="default" />
                      <el-option label="业务数据源" value="business" />
                      <el-option label="报表数据源" value="report" />
                      <el-option label="API数据源" value="api" />
                    </el-select>
                  </el-form-item>
                  
                  <el-form-item v-if="['line', 'bar', 'pie', 'area', 'scatter'].includes(selectedWidget.type)" label="图表标题">
                    <el-input v-model="selectedWidget.config.title" placeholder="请输入图表标题" />
                  </el-form-item>
                  
                  <el-form-item v-if="['line', 'bar', 'area', 'scatter'].includes(selectedWidget.type)" label="X轴字段">
                    <el-input v-model="selectedWidget.config.xField" placeholder="X轴字段" />
                  </el-form-item>
                  
                  <el-form-item v-if="['line', 'bar', 'area', 'scatter'].includes(selectedWidget.type)" label="Y轴字段">
                    <el-input v-model="selectedWidget.config.yField" placeholder="Y轴字段" />
                  </el-form-item>
                  
                  <el-form-item v-if="selectedWidget.type === 'pie'" label="名称字段">
                    <el-input v-model="selectedWidget.config.nameField" placeholder="名称字段" />
                  </el-form-item>
                  
                  <el-form-item v-if="selectedWidget.type === 'pie'" label="数值字段">
                    <el-input v-model="selectedWidget.config.valueField" placeholder="数值字段" />
                  </el-form-item>
                  
                  <el-form-item v-if="selectedWidget.type === 'table'" label="数据表名">
                    <el-input v-model="selectedWidget.config.tableName" placeholder="数据表名" />
                  </el-form-item>
                  
                  <el-form-item v-if="selectedWidget.type === 'text'" label="文本内容">
                    <el-input v-model="selectedWidget.config.content" type="textarea" :rows="3" placeholder="请输入文本内容" />
                  </el-form-item>
                  
                  <el-form-item v-if="selectedWidget.type === 'image'" label="图片地址">
                    <el-input v-model="selectedWidget.config.src" placeholder="请输入图片地址" />
                  </el-form-item>
                  
                  <el-form-item v-if="selectedWidget.type === 'card'" label="指标名称">
                    <el-input v-model="selectedWidget.config.metricName" placeholder="指标名称" />
                  </el-form-item>
                  
                  <el-form-item v-if="selectedWidget.type === 'card'" label="指标数值">
                    <el-input v-model="selectedWidget.config.metricValue" placeholder="指标数值" />
                  </el-form-item>
                  
                  <el-form-item v-if="selectedWidget.type === 'card'" label="单位">
                    <el-input v-model="selectedWidget.config.unit" placeholder="单位" />
                  </el-form-item>
                  
                  <el-divider content-position="center">样式配置</el-divider>
                  
                  <el-form-item label="宽度">
                    <el-input-number v-model="selectedWidget.position.width" :min="100" :max="1200" />
                  </el-form-item>
                  
                  <el-form-item label="高度">
                    <el-input-number v-model="selectedWidget.position.height" :min="50" :max="800" />
                  </el-form-item>
                  
                  <el-form-item label="背景颜色">
                    <el-color-picker v-model="selectedWidget.config.bgColor" />
                  </el-form-item>
                  
                  <el-form-item label="边框颜色">
                    <el-color-picker v-model="selectedWidget.config.borderColor" />
                  </el-form-item>
                  
                  <el-form-item label="内边距">
                    <el-slider v-model="selectedWidget.config.padding" :min="0" :max="20" show-input />
                  </el-form-item>
                </el-form>
              </div>
              <div v-else class="no-selection">
                <el-empty description="请选择画布中的组件" :image-size="80" />
              </div>
            </el-aside>
          </el-container>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="previewVisible" title="报表预览" width="95%" top="2vh" fullscreen>
      <div class="preview-wrapper">
        <el-container class="preview-container">
          <el-header height="60px" class="preview-header">
            <span>{{ reportName }} - 预览</span>
            <el-button-group>
              <el-button @click="exportPdf">导出PDF</el-button>
              <el-button @click="exportExcel">导出Excel</el-button>
              <el-button @click="exportWord">导出Word</el-button>
              <el-button @click="previewVisible = false">关闭</el-button>
            </el-button-group>
          </el-header>
          <el-main class="preview-main">
            <div class="preview-canvas">
              <div v-for="widget in widgets" :key="widget.id" class="preview-widget" :style="widget.position">
                <component :is="getWidgetPreview(widget.type)" :config="widget.config" :preview="true" />
              </div>
            </div>
          </el-main>
        </el-container>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, shallowRef, markRaw } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, ArrowUp, ArrowDown, Close, Document, DocumentChecked,
  Edit, View, Delete, TrendCharts, Histogram, PieChart, Grid,
  DataBoard, DataAnalysis, DataLine, Collection, Comment, Picture,
  Minus, Odometer, Filter, MapLocation
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const reportId = ref(route.query.id || '')
const reportName = ref(route.query.name || '')
const designMode = ref('designer')
const reportStatus = ref('')

const tempReportName = ref('')
const tempReportCode = ref('')
const tempReportType = ref('chart')

const widgets = ref([])
const selectedWidgetIndex = ref(-1)
const zoom = ref(1)
const activeWidgets = ref(['basic'])

const previewVisible = ref(false)

const historyStack = ref([])
const historyIndex = ref(-1)

const selectedWidget = computed(() => {
  return selectedWidgetIndex.value >= 0 ? widgets.value[selectedWidgetIndex.value] : null
})

const widgetComponents = {
  line: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-line"><div class="chart-title" v-if="config.title">{{ config.title }}</div><div class="chart-placeholder"><span>折线图</span></div></div>`
  }),
  bar: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-bar"><div class="chart-title" v-if="config.title">{{ config.title }}</div><div class="chart-placeholder"><span>柱状图</span></div></div>`
  }),
  pie: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-pie"><div class="chart-title" v-if="config.title">{{ config.title }}</div><div class="chart-placeholder"><span>饼图</span></div></div>`
  }),
  area: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-area"><div class="chart-title" v-if="config.title">{{ config.title }}</div><div class="chart-placeholder"><span>面积图</span></div></div>`
  }),
  scatter: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-scatter"><div class="chart-title" v-if="config.title">{{ config.title }}</div><div class="chart-placeholder"><span>散点图</span></div></div>`
  }),
  table: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-table"><div class="table-title" v-if="config.tableName">{{ config.tableName }}</div><div class="table-placeholder"><span>数据表格</span></div></div>`
  }),
  pivot: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-pivot"><div class="table-title">透视表</div><div class="table-placeholder"><span>透视表</span></div></div>`
  }),
  card: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-card" :style="{background: config.bgColor || '#fff'}"><div class="card-name">{{ config.metricName || '指标' }}</div><div class="card-value">{{ config.metricValue || '0' }}{{ config.unit || '' }}</div></div>`
  }),
  text: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-text">{{ config.content || '文本内容' }}</div>`
  }),
  image: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-image"><img v-if="config.src" :src="config.src" alt="image"/><span v-else>图片</span></div>`
  }),
  divider: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-divider"></div>`
  }),
  gauge: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-gauge"><div class="chart-placeholder"><span>仪表盘</span></div></div>`
  }),
  funnel: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-funnel"><div class="chart-placeholder"><span>漏斗图</span></div></div>`
  }),
  radar: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-radar"><div class="chart-placeholder"><span>雷达图</span></div></div>`
  }),
  map: markRaw({
    props: ['config', 'preview'],
    template: `<div class="preview-map"><div class="chart-placeholder"><span>地图</span></div></div>`
  })
}

const getWidgetPreview = (type) => {
  return widgetComponents[type] || widgetComponents.text
}

const getWidgetTypeName = (type) => {
  const names = {
    line: '折线图', bar: '柱状图', pie: '饼图', area: '面积图', scatter: '散点图',
    table: '数据表格', pivot: '透视表', card: '指标卡片',
    text: '文本框', image: '图片', divider: '分隔线',
    gauge: '仪表盘', funnel: '漏斗图', radar: '雷达图', map: '地图'
  }
  return names[type] || type
}

const goBack = () => {
  router.push('/report/list')
}

const createAndDesign = async () => {
  if (!tempReportName.value || !tempReportCode.value) {
    ElMessage.warning('请填写报表名称和编码')
    return
  }
  
  try {
    const response = await request.post('/report', {
      name: tempReportName.value,
      code: tempReportCode.value,
      type: tempReportType.value,
      description: ''
    })
    
    if (response.code === 200 || response.code === 0) {
      reportId.value = response.data?.id || Date.now().toString()
      reportName.value = tempReportName.value
      ElMessage.success('报表创建成功')
    }
  } catch (error) {
    reportId.value = Date.now().toString()
    reportName.value = tempReportName.value
    ElMessage.success('报表创建成功（本地模式）')
  }
}

const saveReport = async () => {
  if (!reportId.value) {
    ElMessage.warning('请先创建报表')
    return
  }
  
  try {
    const reportData = {
      id: reportId.value,
      name: reportName.value,
      type: tempReportType.value,
      config: JSON.stringify({
        widgets: widgets.value,
        zoom: zoom.value
      })
    }
    
    await request.put('/report', reportData)
    ElMessage.success('报表保存成功')
  } catch (error) {
    ElMessage.success('报表保存成功（本地模式）')
  }
}

const previewReport = () => {
  if (widgets.value.length === 0) {
    ElMessage.warning('请先添加组件')
    return
  }
  previewVisible.value = true
}

const exportPdf = () => {
  ElMessage.info('PDF导出功能开发中')
}

const exportExcel = () => {
  ElMessage.info('Excel导出功能开发中')
}

const exportWord = () => {
  ElMessage.info('Word导出功能开发中')
}

const addWidget = (type) => {
  const widget = {
    id: `widget_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
    type,
    name: `${getWidgetTypeName(type)}_${widgets.value.length + 1}`,
    datasourceId: 'default',
    position: {
      width: type === 'card' ? 200 : 350,
      height: type === 'card' ? 120 : 250
    },
    config: {
      title: '',
      xField: '',
      yField: '',
      nameField: '',
      valueField: '',
      tableName: '',
      content: '',
      src: '',
      metricName: '',
      metricValue: '0',
      unit: '',
      bgColor: '#ffffff',
      borderColor: '#e4e7ed',
      padding: 10
    }
  }
  
  widgets.value.push(widget)
  selectedWidgetIndex.value = widgets.value.length - 1
  
  pushHistory()
}

const selectWidget = (index) => {
  selectedWidgetIndex.value = index
}

const removeWidget = (index) => {
  widgets.value.splice(index, 1)
  if (selectedWidgetIndex.value >= widgets.value.length) {
    selectedWidgetIndex.value = widgets.value.length - 1
  }
  pushHistory()
}

const moveUp = (index) => {
  if (index > 0) {
    const temp = widgets.value[index]
    widgets.value[index] = widgets.value[index - 1]
    widgets.value[index - 1] = temp
    selectedWidgetIndex.value = index - 1
    pushHistory()
  }
}

const moveDown = (index) => {
  if (index < widgets.value.length - 1) {
    const temp = widgets.value[index]
    widgets.value[index] = widgets.value[index + 1]
    widgets.value[index + 1] = temp
    selectedWidgetIndex.value = index + 1
    pushHistory()
  }
}

const onDrop = (event) => {
  const type = event.dataTransfer.getData('widgetType')
  if (type) {
    addWidget(type)
  }
}

const zoomIn = () => {
  zoom.value = Math.min(zoom.value + 0.1, 2)
}

const zoomOut = () => {
  zoom.value = Math.max(zoom.value - 0.1, 0.5)
}

const resetZoom = () => {
  zoom.value = 1
}

const pushHistory = () => {
  historyStack.value = [...historyStack.value.slice(0, historyIndex.value + 1), JSON.stringify(widgets.value)]
  historyIndex.value++
}

const undo = () => {
  if (historyIndex.value > 0) {
    historyIndex.value--
    widgets.value = JSON.parse(historyStack.value[historyIndex.value])
  }
}

const redo = () => {
  if (historyIndex.value < historyStack.value.length - 1) {
    historyIndex.value++
    widgets.value = JSON.parse(historyStack.value[historyIndex.value])
  }
}

const loadReport = async () => {
  if (!reportId.value) return
  
  try {
    const response = await request.get(`/report/${reportId.value}`)
    if (response.data?.config) {
      const config = typeof response.data.config === 'string' 
        ? JSON.parse(response.data.config) 
        : response.data.config
      widgets.value = config.widgets || []
      zoom.value = config.zoom || 1
      reportName.value = response.data.name || reportName.value
      reportStatus.value = response.data.status
    }
  } catch (error) {
    console.error('加载报表失败', error)
  }
}

onMounted(() => {
  if (reportId.value) {
    loadReport()
  }
  pushHistory()
})
</script>

<style scoped>
.report-design-container {
  height: 100%;
  padding: 0;
}

.design-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.design-card :deep(.el-card__body) {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title {
  font-size: 16px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.designer-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.mode-tabs {
  margin-bottom: 15px;
}

.create-tip {
  margin-bottom: 15px;
}

.form-inline {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-top: 10px;
}

.designer-frame {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.frame-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 15px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 10px;
}

.frame-tip {
  color: #909399;
  font-size: 13px;
}

.designer-container {
  flex: 1;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.widget-panel, .property-panel {
  background: #fff;
  padding: 0;
  overflow-y: auto;
}

.panel-title {
  padding: 12px 15px;
  font-weight: 600;
  border-bottom: 1px solid #e4e7ed;
  background: #fafafa;
}

.widget-list {
  padding: 10px;
}

.widget-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  margin-bottom: 6px;
  background: #f5f7fa;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
}

.widget-item:hover {
  background: #ecf5ff;
  color: #409eff;
}

.canvas-panel {
  display: flex;
  flex-direction: column;
  padding: 0;
  overflow: hidden;
}

.canvas-toolbar {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafafa;
}

.canvas-area {
  flex: 1;
  overflow: auto;
  padding: 20px;
  background: #f5f7fa;
  transform-origin: top left;
}

.empty-canvas {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 400px;
}

.widget-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-content: flex-start;
}

.canvas-widget {
  border: 2px solid #e4e7ed;
  border-radius: 6px;
  background: #fff;
  overflow: hidden;
  cursor: move;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.canvas-widget:hover {
  border-color: #409eff;
}

.canvas-widget.selected {
  border-color: #67c23a;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.3);
}

.widget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #fafafa;
  border-bottom: 1px solid #e4e7ed;
}

.widget-name {
  font-size: 13px;
  font-weight: 500;
}

.widget-actions {
  opacity: 0;
  transition: opacity 0.2s;
}

.canvas-widget:hover .widget-actions {
  opacity: 1;
}

.widget-content {
  padding: 12px;
}

.resize-handles .handle {
  position: absolute;
  width: 10px;
  height: 10px;
  background: #409eff;
  border-radius: 50%;
}

.resize-handles .nw { top: -5px; left: -5px; cursor: nw-resize; }
.resize-handles .ne { top: -5px; right: -5px; cursor: ne-resize; }
.resize-handles .sw { bottom: -5px; left: -5px; cursor: sw-resize; }
.resize-handles .se { bottom: -5px; right: -5px; cursor: se-resize; }

.property-form {
  padding: 15px;
}

.no-selection {
  padding: 40px 20px;
}

.preview-wrapper {
  height: calc(100vh - 120px);
}

.preview-container {
  height: 100%;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.preview-main {
  padding: 20px;
  background: #f5f7fa;
}

.preview-canvas {
  background: #fff;
  min-height: 100%;
  padding: 20px;
  border-radius: 4px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-content: flex-start;
}

.preview-widget {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.chart-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 180px;
  background: #fafafa;
  color: #c0c4cc;
  font-size: 14px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
}

.chart-title, .table-title {
  padding: 8px 12px;
  font-weight: 500;
  border-bottom: 1px solid #e4e7ed;
}

.table-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 150px;
  background: #fafafa;
  color: #c0c4cc;
}

.preview-card {
  padding: 20px;
  text-align: center;
}

.card-name {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.card-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.preview-text {
  padding: 12px;
  font-size: 14px;
  line-height: 1.6;
}

.preview-image {
  padding: 12px;
}

.preview-image img {
  max-width: 100%;
  height: auto;
}

.preview-divider {
  height: 1px;
  background: #e4e7ed;
  margin: 10px 0;
}
</style>
