<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增角色
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="roleId" label="角色编号" width="100" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleKey" label="权限字符" />
        <el-table-column prop="sort" label="显示顺序" width="100" />
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
            <el-button link type="warning" @click="handlePermission(row)">权限</el-button>
            <el-button link type="primary" @click="handleDataScope(row)">数据范围</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([
  { roleId: 1, roleName: '超级管理员', roleKey: 'admin', sort: 1, status: '0', createTime: '2026-01-01 00:00:00' },
  { roleId: 2, roleName: '普通角色', roleKey: 'common', sort: 2, status: '0', createTime: '2026-02-15 10:30:00' },
  { roleId: 3, roleName: '访客', roleKey: 'guest', sort: 3, status: '1', createTime: '2026-03-01 08:00:00' }
])

const handleAdd = () => ElMessage.info('新增角色')
const handleEdit = (row) => ElMessage.info(`编辑角色: ${row.roleName}`)
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除角色 "${row.roleName}" 吗？`, '提示', { type: 'warning' })
    .then(() => { ElMessage.success('删除成功') })
}
const handlePermission = (row) => ElMessage.info(`权限配置: ${row.roleName}`)
const handleDataScope = (row) => ElMessage.info(`数据范围: ${row.roleName}`)

onMounted(() => {})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
