<template>
  <div class="designer-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程设计器</span>
          <el-button-group>
            <el-button type="primary" @click="handleNew">
              <el-icon><FolderAdd /></el-icon>新建
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
            <div class="palette-item" @click="addElement('startEvent')">
              <el-icon><VideoPlay /></el-icon>
              <span>开始事件</span>
            </div>
            <div class="palette-item" @click="addElement('endEvent')">
              <el-icon><CircleClose /></el-icon>
              <span>结束事件</span>
            </div>
            <div class="palette-item" @click="addElement('userTask')">
              <el-icon><User /></el-icon>
              <span>用户任务</span>
            </div>
            <div class="palette-item" @click="addElement('serviceTask')">
              <el-icon><Cpu /></el-icon>
              <span>服务任务</span>
            </div>
            <div class="palette-item" @click="addElement('exclusiveGateway')">
              <el-icon><Switch /></el-icon>
              <span>排他网关</span>
            </div>
            <div class="palette-item" @click="addElement('parallelGateway')">
              <el-icon><Connection /></el-icon>
              <span>并行网关</span>
            </div>
            <div class="palette-item" @click="addElement('scriptTask')">
              <el-icon><Document /></el-icon>
              <span>脚本任务</span>
            </div>
            <div class="palette-item" @click="addElement('manualTask')">
              <el-icon><Memo /></el-icon>
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
            <el-form-item label="办理人" v-if="isUserTask(selectedElement.type)">
              <el-input v-model="selectedElement.assignee" placeholder="请输入办理人" @change="updateElementAssignee" />
            </el-form-item>
          </el-form>
          <el-empty v-else description="点击左侧元素添加到画布" :image-size="60" />
        </div>
      </div>
    </el-card>

    <input type="file" ref="fileInputRef" style="display:none" accept=".bpmn,.xml" @change="handleFileChange" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { FolderAdd, FolderChecked, Upload, Download, VideoPlay, CircleClose, User, Cpu, Switch, Connection, Document, Memo } from '@element-plus/icons-vue'
import { deployXml } from '@/api/workflow/process'
import BpmnModeler from 'bpmn-js/lib/Modeler'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css'

const bpmnCanvasRef = ref(null)
const fileInputRef = ref(null)
const processName = ref('请假流程')
const processKey = ref('leaveProcess')
const processCategory = ref('HR')
const selectedElement = ref(null)

let bpmnModeler = null

const elementTypeMap = {
  startEvent: 'bpmn:StartEvent',
  endEvent: 'bpmn:EndEvent',
  userTask: 'bpmn:UserTask',
  serviceTask: 'bpmn:ServiceTask',
  exclusiveGateway: 'bpmn:ExclusiveGateway',
  parallelGateway: 'bpmn:ParallelGateway',
  scriptTask: 'bpmn:ScriptTask',
  manualTask: 'bpmn:ManualTask'
}

const elementNames = {
  startEvent: '开始',
  endEvent: '结束',
  userTask: '用户任务',
  serviceTask: '服务任务',
  exclusiveGateway: '排他网关',
  parallelGateway: '并行网关',
  scriptTask: '脚本任务',
  manualTask: '手动任务'
}

const getElementTypeName = (type) => {
  const map = {
    'bpmn:StartEvent': '开始事件',
    'bpmn:EndEvent': '结束事件',
    'bpmn:UserTask': '用户任务',
    'bpmn:ServiceTask': '服务任务',
    'bpmn:ExclusiveGateway': '排他网关',
    'bpmn:ParallelGateway': '并行网关',
    'bpmn:ScriptTask': '脚本任务',
    'bpmn:ManualTask': '手动任务'
  }
  return map[type] || type || ''
}

const isUserTask = (type) => {
  return type === 'bpmn:UserTask'
}

