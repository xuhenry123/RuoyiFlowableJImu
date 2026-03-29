<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <div class="logo" :class="{ 'logo-collapse': isCollapse }">
        <img src="@/assets/logo.png" v-if="!isCollapse" />
        <span v-if="!isCollapse">RuoYi</span>
        <img src="@/assets/logo-mini.png" v-else class="mini-logo" />
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="menu"
      >
        <template v-for="item in menus" :key="item.path">
          <el-sub-menu v-if="item.children && item.children.length" :index="item.path">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.path"
              :index="child.path"
            >
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta.title">
              {{ route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :size="32">{{ user?.nickName?.[0] || 'A' }}</el-avatar>
              <span class="username">{{ user?.nickName || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="resetPwd">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from '@/store'
import { getMenu } from '@/utils/menu'
import { ElMessage } from 'element-plus'
import {
  Fold, Expand, ArrowDown, HomeFilled, Setting, User, Role, Connection,
  DataLine, Monitor, Tool, FlowChart, Flow, Ticket, Chart, Edit, Office,
  Menu as MenuIcon, Post, Dict, Config, Operation, Login, Form, Code
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const store = useStore()

const isCollapse = ref(false)
const menus = ref([])
const user = computed(() => store.state.user)

const activeMenu = computed(() => route.path)

const icons = {
  HomeFilled, Setting, User, Role, Connection, DataLine, Monitor, Tool,
  FlowChart, Flow, Ticket, Chart, Edit, Office, Menu: MenuIcon, Post,
  Dict, Config, Operation, Login, Form, Code
}

onMounted(async () => {
  menus.value = await getMenu()
  store.dispatch('user/getInfo')
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
  if (command === 'logout') {
    store.dispatch('user/logout')
    router.push('/login')
    ElMessage.success('退出登录成功')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: #2b3a4a;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.logo img {
  height: 32px;
}

.logo-collapse {
  justify-content: center;
}

.mini-logo {
  width: 24px;
  height: 24px;
}

.menu {
  border-right: none;
  background: transparent;
}

.menu:not(.el-menu--collapse) {
  width: 200px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
}

.main-content {
  background: #f0f2f5;
  padding: 15px;
}
</style>
