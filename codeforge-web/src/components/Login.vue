<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref('')
const password = ref('')
const errorMsg = ref('')
const loading = ref(false)

async function login() {
  errorMsg.value = ''
  loading.value = true
  try {
    const result = await request.post('/auth/login', {
      username: username.value,
      password: password.value
    })
    if (result.code === 200) {
      userStore.setLoginInfo(result.data)
      router.push('/problems')
    } else {
      errorMsg.value = result.message || '登录失败'
    }
  } catch (e) {
    errorMsg.value = '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function goToRegister() {
  router.push('/register')
}

// ---------- 忘记密码 ----------
const forgotVisible = ref(false)
const sending = ref(false)
const resetting = ref(false)
const resetError = ref('')
const countdown = ref(0)
let timer = null
const resetForm = ref({ username: '', code: '', newPassword: '', confirmPassword: '' })

function openForgot() {
  resetForm.value = { username: username.value, code: '', newPassword: '', confirmPassword: '' }
  resetError.value = ''
  forgotVisible.value = true
}

function startCountdown() {
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
      timer = null
    }
  }, 1000)
}

async function sendResetCode() {
  resetError.value = ''
  if (!resetForm.value.username.trim()) {
    resetError.value = '请输入用户名'
    return
  }
  sending.value = true
  try {
    const result = await request.post('/auth/password/reset-code', {
      username: resetForm.value.username.trim()
    })
    if (result.code === 200) {
      ElMessage.success('如果该账号已绑定邮箱，验证码已发送，请查收')
      startCountdown()
    } else {
      resetError.value = result.message || '发送失败'
    }
  } catch (e) {
    resetError.value = '发送失败，请稍后重试'
  } finally {
    sending.value = false
  }
}

async function submitReset() {
  resetError.value = ''
  const form = resetForm.value
  if (!form.username.trim()) {
    resetError.value = '请输入用户名'
    return
  }
  if (!/^\d{6}$/.test(form.code)) {
    resetError.value = '请输入6位数字验证码'
    return
  }
  if (!form.newPassword || form.newPassword.length < 6) {
    resetError.value = '新密码至少6位'
    return
  }
  if (form.newPassword !== form.confirmPassword) {
    resetError.value = '两次输入的新密码不一致'
    return
  }
  resetting.value = true
  try {
    const result = await request.post('/auth/password/reset', {
      username: form.username.trim(),
      code: form.code,
      newPassword: form.newPassword
    })
    if (result.code === 200) {
      ElMessage.success('密码重置成功，请用新密码登录')
      username.value = form.username.trim()
      password.value = ''
      forgotVisible.value = false
    } else {
      resetError.value = result.message || '重置失败'
    }
  } catch (e) {
    resetError.value = '重置失败，请稍后重试'
  } finally {
    resetting.value = false
  }
}

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <!-- 左：品牌渐变区 -->
      <div class="brand-panel">
        <div class="brand-logo">CF</div>
        <h1 class="brand-name">CodeForge</h1>
        <p class="brand-slogan">在线编程练习与评测平台</p>
        <div class="code-decor">
          <span>// 每次提交，都是</span>
          <span>// 一次进步</span>
        </div>
      </div>

      <!-- 右：登录表单区 -->
      <div class="form-panel">
        <h2 class="panel-title">欢迎回来</h2>
        <p class="panel-sub">登录你的账号，继续刷题</p>

        <el-input
          v-model="username"
          placeholder="用户名"
          size="large"
          class="field"
          @keyup.enter="login"
        />
        <el-input
          v-model="password"
          placeholder="密码"
          type="password"
          size="large"
          show-password
          class="field"
          @keyup.enter="login"
        />

        <p class="forgot"><a @click="openForgot">忘记密码？</a></p>

        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

        <el-button
          class="submit-btn"
          size="large"
          :loading="loading"
          @click="login"
        >登录</el-button>

        <p class="switch">还没有账号？<a @click="goToRegister">立即注册</a></p>
      </div>
    </div>

    <!-- 忘记密码弹窗 -->
    <el-dialog
      v-model="forgotVisible"
      width="440px"
      align-center
      class="forgot-dialog"
    >
      <template #header>
        <div class="dialog-head">
          <span class="dialog-mark">CF</span>
          <div class="dialog-head-text">
            <h3 class="dialog-title">找回密码</h3>
            <p class="dialog-sub">验证绑定邮箱后即可设置新密码</p>
          </div>
        </div>
      </template>

      <el-form label-position="top" class="forgot-form">
        <el-form-item label="用户名">
          <el-input v-model="resetForm.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="验证码">
          <div class="code-row">
            <el-input v-model="resetForm.code" placeholder="6 位数字验证码" maxlength="6" size="large" />
            <el-button
              type="primary"
              plain
              :disabled="countdown > 0"
              :loading="sending"
              class="code-btn"
              @click="sendResetCode"
            >
              {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="resetForm.newPassword" type="password" show-password placeholder="新密码（至少 6 位）" size="large" />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="resetForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" size="large" />
        </el-form-item>
      </el-form>

      <el-alert v-if="resetError" type="error" :closable="false" show-icon :title="resetError" class="msg" />
      <div class="notice">未绑定邮箱的账号请联系管理员重置密码</div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="forgotVisible = false">取消</el-button>
          <el-button type="primary" :loading="resetting" @click="submitReset">重置密码</el-button>
        </div>
      </template>
    </el-dialog>
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

/* 左：品牌渐变区 */
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

.code-decor .indent {
  padding-left: 16px;
}

/* 右：表单区 */
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

.forgot {
  text-align: right;
  margin: -6px 0 12px;
  font-size: 13px;
}

.forgot a {
  color: #2a9d8f;
  cursor: pointer;
}

.forgot a:hover {
  text-decoration: underline;
}

.code-row {
  display: flex;
  gap: 10px;
  width: 100%;
}

.code-row .el-input {
  flex: 1;
}

.code-btn {
  flex-shrink: 0;
}

.dialog-head {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dialog-mark {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #2b3a67, #1e2a4a);
  color: #7fd7cc;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.dialog-head-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.dialog-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e2a4a;
}

.dialog-sub {
  margin: 0;
  font-size: 12px;
  color: #90a0b9;
}

.forgot-form {
  margin-top: 4px;
}

.forgot-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.forgot-form :deep(.el-form-item__label) {
  padding-bottom: 6px;
  font-size: 13px;
  color: #5b6577;
}

.notice {
  margin-top: 4px;
  padding: 10px 12px;
  background: #f5f9f8;
  border-left: 3px solid #2a9d8f;
  border-radius: 6px;
  color: #5b6577;
  font-size: 12px;
  line-height: 1.6;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.msg {
  margin-bottom: 12px;
}

/* 窄屏：隐藏品牌区，只留表单 */
@media (max-width: 640px) {
  .brand-panel {
    display: none;
  }
}
</style>

<style>
/* 弹窗本身被 teleport 到 body，用非 scoped 的强限定类名调整外观 */
.forgot-dialog {
  border-radius: 16px;
}

.forgot-dialog .el-dialog__header {
  padding: 24px 24px 12px;
  margin-right: 0;
}

.forgot-dialog .el-dialog__body {
  padding: 8px 24px 4px;
}

.forgot-dialog .el-dialog__footer {
  padding: 12px 24px 20px;
}
</style>