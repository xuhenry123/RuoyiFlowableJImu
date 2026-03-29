<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-title">用户总数</p>
              <p class="stat-value">1,234</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="30"><Connection /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-title">流程实例</p>
              <p class="stat-value">568</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="30"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-title">报表数量</p>
              <p class="stat-value">89</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="30"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-title">待办任务</p>
              <p class="stat-value">42</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>技术栈信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="基础框架">RuoYi-Vue-Plus 5.6.0</el-descriptions-item>
            <el-descriptions-item label="Java版本">17</el-descriptions-item>
            <el-descriptions-item label="Spring Boot">3.5.12</el-descriptions-item>
            <el-descriptions-item label="流程引擎">Flowable 6.8.0</el-descriptions-item>
            <el-descriptions-item label="报表组件">JimuReport 1.9.0</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
            <el-descriptions-item label="缓存">Redis</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>快捷入口</span>
          </template>
          <div class="quick-links">
            <el-button type="primary" @click="$router.push('/workflow')">流程管理</el-button>
            <el-button type="success" @click="$router.push('/report')">报表中心</el-button>
            <el-button type="warning">积木设计器</el-button>
            <el-button type="info">Flowable设计器</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { User, Connection, DataLine, Clock } from '@element-plus/icons-vue'
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-title {
  color: #909399;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
