<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="login-box">
        <div class="login-header">
          <h2>RuoYi Flowable Jimu</h2>
          <p>应用开发平台</p>
        </div>
        <el-form ref="loginFormRef" :model="form" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="form.code"
              placeholder="验证码"
              size="large"
              style="width: 60%"
              prefix-icon="CircleCheck"
              @keyup.enter="handleLogin"
            />
            <div class="captcha">
              <img :src="captchaUrl" @click="getCaptcha" alt="验证码" />
            </div>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="form.rememberMe">记住我</el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <div class="tech-stack">
            <el-tag type="info">RuoYi-Vue-Plus 5.6.0</el-tag>
            <el-tag type="info">Flowable 6.8</el-tag>
            <el-tag type="info">JimuReport 1.9</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from '@/store'
import { ElMessage } from 'element-plus'
import { User, Lock, CircleCheck } from '@element-plus/icons-vue'

const router = useRouter()
const store = useStore()

const loginFormRef = ref()
const loading = ref(false)
const captchaEnabled = ref(false)
const captchaUrl = ref('')

const form = reactive({
  username: 'admin',
  password: 'admin123',
  code: '',
  rememberMe: false
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const getCaptcha = () => {
  captchaUrl.value = '/api/captcha?uuid=' + Math.random()
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    store.setToken('mock-token-' + Date.now())
    store.setUser({ userName: form.username, nickName: '管理员' })
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

getCaptcha()
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-bg {
  width: 100%;
  display: flex;
  justify-content: center;
}

.login-box {
  width: 450px;
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #409eff;
  font-size: 26px;
  margin-bottom: 10px;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.captcha {
  margin-left: 10px;
  cursor: pointer;
}

.captcha img {
  height: 40px;
  border-radius: 4px;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
}

.tech-stack {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
