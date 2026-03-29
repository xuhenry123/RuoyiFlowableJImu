<template>
  <div class="gen-container">
    <el-card>
      <template #header>
        <span>代码生成</span>
      </template>
      <el-alert type="info" :closable="false">
        <p>提供代码生成功能，支持根据数据库表结构生成前后端代码</p>
      </el-alert>
      <div class="gen-area">
        <el-empty description="代码生成功能" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.gen-area {
  margin-top: 20px;
  padding: 40px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
}
</style>
