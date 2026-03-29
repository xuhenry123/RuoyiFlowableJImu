<template>
  <div class="dict-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>字典管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增字典
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="字典名称">
          <el-input v-model="queryParams.dictName" placeholder="请输入字典名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="字典类型">
          <el-input v-model="queryParams.dictType" placeholder="请输入字典类型" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="字典状态" clearable>
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
        <el-table-column prop="id" label="字典编号" width="100" />
        <el-table-column prop="dictName" label="字典名称" />
        <el-table-column prop="dictType" label="字典类型" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="primary" @click="handleData(row)">数据</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑字典' : '新增字典'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型" />
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

    <el-dialog v-model="dataDialogVisible" title="字典数据" width="900px">
      <div class="dict-data-toolbar">
        <el-button type="primary" @click="handleAddData">
          <el-icon><Plus /></el-icon>新增数据
        </el-button>
      </div>
      <el-table :data="dictDataList" border size="small">
        <el-table-column prop="dictLabel" label="字典标签" />
        <el-table-column prop="dictValue" label="字典键值" />
        <el-table-column prop="dictSort" label="排序" width="80" />
        <el-table-column prop="listClass" label="样式" width="100" />
        <el-table-column prop="isDefault" label="默认" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEditData(row)">修改</el-button>
            <el-button link type="danger" size="small" @click="handleDeleteData(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('dict_type', [
  { id: '1', dictName: '用户性别', dictType: 'sys_user_sex', status: '0', createTime: '2026-01-01 10:00:00', remark: '性别编码' },
  { id: '2', dictName: '菜单状态', dictType: 'sys_show_hide', status: '0', createTime: '2026-01-01 10:00:00', remark: '菜单可见性' },
  { id: '3', dictName: '系统状态', dictType: 'sys_normal_disable', status: '0', createTime: '2026-01-01 10:00:00', remark: '系统启用状态' }
])

initLocalData('dict_data', [
  { id: '1', dictSort: 1, dictLabel: '男', dictValue: '1', dictType: 'sys_user_sex', listClass: 'primary', isDefault: 'Y', status: '0' },
  { id: '2', dictSort: 2, dictLabel: '女', dictValue: '2', dictType: 'sys_user_sex', listClass: 'danger', isDefault: 'N', status: '0' },
  { id: '3', dictSort: 1, dictLabel: '显示', dictValue: '0', dictType: 'sys_show_hide', listClass: 'primary', isDefault: 'Y', status: '0' },
  { id: '4', dictSort: 2, dictLabel: '隐藏', dictValue: '1', dictType: 'sys_show_hide', listClass: 'danger', isDefault: 'N', status: '0' }
])

const dictTypeApi = createLocalApi('dict_type')
const dictDataApi = createLocalApi('dict_data')

const loading = ref(false)
const tableData = ref([])
const dictDataList = ref([])
const dialogVisible = ref(false)
const dataDialogVisible = ref(false)
const currentDictType = ref('')
const formRef = ref(null)

const queryParams = reactive({
  dictName: '',
  dictType: '',
  status: ''
})

const form = reactive({
  id: '',
  dictName: '',
  dictType: '',
  status: '0',
  remark: ''
})

const rules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictType: [{ required: true, message: '请输入字典类型', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const response = await dictTypeApi.list(queryParams)
    if (response.code === 200) {
      tableData.value = response.data.list || []
    }
  } catch (error) {
    console.error('获取字典列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryParams.dictName = ''
  queryParams.dictType = ''
  queryParams.status = ''
  getList()
}

const handleAdd = () => {
  form.id = ''
  form.dictName = ''
  form.dictType = ''
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
      response = await dictTypeApi.update(form)
    } else {
      response = await dictTypeApi.add(form)
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
  ElMessageBox.confirm(`确认删除字典 "${row.dictName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await dictTypeApi.delete(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleData = async (row) => {
  currentDictType.value = row.dictType
  try {
    const response = await dictDataApi.list({ dictType: row.dictType })
    if (response.code === 200) {
      dictDataList.value = response.data.list || []
    }
  } catch (error) {
    console.error('获取字典数据失败:', error)
  }
  dataDialogVisible.value = true
}

const handleAddData = () => {
  ElMessage.info('新增字典数据功能')
}

const handleEditData = (row) => {
  ElMessage.info(`编辑字典数据: ${row.dictLabel}`)
}

const handleDeleteData = (row) => {
  ElMessageBox.confirm(`确认删除 "${row.dictLabel}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await dictDataApi.delete(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        handleData({ dictType: currentDictType.value })
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

.dict-data-toolbar {
  margin-bottom: 15px;
}
</style>
