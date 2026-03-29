<template>
  <div class="report-design-container">
    <el-card>
      <template #header>
        <span>报表设计</span>
      </template>
      <div class="design-area">
        <el-alert type="info" :closable="false">
          <p>集成 JimuReport 积木报表在线设计器</p>
          <p>设计器地址: <a href="http://jimureport.baike888.com" target="_blank">http://jimureport.baike888.com</a></p>
          <p>功能: 可视化拖拽设计报表、图表，支持数据源配置、打印导出等</p>
        </el-alert>
        <div class="design-preview">
          <el-empty description="点击上方链接打开积木报表设计器进行报表设计" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.design-area {
  min-height: 500px;
}

.design-preview {
  margin-top: 30px;
  padding: 20px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
}
</style>
