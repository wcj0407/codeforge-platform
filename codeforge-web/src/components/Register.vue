<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()
const username = ref('')
const password = ref('')
const email = ref('')
const errorMsg = ref('')
const successMsg = ref('')
const loading = ref(false)

async function register() {
  errorMsg.value = ''
  successMsg.value = ''

  if (!username.value || !password.value || !email.value) {
    errorMsg.value = '请填写所有字段'
    return
  }

  if (password.value.length < 6) {
    errorMsg.value = '密码长度至少6位'
    return
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value.trim())) {
    errorMsg.value = '邮箱格式不正确'
    return
  }

  loading.value = true
  try {
    const result = await request.post('/auth/register', {
      username: username.value,
      password: password.value,
      email: email.value.trim()
    })

    if (result.code === 200) {
      successMsg.value = '注册成功，2秒后跳转到登录页...'
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      errorMsg.value = result.message || '注册失败'
    }
  } catch (error) {
    errorMsg.value = '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function goToLogin() {
  router.push('/login')
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <!-- 左：品牌渐变区 -->
      <div class="brand-panel">
        <div class="brand-logo">CF</div>
        <h1 class="brand-name">CodeForge</h1>
        <p class="brand-slogan">加入我们，一起刷题成长</p>
        <div class="code-decor">
          <span>// 从今天开始</span>
          <span>// 成为更好的程序员</span>
        </div>
      </div>

      <!-- 右：注册表单区 -->
      <div class="form-panel">
        <h2 class="panel-title">创建账号</h2>
        <p class="panel-sub">注册后即可开始做题</p>

        <el-input
          v-model="username"
          placeholder="用户名"
          size="large"
          class="field"
          @keyup.enter="register"
        />
        <el-input
          v-model="email"
          placeholder="邮箱（用于找回密码）"
          size="large"
          class="field"
          @keyup.enter="register"
        />
        <el-input
          v-model="password"
          placeholder="密码（至少6位）"
          type="password"
          size="large"
          show-password
          class="field"
          @keyup.enter="register"
        />

        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
        <p v-if="successMsg" class="success">{{ successMsg }}</p>

        <el-button
          class="submit-btn"
          size="large"
          :loading="loading"
          @click="register"
        >注册</el-button>

        <p class="switch">已有账号？<a @click="goToLogin">去登录</a></p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  box-sizing: border-box;
  background: #e8ecf4;
}

.auth-card {
  display: flex;
  width: 100%;
  max-width: 760px;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(43, 58, 103, 0.25);
}

.brand-panel {
  flex: 0 0 42%;
  background: linear-gradient(150deg, #2b3a67 0%, #1e2a4a 100%);
  color: #fff;
  padding: 48px 36px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand-logo {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-bottom: 20px;
}

.brand-name {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: #fff;
}

.brand-slogan {
  margin: 10px 0 28px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.code-decor {
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 13px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.55);
  border-left: 2px solid rgba(42, 157, 143, 0.6);
  padding-left: 12px;
}

.form-panel {
  flex: 1;
  background: #fff;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.panel-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1e2a4a;
}

.panel-sub {
  margin: 8px 0 28px;
  font-size: 14px;
  color: #90a0b9;
}

.field {
  margin-bottom: 16px;
}

.error {
  color: #f56c6c;
  font-size: 13px;
  margin: -4px 0 14px;
}

.success {
  color: #2a9d8f;
  font-size: 13px;
  margin: -4px 0 14px;
}

.submit-btn {
  width: 100%;
  background: #2a9d8f;
  border-color: #2a9d8f;
  color: #fff;
  font-weight: 600;
}

.submit-btn:hover {
  background: #24887b;
  border-color: #24887b;
}

.switch {
  margin: 20px 0 0;
  text-align: center;
  font-size: 14px;
  color: #90a0b9;
}

.switch a {
  color: #2a9d8f;
  cursor: pointer;
  font-weight: 600;
}

@media (max-width: 640px) {
  .brand-panel {
    display: none;
  }
}
</style>