<template>
  <div class="designer-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程设计器</span>
          <el-button-group>
            <el-button type="primary" @click="handleNew">
              <el-icon><DocumentAdd /></el-icon>新建
            </el-button>
            <el-button type="primary" @click="handleSave">
              <el-icon><FolderChecked /></el-icon>保存
            </el-button>
            <el-button type="success" @click="handleDeploy">
              <el-icon><Upload /></el-icon>部署
            </el-button>
            <el-button @click="handleDownload">
              <el-icon><Download /></el-icon>下载
            </el-button>
            <el-button @click="handleImport">
              <el-icon><Upload /></el-icon>导入
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
              <el-icon><Connection /></el-icon>
              <span>排他网关</span>
            </div>
            <div class="palette-item" draggable="true" @dragstart="onDragStart('parallelGateway')">
              <el-icon><Connection /></el-icon>
              <span>并行网关</span>
            </div>
            <div class="palette-item" draggable="true" @dragstart="onDragStart('inclusiveGateway')">
              <el-icon><Connection /></el-icon>
              <span>包容网关</span>
            </div>
            <div class="palette-item" draggable="true" @dragstart="onDragStart('scriptTask')">
              <el-icon><Document /></el-icon>
              <span>脚本任务</span>
            </div>
            <div class="palette-item" draggable="true" @dragstart="onDragStart('manualTask')">
              <el-icon><DocumentCopy /></el-icon>
              <span>手动任务</span>
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
              <el-input v-model="processName" placeholder="请输入流程名称" @change="updateProcessInfo" />
            </el-form-item>
            <el-form-item label="流程Key">
              <el-input v-model="processKey" placeholder="请输入流程标识" @change="updateProcessInfo" />
            </el-form-item>
            <el-form-item label="流程分类">
              <el-select v-model="processCategory" placeholder="请选择" @change="updateProcessInfo">
                <el-option label="办公类" value="OFFICE" />
                <el-option label="财务类" value="FINANCE" />
                <el-option label="人事类" value="HR" />
                <el-option label="审批类" value="APPROVAL" />
              </el-select>
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="description" type="textarea" :rows="2" placeholder="请输入描述" @change="updateProcessInfo" />
            </el-form-item>
          </el-form>
          
          <el-divider content-position="left">元素属性</el-divider>
          <el-form label-width="80px" size="small" v-if="selectedElement">
            <el-form-item label="元素ID">
              <el-input v-model="selectedElement.id" disabled />
            </el-form-item>
            <el-form-item label="元素名称">
              <el-input v-model="selectedElement.name" @change="updateElementName" />
            </el-form-item>
            <el-form-item label="元素类型">
              <el-input :value="getElementTypeName(selectedElement.type)" disabled />
            </el-form-item>
            <el-form-item label="办理人" v-if="selectedElement.type === 'bpmn:UserTask'">
              <el-input v-model="selectedElement.assignee" placeholder="请输入办理人" @change="updateElementAssignee" />
            </el-form-item>
            <el-form-item label="优先级" v-if="selectedElement.type === 'bpmn:UserTask'">
              <el-select v-model="selectedElement.priority" placeholder="请选择" @change="updateElementPriority">
                <el-option label="最低" value="0" />
                <el-option label="低" value="20" />
                <el-option label="普通" value="50" />
                <el-option label="高" value="80" />
                <el-option label="最高" value="100" />
              </el-select>
            </el-form-item>
            <el-form-item label="时限(天)" v-if="selectedElement.type === 'bpmn:UserTask'">
              <el-input-number v-model="selectedElement.timeout" :min="0" :max="365" @change="updateElementTimeout" />
            </el-form-item>
            <el-form-item label="表达式" v-if="selectedElement.type === 'bpmn:ServiceTask'">
              <el-input v-model="selectedElement.expression" placeholder="请输入表达式" @change="updateElementExpression" />
            </el-form-item>
          </el-form>
          <el-empty v-else description="请在画布中选择元素" :image-size="60" />
        </div>
      </div>
    </el-card>

    <input type="file" ref="fileInputRef" style="display:none" accept=".bpmn,.xml" @change="handleFileChange" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { FolderChecked, Upload, Download, VideoPlay, CircleClose, User, Cpu, Connection, Document, DocumentCopy } from '@element-plus/icons-vue'
import BpmnModeler from 'bpmn-js/lib/Modeler'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css'

const fileInputRef = ref(null)
const bpmnCanvasRef = ref(null)
const processName = ref('请假流程')
const processKey = ref('leaveProcess')
const processCategory = ref('HR')
const description = ref('')
const selectedElement = ref(null)
const currentXml = ref('')

let bpmnModeler = null

const getElementTypeName = (type) => {
  const typeMap = {
    'bpmn:StartEvent': '开始事件',
    'bpmn:EndEvent': '结束事件',
    'bpmn:UserTask': '用户任务',
    'bpmn:ServiceTask': '服务任务',
    'bpmn:ScriptTask': '脚本任务',
    'bpmn:ManualTask': '手动任务',
    'bpmn:ExclusiveGateway': '排他网关',
    'bpmn:ParallelGateway': '并行网关',
    'bpmn:InclusiveGateway': '包容网关',
    'bpmn:Task': '任务'
  }
  return typeMap[type] || type
}

const createEmptyXml = () => {
  return `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                 xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                 xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                 xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
                 id="Definitions_1"
                 targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="${processKey.value}" name="${processName.value}" isExecutable="true">
    <bpmn:startEvent id="StartEvent_1" name="开始" />
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="${processKey.value}" />
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`
}

