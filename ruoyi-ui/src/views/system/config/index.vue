<template>
  <div class="config-container">
    <el-card>
      <template #header>
        <span>参数管理</span>
      </template>
      <el-table :data="tableData" border>
        <el-table-column prop="configId" label="参数编号" width="100" />
        <el-table-column prop="configName" label="参数名称" />
        <el-table-column prop="configKey" label="参数键名" />
        <el-table-column prop="configValue" label="参数键值" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary">修改</el-button>
            <el-button link type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const tableData = ref([
  { configId: 1, configName: '主框架页默认风格', configKey: 'sys.index.skinName', configValue: 'skin-blue', status: '0' },
  { configId: 2, configName: '用户管理-初始密码', configKey: 'sys.user.initPassword', configValue: '123456', status: '0' }
])
</script>
