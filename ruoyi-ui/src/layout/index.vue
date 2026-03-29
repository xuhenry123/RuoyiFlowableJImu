<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <div class="logo" :class="{ 'logo-collapse': isCollapse }">
        <span v-if="!isCollapse">RuoYi</span>
        <span v-else>R</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="menu"
      >
        <el-menu-item index="/index">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
          <el-menu-item index="/system/menu">菜单管理</el-menu-item>
          <el-menu-item index="/system/dept">部门管理</el-menu-item>
          <el-menu-item index="/system/post">岗位管理</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/workflow">
          <template #title>
            <el-icon><Connection /></el-icon>
            <span>流程管理</span>
          </template>
          <el-menu-item index="/workflow/designer">流程设计</el-menu-item>
          <el-menu-item index="/workflow/definition">流程定义</el-menu-item>
          <el-menu-item index="/workflow/instance">流程实例</el-menu-item>
          <el-menu-item index="/workflow/task">我的任务</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/report">
          <template #title>
            <el-icon><DataLine /></el-icon>
            <span>报表中心</span>
          </template>
          <el-menu-item index="/report/list">报表管理</el-menu-item>
          <el-menu-item index="/report/design">报表设计</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/monitor">
          <template #title>
            <el-icon><Monitor /></el-icon>
            <span>系统监控</span>
          </template>
          <el-menu-item index="/monitor/online">在线用户</el-menu-item>
          <el-menu-item index="/monitor/operlog">操作日志</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/tool">
          <template #title>
            <el-icon><Tools /></el-icon>
            <span>系统工具</span>
          </template>
          <el-menu-item index="/tool/build">表单构建</el-menu-item>
          <el-menu-item index="/tool/gen">代码生成</el-menu-item>
        </el-sub-menu>
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
            <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
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
import { ElMessage } from 'element-plus'
import { 
  Fold, Expand, ArrowDown, HomeFilled, Setting, Connection, 
  DataLine, Monitor, Tools 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const store = useStore()

const isCollapse = ref(false)
const user = computed(() => store.user)
const activeMenu = computed(() => route.path)

onMounted(() => {
  if (store.token) {
    store.getInfo()
  }
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
  if (command === 'logout') {
    store.logout()
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
  background: #2b3a4a;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
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
