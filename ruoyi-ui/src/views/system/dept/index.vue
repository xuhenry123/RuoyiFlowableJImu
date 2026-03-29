<template>
  <div class="dept-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>部门管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增部门
          </el-button>
        </div>
      </template>
      
      <el-table v-if="tableData.length > 0" :data="tableData" border default-expand-all row-key="id">
        <el-table-column prop="deptName" label="部门名称" />
        <el-table-column prop="orderNum" label="排序" width="100" />
        <el-table-column prop="leader" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="primary" @click="handleAddChild(row)">新增下级</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无数据" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑部门' : '新增部门'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select v-model="form.parentId" :data="deptOptions" :props="{ label: 'deptName', value: 'id' }" placeholder="选择上级部门" check-strictly />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" />
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('dept', [
  { id: '1', parentId: '0', deptName: '总公司', orderNum: 1, leader: 'admin', phone: '021-12345678', email: 'admin@company.com', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '100', parentId: '1', deptName: '研发部', orderNum: 1, leader: 'dev', phone: '021-12345679', email: 'dev@company.com', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '101', parentId: '1', deptName: '市场部', orderNum: 2, leader: 'market', phone: '021-12345680', email: 'market@company.com', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '102', parentId: '100', deptName: '前端组', orderNum: 1, leader: 'fe', phone: '021-12345681', email: 'fe@company.com', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '103', parentId: '100', deptName: '后端组', orderNum: 2, leader: 'be', phone: '021-12345682', email: 'be@company.com', status: '0', createTime: '2026-01-01 10:00:00' }
])

const deptApi = createLocalApi('dept')

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({
  id: '',
  parentId: '0',
  deptName: '',
  orderNum: 0,
  leader: '',
  phone: '',
  email: '',
  status: '0'
})

const rules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const deptOptions = computed(() => {
  return [{ id: '0', deptName: '顶级部门' }, ...tableData.value]
})

const buildTree = (data, parentId = '0') => {
  return data
    .filter(item => item.parentId === parentId)
    .map(item => ({
      ...item,
      children: buildTree(data, item.id)
    }))
}

const getList = async () => {
  loading.value = true
  try {
    const response = await deptApi.list()
    if (response.code === 200) {
      tableData.value = buildTree(response.data.list || [])
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.id = ''
  form.parentId = '0'
  form.deptName = ''
  form.orderNum = 0
  form.leader = ''
  form.phone = ''
  form.email = ''
  form.status = '0'
  dialogVisible.value = true
}

const handleAddChild = (row) => {
  form.id = ''
  form.parentId = row.id
  form.deptName = ''
  form.orderNum = 0
  form.leader = ''
  form.phone = ''
  form.email = ''
  form.status = '0'
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
      response = await deptApi.update(form)
    } else {
      response = await deptApi.add(form)
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
  ElMessageBox.confirm(`确认删除部门 "${row.deptName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deptApi.delete(row.id)
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
</style>
