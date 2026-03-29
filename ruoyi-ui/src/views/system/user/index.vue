<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增用户
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名称">
          <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="用户状态" clearable>
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
        <el-table-column type="selection" width="55" />
        <el-table-column prop="userId" label="用户编号" width="100" />
        <el-table-column prop="userName" label="用户名称" />
        <el-table-column prop="nickName" label="用户昵称" />
        <el-table-column prop="deptName" label="部门" />
        <el-table-column prop="phonenumber" label="手机号码" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            <el-button link type="warning" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button link type="primary" @click="handleAuthRole(row)">分配角色</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名称" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名称" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="nickName">
          <el-input v-model="form.nickName" placeholder="请输入用户昵称" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-tree-select v-model="form.deptId" :data="deptOptions" :props="{ value: 'id', label: 'deptName', children: 'children' }" placeholder="请选择部门" check-strictly />
        </el-form-item>
        <el-form-item label="手机号码" prop="phonenumber">
          <el-input v-model="form.phonenumber" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'
import storage, { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('user', [
  { userId: 1, userName: 'admin', nickName: '管理员', deptId: '100', deptName: '研发部', phonenumber: '13800138000', email: 'admin@company.com', status: '0', createTime: '2026-03-01 10:00:00' },
  { userId: 2, userName: 'user1', nickName: '张三', deptId: '101', deptName: '市场部', phonenumber: '13800138001', email: 'user1@company.com', status: '0', createTime: '2026-03-15 14:30:00' },
  { userId: 3, userName: 'user2', nickName: '李四', deptId: '100', deptName: '研发部', phonenumber: '13800138002', email: 'user2@company.com', status: '1', createTime: '2026-03-20 09:15:00' }
])

const userApi = createLocalApi('user', { idField: 'userId' })

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const deptOptions = ref([
  { id: '1', deptName: '总公司', children: [
    { id: '100', deptName: '研发部' },
    { id: '101', deptName: '市场部' }
  ]}
])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  phonenumber: '',
  status: ''
})

const form = reactive({
  id: '',
  userId: '',
  userName: '',
  nickName: '',
  deptId: '',
  phonenumber: '',
  email: '',
  status: '0'
})

const rules = {
  userName: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
  nickName: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const response = await userApi.list(queryParams)
    if (response.code === 200) {
      tableData.value = response.data.list || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.userName = ''
  queryParams.phonenumber = ''
  queryParams.status = ''
  handleQuery()
}

const handleAdd = () => {
  form.id = ''
  form.userId = ''
  form.userName = ''
  form.nickName = ''
  form.deptId = ''
  form.phonenumber = ''
  form.email = ''
  form.status = '0'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, {
    id: row.userId,
    userId: row.userId,
    userName: row.userName,
    nickName: row.nickName,
    deptId: row.deptId,
    phonenumber: row.phonenumber,
    email: row.email,
    status: row.status
  })
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    let response
    if (form.id) {
      response = await userApi.update(form)
    } else {
      response = await userApi.add(form)
    }
    
    if (response.code === 200) {
      ElMessage.success(response.msg || '操作成功')
      dialogVisible.value = false
      getList()
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除用户 "${row.userName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await userApi.delete(row.userId)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleResetPwd = (row) => {
  ElMessageBox.confirm(`确认重置用户 "${row.userName}" 的密码吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('密码重置成功，新密码为：123456')
  })
}

const handleAuthRole = (row) => {
  ElMessage.info(`分配角色功能：${row.userName}`)
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