const createNewXml = () => {
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

const initBpmn = async () => {
  await nextTick()
  
  bpmnModeler = new BpmnModeler({
    container: bpmnCanvasRef.value,
    keyboard: { bindTo: document }
  })

  const savedXml = localStorage.getItem('bpmn_xml')
  const savedKey = localStorage.getItem('bpmn_process_key')
  
  if (savedXml && savedXml.length > 100) {
    try {
      await bpmnModeler.importXML(savedXml)
      if (savedKey) {
        processKey.value = savedKey
      }
      ElMessage.success('已恢复之前保存的流程')
    } catch (err) {
      console.error('Failed to load saved BPMN:', err)
      await bpmnModeler.importXML(createNewXml())
    }
  } else {
    try {
      await bpmnModeler.importXML(createNewXml())
    } catch (err) {
      console.error('Failed to import BPMN:', err)
      ElMessage.error('流程图初始化失败')
    }
  }
  
  try {
    const canvas = bpmnModeler.get('canvas')
    canvas.zoom('fit-viewport')
  } catch (err) {
    console.error('Failed to zoom:', err)
  }

  bpmnModeler.on('selection.changed', (event) => {
    const element = event.newSelection[0]
    if (element && element.businessObject) {
      selectedElement.value = {
        id: element.id,
        name: element.businessObject.name || '',
        type: element.businessObject.$type,
        assignee: element.businessObject.assignee || ''
      }
    } else {
      selectedElement.value = null
    }
  })
}

const addElement = (type) => {
  if (!bpmnModeler) {
    ElMessage.error('流程设计器未初始化')
    return
  }

  try {
    const elementFactory = bpmnModeler.get('elementFactory')
    const modeling = bpmnModeler.get('modeling')
    const bpmnType = elementTypeMap[type]
    
    if (!bpmnType) {
      ElMessage.warning('不支持的元素类型')
      return
    }

    const x = 150 + Math.random() * 300
    const y = 150 + Math.random() * 200

    const newElement = elementFactory.createElement({
      type: bpmnType
    })

    modeling.createElement({
      type: bpmnType,
      x: x,
      y: y
    }, newElement)

    modeling.updateProperties(newElement, {
      name: elementNames[type]
    })

    ElMessage.success('已添加' + elementNames[type])
  } catch (err) {
    console.error('Create element error:', err)
    ElMessage.error('添加元素失败: ' + err.message)
  }
}

const updateProcessInfo = () => {
  if (!bpmnModeler) return
  try {
    const elementRegistry = bpmnModeler.get('elementRegistry')
    const process = elementRegistry.get(processKey.value)
    if (process) {
      const modeling = bpmnModeler.get('modeling')
      modeling.updateProperties(process, {
        name: processName.value
      })
    }
  } catch (err) {
    console.error('Update process info error:', err)
  }
}

const updateElementName = () => {
  if (!selectedElement.value || !bpmnModeler) return
  try {
    const elementRegistry = bpmnModeler.get('elementRegistry')
    const modeling = bpmnModeler.get('modeling')
    const element = elementRegistry.get(selectedElement.value.id)
    if (element) {
      modeling.updateProperties(element, { name: selectedElement.value.name })
    }
  } catch (err) {
    console.error('Update element name error:', err)
  }
}

const updateElementAssignee = () => {
  if (!selectedElement.value || !bpmnModeler) return
  try {
    const elementRegistry = bpmnModeler.get('elementRegistry')
    const modeling = bpmnModeler.get('modeling')
    const element = elementRegistry.get(selectedElement.value.id)
    if (element) {
      modeling.updateProperties(element, { assignee: selectedElement.value.assignee })
    }
  } catch (err) {
    console.error('Update assignee error:', err)
  }
}

const handleNew = () => {
  ElMessageBox.confirm('确定要新建流程吗？当前未保存的内容将丢失。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    processName.value = '新流程'
    processKey.value = 'newProcess'
    processCategory.value = 'OFFICE'
    selectedElement.value = null
    
    localStorage.removeItem('bpmn_xml')
    localStorage.removeItem('bpmn_xml_backup')
    localStorage.removeItem('bpmn_xml_time')
    localStorage.removeItem('bpmn_process_key')
    localStorage.removeItem('bpmn_process_name')
    localStorage.removeItem('bpmn_deployment_id')
    
    try {
      await bpmnModeler.importXML(createNewXml())
      const canvas = bpmnModeler.get('canvas')
      canvas.zoom('fit-viewport')
      ElMessage.success('已创建新流程')
    } catch (err) {
      console.error('Create new BPMN error:', err)
      ElMessage.error('创建失败: ' + err.message)
    }
  }).catch(() => {})
}

