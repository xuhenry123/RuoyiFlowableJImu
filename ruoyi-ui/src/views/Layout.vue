<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="logo">
        <h3>RuoYi Flowable Jimu</h3>
      </div>
      <el-menu mode="horizontal" :default-active="activeMenu" router>
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/workflow">
          <el-icon><Connection /></el-icon>
          <span>流程管理</span>
        </el-menu-item>
        <el-menu-item index="/report">
          <el-icon><DataLine /></el-icon>
          <span>报表中心</span>
        </el-menu-item>
        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
      <div class="user-info">
        <el-dropdown>
          <span class="user-name">
            <el-avatar :size="32">Admin</el-avatar>
            <span>管理员</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { HomeFilled, Connection, DataLine, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.logo {
  margin-right: 40px;
}

.logo h3 {
  color: #409eff;
  white-space: nowrap;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
}

.user-info {
  margin-left: auto;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}
</style>
