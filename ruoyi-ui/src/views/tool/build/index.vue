<template>
  <div class="build-container">
    <el-card>
      <template #header>
        <span>表单构建</span>
      </template>
      <el-alert type="info" :closable="false">
        <p>提供可视化表单构建功能，支持拖拽组件生成表单</p>
      </el-alert>
      <div class="build-area">
        <el-empty description="表单构建功能" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.build-area {
  margin-top: 20px;
  padding: 40px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
}
</style>