const initBpmn = () => {
  const xml = createEmptyXml()
  
  bpmnModeler = new BpmnModeler({
    container: bpmnCanvasRef.value,
    keyboard: { bindTo: document }
  })

  bpmnModeler.importXML(xml, (err) => {
    if (err) {
      console.error('Failed to import BPMN:', err)
      ElMessage.error('流程图初始化失败')
      return
    }
    const canvas = bpmnModeler.get('canvas')
    canvas.zoom('fit-viewport')
    currentXml.value = xml
  })

  bpmnModeler.on('selection.changed', (event) => {
    const element = event.selectedElements[0]
    if (element && element.businessObject) {
      selectedElement.value = {
        id: element.id,
        name: element.businessObject.name || '',
        type: element.$type,
        assignee: element.businessObject.assignee || '',
        priority: element.businessObject.priority || '50',
        timeout: element.businessObject.timeout || 0,
        expression: element.businessObject.expression || ''
      }
    } else {
      selectedElement.value = null
    }
  })

  bpmnModeler.on('commandStack.changed', () => {
    bpmnModeler.saveXML({ format: true }, (err, xml) => {
      if (!err) {
        currentXml.value = xml
      }
    })
  })
}

const updateProcessInfo = () => {
  if (!bpmnModeler) return
  const elementRegistry = bpmnModeler.get('elementRegistry')
  const modeling = bpmnModeler.get('modeling')
  
  const process = elementRegistry.get(processKey.value)
  if (process) {
    modeling.updateProperties(process, {
      name: processName.value,
      documentation: description.value
    })
  }
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

const updateElementAssignee = () => {
  if (!selectedElement.value || !bpmnModeler) return
  const modeling = bpmnModeler.get('modeling')
  const elementRegistry = bpmnModeler.get('elementRegistry')
  const element = elementRegistry.get(selectedElement.value.id)
  if (element) {
    modeling.updateProperties(element, { assignee: selectedElement.value.assignee })
  }
}

const updateElementPriority = () => {
  if (!selectedElement.value || !bpmnModeler) return
  const modeling = bpmnModeler.get('modeling')
  const elementRegistry = bpmnModeler.get('elementRegistry')
  const element = elementRegistry.get(selectedElement.value.id)
  if (element) {
    modeling.updateProperties(element, { priority: selectedElement.value.priority })
  }
}

const updateElementTimeout = () => {
  if (!selectedElement.value || !bpmnModeler) return
  const modeling = bpmnModeler.get('modeling')
  const elementRegistry = bpmnModeler.get('elementRegistry')
  const element = elementRegistry.get(selectedElement.value.id)
  if (element) {
    modeling.updateProperties(element, { timeout: selectedElement.value.timeout })
  }
}

const updateElementExpression = () => {
  if (!selectedElement.value || !bpmnModeler) return
  const modeling = bpmnModeler.get('modeling')
  const elementRegistry = bpmnModeler.get('elementRegistry')
  const element = elementRegistry.get(selectedElement.value.id)
  if (element) {
    modeling.updateProperties(element, { expression: selectedElement.value.expression })
  }
}

const onDragStart = (type) => {
  console.log('Drag start:', type)
}

const handleNew = () => {
  ElMessageBox.confirm('确定要新建流程吗？当前未保存的内容将丢失。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    processName.value = '新流程'
    processKey.value = 'newProcess'
    processCategory.value = 'OFFICE'
    description.value = ''
    
    const xml = createEmptyXml()
    bpmnModeler.importXML(xml, (err) => {
      if (!err) {
        const canvas = bpmnModeler.get('canvas')
        canvas.zoom('fit-viewport')
        ElMessage.success('已创建新流程')
      }
    })
  }).catch(() => {})
}

const handleSave = () => {
  bpmnModeler.saveXML({ format: true }, (err, xml) => {
    if (err) {
      ElMessage.error('保存失败: ' + err.message)
      return
    }
    console.log('流程XML:', xml)
    ElMessage.success('流程已保存到本地')
  })
}

const handleDeploy = () => {
  bpmnModeler.saveXML({ format: true }, (err, xml) => {
    if (err) {
      ElMessage.error('部署失败: ' + err.message)
      return
    }
    ElMessage.success('流程部署成功！可通过"流程定义"页面查看')
  })
}

const handleDownload = () => {
  bpmnModeler.saveXML({ format: true }, (err, xml) => {
    if (err) {
      ElMessage.error('下载失败: ' + err.message)
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

const handleImport = () => {
  fileInputRef.value.click()
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const reader = new FileReader()
  reader.onload = (e) => {
    const xml = e.target.result
    bpmnModeler.importXML(xml, (err) => {
      if (err) {
        ElMessage.error('导入失败: ' + err.message)
        return
      }
      const canvas = bpmnModeler.get('canvas')
      canvas.zoom('fit-viewport')
      
      bpmnModeler.saveXML({ format: true }, (saveErr, savedXml) => {
        if (!saveErr && savedXml) {
          const keyMatch = savedXml.match(/id="([^"]+)"/)
          if (keyMatch) {
            processKey.value = keyMatch[1]
          }
          const nameMatch = savedXml.match(/name="([^"]+)"/)
          if (nameMatch) {
            processName.value = nameMatch[1]
          }
        }
      })
      
      ElMessage.success('导入成功')
    })
  }
  reader.readAsText(file)
  event.target.value = ''
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
  width: 160px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  padding: 15px;
  overflow-y: auto;
}

.palette-title {
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}

.palette-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.palette-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  background: #f5f7fa;
  border-radius: 4px;
  cursor: move;
  font-size: 12px;
  color: #606266;
  transition: all 0.2s;
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
  width: 280px;
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
