<template>
  <div class="config-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>参数管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增参数
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="参数名称">
          <el-input v-model="queryParams.configName" placeholder="请输入参数名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="参数键名">
          <el-input v-model="queryParams.configKey" placeholder="请输入参数键名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="参数状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
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

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="id" label="参数编号" width="100" />
        <el-table-column prop="configName" label="参数名称" />
        <el-table-column prop="configKey" label="参数键名" />
        <el-table-column prop="configValue" label="参数键值" show-overflow-tooltip />
        <el-table-column prop="configType" label="系统内置" width="120">
          <template #default="{ row }">
            <el-tag :type="row.configType === 'Y' ? 'success' : 'info'">
              {{ row.configType === 'Y' ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑参数' : '新增参数'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入参数键名" />
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="form.configValue" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="系统内置">
          <el-radio-group v-model="form.configType">
            <el-radio label="Y">是</el-radio>
            <el-radio label="N">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('config', [
  { id: '1', configName: '主框架页-默认皮肤', configKey: 'sys.index.skinName', configValue: 'skin-blue', configType: 'Y', isGlobal: 'Y', status: '0', createTime: '2026-01-01 10:00:00', remark: '默认皮肤：蓝色' },
  { id: '2', configName: '用户管理-初始密码', configKey: 'sys.user.initPassword', configValue: '123456', configType: 'Y', isGlobal: 'Y', status: '0', createTime: '2026-01-01 10:00:00', remark: '初始密码' },
  { id: '3', configName: '主框架页-侧边栏主题', configKey: 'sys.index.sideTheme', configValue: 'theme-dark', configType: 'Y', isGlobal: 'Y', status: '0', createTime: '2026-01-01 10:00:00', remark: '暗色侧边栏' },
  { id: '4', configName: '用户头像', configKey: 'sys.user.avatar', configValue: '', configType: 'N', isGlobal: 'N', status: '0', createTime: '2026-01-01 10:00:00', remark: '用户默认头像' }
])

const configApi = createLocalApi('config')

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const queryParams = reactive({
  configName: '',
  configKey: '',
  status: ''
})

const form = reactive({
  id: '',
  configName: '',
  configKey: '',
  configValue: '',
  configType: 'N',
  isGlobal: 'N',
  status: '0',
  remark: ''
})

const rules = {
  configName: [{ required: true, message: '请输入参数名称', trigger: 'blur' }],
  configKey: [{ required: true, message: '请输入参数键名', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入参数键值', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const response = await configApi.list(queryParams)
    if (response.code === 200) {
      tableData.value = response.data.list || []
    }
  } catch (error) {
    console.error('获取参数列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryParams.configName = ''
  queryParams.configKey = ''
  queryParams.status = ''
  getList()
}

const handleAdd = () => {
  form.id = ''
  form.configName = ''
  form.configKey = ''
  form.configValue = ''
  form.configType = 'N'
  form.isGlobal = 'N'
  form.status = '0'
  form.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    let response
    if (form.id) {
      response = await configApi.update(form)
    } else {
      response = await configApi.add(form)
    }
    
    if (response.code === 200) {
      ElMessage.success(response.msg || '操作成功')
      dialogVisible.value = false
      getList()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除参数 "${row.configName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await configApi.delete(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 15px;
}
</style>
