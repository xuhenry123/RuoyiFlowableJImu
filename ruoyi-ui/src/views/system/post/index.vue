<template>
  <div class="post-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>岗位管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增岗位
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="岗位名称">
          <el-input v-model="queryParams.postName" placeholder="请输入岗位名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="岗位编码">
          <el-input v-model="queryParams.postCode" placeholder="请输入岗位编码" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="岗位状态" clearable>
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
        <el-table-column prop="id" label="岗位编号" width="100" />
        <el-table-column prop="postCode" label="岗位编码" />
        <el-table-column prop="postName" label="岗位名称" />
        <el-table-column prop="postSort" label="显示顺序" width="100" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑岗位' : '新增岗位'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入岗位编码" />
        </el-form-item>
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="postSort">
          <el-input-number v-model="form.postSort" :min="0" />
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

initLocalData('post', [
  { id: '1', postCode: 'ceo', postName: '首席执行官', postSort: 1, status: '0', createTime: '2026-01-01 10:00:00', remark: '公司最高管理者' },
  { id: '2', postCode: 'cto', postName: '技术总监', postSort: 2, status: '0', createTime: '2026-01-01 10:00:00', remark: '技术负责人' },
  { id: '3', postCode: 'dev', postName: '开发工程师', postSort: 3, status: '0', createTime: '2026-01-01 10:00:00', remark: '研发人员' },
  { id: '4', postCode: 'test', postName: '测试工程师', postSort: 4, status: '0', createTime: '2026-01-01 10:00:00', remark: '测试人员' },
  { id: '5', postCode: 'pm', postName: '产品经理', postSort: 5, status: '0', createTime: '2026-01-01 10:00:00', remark: '产品负责人' }
])

const postApi = createLocalApi('post')

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const queryParams = reactive({
  postName: '',
  postCode: '',
  status: ''
})

const form = reactive({
  id: '',
  postCode: '',
  postName: '',
  postSort: 0,
  status: '0',
  remark: ''
})

const rules = {
  postCode: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }],
  postName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const response = await postApi.list(queryParams)
    if (response.code === 200) {
      tableData.value = response.data.list || []
    }
  } catch (error) {
    console.error('获取岗位列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryParams.postName = ''
  queryParams.postCode = ''
  queryParams.status = ''
  getList()
}

const handleAdd = () => {
  form.id = ''
  form.postCode = ''
  form.postName = ''
  form.postSort = 0
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
      response = await postApi.update(form)
    } else {
      response = await postApi.add(form)
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
  ElMessageBox.confirm(`确认删除岗位 "${row.postName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await postApi.delete(row.id)
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
