<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Cpu } from '@element-plus/icons-vue'
import { login } from '@/api/sys'

const router = useRouter()
const loading = ref(false)
const loginFormRef = ref()

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        if (res && res.token) {
          localStorage.setItem('token', res.token)
          localStorage.setItem('username', res.nickname || res.username)
          ElMessage.success('登录成功，欢迎回来！')
          router.push('/dashboard')
        }
      } catch (error: any) {
        // error already handled by interceptor, or just show generic error
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <el-icon class="login-logo-icon"><Cpu /></el-icon>
        <h2>Orivon API Hub</h2>
        <p>高性能 · 易管控 · 企业级 API 网关</p>
      </div>
      <el-form 
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            :prefix-icon="User"
            placeholder="请输入管理员账号" 
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            :prefix-icon="Lock"
            type="password"
            placeholder="请输入密码" 
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            class="login-btn" 
            size="large" 
            :loading="loading" 
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        测试账号: admin / admin123
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1890ff 0%, #364d79 100%);
}

.login-box {
  width: 420px;
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-logo-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 12px;
}

.login-header h2 {
  margin: 0;
  font-size: 26px;
  color: #303133;
  letter-spacing: 1px;
}

.login-header p {
  margin: 10px 0 0;
  font-size: 14px;
  color: #909399;
}

.login-form {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  font-size: 16px;
  letter-spacing: 4px;
}

.login-footer {
  text-align: center;
  font-size: 13px;
  color: #c0c4cc;
}
</style>
