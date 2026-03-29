<template>
  <div class="designer-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程设计器</span>
          <el-button-group>
            <el-button type="primary" @click="handleSave">
              <el-icon><FolderChecked /></el-icon>保存
            </el-button>
            <el-button type="success" @click="handleDeploy">
              <el-icon><Upload /></el-icon>部署
            </el-button>
            <el-button @click="handleDownload">
              <el-icon><Download /></el-icon>下载
            </el-button>
          </el-button-group>
        </div>
      </template>
      
      <div class="designer-wrapper">
        <div class="palette">
          <div class="palette-title">流程元素</div>
          <div class="palette-items">
            <div class="palette-item" draggable="true" @dragstart="onDragStart('startEvent')">
              <el-icon><VideoPlay /></el-icon>
              <span>开始事件</span>
            </div>
            <div class="palette-item" draggable="true" @dragstart="onDragStart('endEvent')">
              <el-icon><CircleClose /></el-icon>
              <span>结束事件</span>
            </div>
            <div class="palette-item" draggable="true" @dragstart="onDragStart('userTask')">
              <el-icon><User /></el-icon>
              <span>用户任务</span>
            </div>
            <div class="palette-item" draggable="true" @dragstart="onDragStart('serviceTask')">
              <el-icon><Cpu /></el-icon>
              <span>服务任务</span>
            </div>
            <div class="palette-item" draggable="true" @dragstart="onDragStart('exclusiveGateway')">
              <el-icon><SetUp /></el-icon>
              <span>排他网关</span>
            </div>
            <div class="palette-item" draggable="true" @dragstart="onDragStart('parallelGateway')">
              <el-icon><Connection /></el-icon>
              <span>并行网关</span>
            </div>
          </div>
        </div>
        
        <div class="canvas" ref="canvasRef">
          <div id="bpmn-canvas" ref="bpmnCanvasRef"></div>
        </div>
        
        <div class="properties-panel">
          <div class="panel-title">属性配置</div>
          <el-form label-width="80px" size="small">
            <el-form-item label="流程名称">
              <el-input v-model="processName" placeholder="请输入流程名称" />
            </el-form-item>
            <el-form-item label="流程Key">
              <el-input v-model="processKey" placeholder="请输入流程标识" />
            </el-form-item>
            <el-form-item label="流程分类">
              <el-select v-model="processCategory" placeholder="请选择">
                <el-option label="办公类" value="OFFICE" />
                <el-option label="财务类" value="FINANCE" />
                <el-option label="人事类" value="HR" />
              </el-select>
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="description" type="textarea" :rows="3" placeholder="请输入描述" />
            </el-form-item>
          </el-form>
          
          <el-divider>元素属性</el-divider>
          <el-form label-width="80px" size="small" v-if="selectedElement">
            <el-form-item label="元素ID">
              <el-input v-model="selectedElement.id" disabled />
            </el-form-item>
            <el-form-item label="元素名称">
              <el-input v-model="selectedElement.name" @change="updateElementName" />
            </el-form-item>
            <el-form-item label="元素类型">
              <el-input :value="selectedElement.type" disabled />
            </el-form-item>
          </el-form>
          <el-empty v-else description="请选择元素" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { FolderChecked, Upload, Download, VideoPlay, CircleClose, User, Cpu, Setting, Connection } from '@element-plus/icons-vue'
import BpmnModeler from 'bpmn-js/lib/Modeler'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css'

const canvasRef = ref(null)
const bpmnCanvasRef = ref(null)
const processName = ref('请假流程')
const processKey = ref('leaveProcess')
const processCategory = ref('HR')
const description = ref('')
const selectedElement = ref(null)

let bpmnModeler = null

const initBpmn = () => {
  const xml = `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                   xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                   xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                   xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
                   id="Definitions_1"
                   targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="Process_1" isExecutable="true">
    <bpmn:startEvent id="StartEvent_1" name="开始" />
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1" />
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`

  bpmnModeler = new BpmnModeler({
    container: bpmnCanvasRef.value,
    keyboard: { bindTo: document }
  })

  bpmnModeler.importXML(xml, (err) => {
    if (err) {
      console.error('Failed to import BPMN:', err)
      return
    }
    const canvas = bpmnModeler.get('canvas')
    canvas.zoom('fit-viewport')
  })

  bpmnModeler.on('selection.changed', (event) => {
    const element = event.selectedElements[0]
    if (element) {
      selectedElement.value = {
        id: element.id,
        name: element.businessObject?.name || '',
        type: element.$type
      }
    } else {
      selectedElement.value = null
    }
  })
}

const onDragStart = (type) => {
  console.log('Drag start:', type)
}

const updateElementName = () => {
  if (!selectedElement.value || !bpmnModeler) return
  const modeling = bpmnModeler.get('modeling')
  const elementRegistry = bpmnModeler.get('elementRegistry')
  const element = elementRegistry.get(selectedElement.value.id)
  if (element) {
    modeling.updateProperties(element, { name: selectedElement.value.name })
  }
}

const handleSave = () => {
  bpmnModeler.saveXML({ format: true }, (err, xml) => {
    if (err) {
      ElMessage.error('保存失败')
      return
    }
    console.log('XML:', xml)
    ElMessage.success('流程已保存')
  })
}

const handleDeploy = () => {
  bpmnModeler.saveXML({ format: true }, (err, xml) => {
    if (err) {
      ElMessage.error('部署失败')
      return
    }
    ElMessage.success('流程部署成功')
  })
}

const handleDownload = () => {
  bpmnModeler.saveXML({ format: true }, (err, xml) => {
    if (err) {
      ElMessage.error('下载失败')
      return
    }
    const blob = new Blob([xml], { type: 'application/xml' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `${processKey.value}.bpmn`
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  })
}

onMounted(() => {
  initBpmn()
})

onUnmounted(() => {
  if (bpmnModeler) {
    bpmnModeler.destroy()
  }
})
</script>

<style scoped>
.designer-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.designer-wrapper {
  display: flex;
  height: calc(100vh - 250px);
  min-height: 500px;
}

.palette {
  width: 180px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  padding: 15px;
}

.palette-title {
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}

.palette-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.palette-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  cursor: move;
  font-size: 13px;
  color: #606266;
  transition: all 0.3s;
}

.palette-item:hover {
  background: #ecf5ff;
  color: #409eff;
}

.canvas {
  flex: 1;
  background: #f5f7fa;
  overflow: hidden;
}

#bpmn-canvas {
  width: 100%;
  height: 100%;
}

.properties-panel {
  width: 300px;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  padding: 15px;
  overflow-y: auto;
}

.panel-title {
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}
</style>