const checkBackendConnection = async () => {
  try {
    const response = await fetch('/flowable/process/definition/list', {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' }
    })
    return response.ok
  } catch (error) {
    return false
  }
}

const handleSave = async () => {
  try {
    const { xml } = await bpmnModeler.saveXML({ format: true })
    
    localStorage.setItem('bpmn_xml', xml)
    localStorage.setItem('bpmn_xml_time', new Date().toISOString())
    localStorage.setItem('bpmn_process_key', processKey.value)
    localStorage.setItem('bpmn_process_name', processName.value)
    
    const isConnected = await checkBackendConnection()
    
    if (!isConnected) {
      ElMessage.success('流程已保存到本地存储（后端服务未连接）')
      return
    }
    
    try {
      const response = await deployXml(xml, processName.value)
      
      if (response.code === 200 || response.code === 0) {
        localStorage.setItem('bpmn_deployment_id', response.data)
        ElMessage.success('流程保存成功（已同步到服务器）')
      } else {
        ElMessage.warning('流程已保存到本地，服务器同步失败')
      }
    } catch (err) {
      console.error('Deploy to server error:', err)
      ElMessage.warning('流程已保存到本地，服务器同步失败')
    }
  } catch (err) {
    console.error('Save error:', err)
    ElMessage.error('保存失败: ' + (err.message || '请检查网络连接'))
  }
}

const handleDeploy = async () => {
  if (!processKey.value) {
    ElMessage.warning('请先设置流程Key')
    return
  }
  
  try {
    const { xml } = await bpmnModeler.saveXML({ format: true })
    
    localStorage.setItem('bpmn_xml', xml)
    localStorage.setItem('bpmn_xml_time', new Date().toISOString())
    localStorage.setItem('bpmn_process_key', processKey.value)
    localStorage.setItem('bpmn_process_name', processName.value)
    
    const isConnected = await checkBackendConnection()
    
    if (!isConnected) {
      ElMessage.success('流程已保存到本地存储（后端服务未连接）')
      return
    }
    
    try {
      const response = await deployXml(xml, processName.value)
      
      if (response.code === 200 || response.code === 0) {
        localStorage.setItem('bpmn_deployment_id', response.data)
        ElMessage.success('流程部署成功！可通过"流程定义"页面查看')
      } else {
        ElMessage.warning('流程已保存到本地，服务器部署失败')
      }
    } catch (err) {
      console.error('Deploy to server error:', err)
      ElMessage.warning('流程已保存到本地，服务器部署失败')
    }
  } catch (err) {
    console.error('Deploy error:', err)
    ElMessage.error('部署失败: ' + (err.message || '请检查网络连接'))
  }
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
    a.download = processKey.value + '.bpmn'
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
  reader.onload = async (e) => {
    const xml = e.target.result
    try {
      await bpmnModeler.importXML(xml)
      const canvas = bpmnModeler.get('canvas')
      canvas.zoom('fit-viewport')
      ElMessage.success('导入成功')
    } catch (err) {
      ElMessage.error('导入失败: ' + err.message)
    }
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
  height: calc(100vh - 220px);
  min-height: 500px;
}

.palette {
  width: 140px;
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
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: all 0.2s;
}

.palette-item:hover {
  background: #409eff;
  color: #fff;
}

.canvas {
  flex: 1;
  background: #f5f7fa;
  overflow: hidden;
}

#bpmn-canvas {
  width: 100%;
  height: 100%;
  min-height: 500px;
}

.properties-panel {
  width: 260px;
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
