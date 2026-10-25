<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const user = ref(null)
const loading = ref(true)
const stats = ref({
  totalSubmissions: 0,
  acceptedSubmissions: 0,
  solvedProblemCount: 0,
  attemptedProblemCount: 0,
  passRate: 0
})
const statsLoading = ref(true)

async function loadStats() {
  statsLoading.value = true
  try {
    const result = await request.get('/user/stats')
    if (result.code === 200) {
      stats.value = result.data
    }
  } catch (error) {
    console.error('加载刷题统计失败:', error)
  } finally {
    statsLoading.value = false
  }
}

async function loadUserInfo() {
  loading.value = true
  try {
    const result = await request.get('/user/me')
    if (result.code === 200) {
      user.value = result.data
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/problems')
}

// 角色：后端枚举 -> 中文 + Element Plus 标签类型
const roleMap = {
  USER: { label: '普通用户', type: 'info' },
  AUTHOR: { label: '出题人', type: 'warning' },
  ADMIN: { label: '管理员', type: 'danger' }
}
function getRoleName(role) { return roleMap[role] ? roleMap[role].label : role }
function getRoleType(role) { return roleMap[role] ? roleMap[role].type : 'info' }

function formatTime(timeStr) {
  if (!timeStr) return '-'
  return String(timeStr).replace('T', ' ')
}

// ---------- 修改密码 ----------
const pwdDialogVisible = ref(false)
const pwdSubmitting = ref(false)
const pwdError = ref('')
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

function openPwdDialog() {
  pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  pwdError.value = ''
  pwdDialogVisible.value = true
}

async function submitPassword() {
  pwdError.value = ''
  if (!pwdForm.value.oldPassword) {
    pwdError.value = '请输入旧密码'
    return
  }
  if (!pwdForm.value.newPassword || pwdForm.value.newPassword.length < 6) {
    pwdError.value = '新密码至少6位'
    return
  }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    pwdError.value = '两次输入的新密码不一致'
    return
  }
  pwdSubmitting.value = true
  try {
    const result = await request.put('/user/password', {
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    })
    if (result.code === 200) {
      ElMessage.success('密码修改成功')
      pwdDialogVisible.value = false
    } else {
      pwdError.value = result.message || '修改失败'
    }
  } catch (e) {
    pwdError.value = '修改失败，请稍后重试'
  } finally {
    pwdSubmitting.value = false
  }
}

// ---------- 绑定 / 修改邮箱 ----------
const emailDialogVisible = ref(false)
const emailSubmitting = ref(false)
const emailError = ref('')
const emailForm = ref({ email: '' })

function openEmailDialog() {
  emailError.value = ''
  emailForm.value.email = (user.value && user.value.email) || ''
  emailDialogVisible.value = true
}

async function submitEmail() {
  emailError.value = ''
  const email = emailForm.value.email.trim()
  if (!email) {
    emailError.value = '请输入邮箱'
    return
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    emailError.value = '邮箱格式不正确'
    return
  }
  emailSubmitting.value = true
  try {
    const result = await request.put('/user/email', { email })
    if (result.code === 200) {
      ElMessage.success('邮箱已更新')
      emailDialogVisible.value = false
      await loadUserInfo()
    } else {
      emailError.value = result.message || '保存失败'
    }
  } catch (e) {
    emailError.value = '保存失败，请稍后重试'
  } finally {
    emailSubmitting.value = false
  }
}

// ---------- 修改用户名 ----------
const usernameDialogVisible = ref(false)
const usernameSubmitting = ref(false)
const usernameError = ref('')
const usernameForm = ref({ username: '' })

function openUsernameDialog() {
  usernameForm.value.username = (user.value && user.value.username) || ''
  usernameError.value = ''
  usernameDialogVisible.value = true
}

async function submitUsername() {
  usernameError.value = ''
  const name = usernameForm.value.username.trim()
  if (!name) {
    usernameError.value = '用户名不能为空'
    return
  }
  usernameSubmitting.value = true
  try {
    const result = await request.put('/user/username', { username: name })
    if (result.code === 200) {
      // 后端返回 { token, user }，用 setLoginInfo 更新本地 token 和用户名
      userStore.setLoginInfo(result.data)
      ElMessage.success('用户名已修改')
      usernameDialogVisible.value = false
      await loadUserInfo()
    } else {
      usernameError.value = result.message || '修改失败'
    }
  } catch (e) {
    usernameError.value = '修改失败，请稍后重试'
  } finally {
    usernameSubmitting.value = false
  }
}

// ---------- 上传头像 ----------
const avatarUploading = ref(false)

async function handleAvatarUpload(options) {
  const file = options.file
  const formData = new FormData()
  formData.append('file', file)
  avatarUploading.value = true
  try {
    const result = await request.post('/user/avatar', formData)
    if (result.code === 200) {
      userStore.setAvatar(result.data)
      ElMessage.success('头像已更新')
      await loadUserInfo()
    } else {
      ElMessage.error(result.message || '上传失败')
    }
  } catch (e) {
    ElMessage.error('上传失败，请稍后重试')
  } finally {
    avatarUploading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
  loadStats()
})
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2 class="title">个人信息</h2>
      <el-button @click="goBack">返回首页</el-button>
    </div>

    <el-card v-loading="loading" shadow="never" class="card">
      <template v-if="user">
        <div class="avatar-section">
          <el-avatar :size="72" class="avatar" :src="user.avatar || undefined">
            {{ user.username ? user.username.charAt(0).toUpperCase() : 'U' }}
          </el-avatar>
          <div class="basic-info">
            <h3 class="username">{{ user.username }}</h3>
            <el-tag :type="getRoleType(user.role)" effect="light">
              {{ getRoleName(user.role) }}
            </el-tag>
          </div>
          <el-upload
            :show-file-list="false"
            :http-request="handleAvatarUpload"
            accept="image/*"
            class="avatar-upload"
          >
            <el-button size="small" :loading="avatarUploading">
              {{ user && user.avatar ? '更换头像' : '上传头像' }}
            </el-button>
          </el-upload>
        </div>

        <el-divider />

        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ user.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
          <el-descriptions-item label="角色">{{ getRoleName(user.role) }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ user.email || '未绑定' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatTime(user.createTime) }}</el-descriptions-item>
        </el-descriptions>
      </template>
      <el-empty v-else-if="!loading" description="无法加载用户信息" />
    </el-card>

    <el-card v-loading="statsLoading" shadow="never" class="card">
      <template #header><span class="card-title">刷题统计</span></template>
      <div class="stats">
        <div class="stat">
          <span class="stat-num">{{ stats.solvedProblemCount }}</span>
          <span class="stat-label">通过题数</span>
        </div>
        <div class="stat">
          <span class="stat-num">{{ stats.totalSubmissions }}</span>
          <span class="stat-label">提交总数</span>
        </div>
        <div class="stat">
          <span class="stat-num">{{ stats.passRate }}%</span>
          <span class="stat-label">通过率</span>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="card">
      <template #header><span class="card-title">安全设置</span></template>
      <div class="action-buttons">
        <el-button type="primary" plain @click="openUsernameDialog">修改用户名</el-button>
        <el-button type="primary" plain @click="openPwdDialog">修改密码</el-button>
        <el-button type="primary" plain @click="openEmailDialog">
          {{ user && user.email ? '修改邮箱' : '绑定邮箱' }}
        </el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="card">
      <template #header><span class="card-title">快捷操作</span></template>
      <div class="action-buttons">
        <el-button @click="router.push('/problems')">题目列表</el-button>
        <el-button @click="router.push('/submissions')">我的代码提交</el-button>
        <el-button
          v-if="user && (user.role === 'AUTHOR' || user.role === 'ADMIN')"
          type="primary"
          @click="router.push('/problem/create')"
        >创建题目</el-button>
      </div>
    </el-card>

    <!-- 修改用户名弹窗 -->
    <el-dialog v-model="usernameDialogVisible" title="修改用户名" width="440px">
      <el-form label-position="top">
        <el-form-item label="新用户名">
          <el-input v-model="usernameForm.username" placeholder="请输入新用户名" />
        </el-form-item>
      </el-form>
      <el-alert v-if="usernameError" type="error" :closable="false" show-icon :title="usernameError" class="msg" />
      <template #footer>
        <el-button @click="usernameDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="usernameSubmitting" @click="submitUsername">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="440px">
      <el-form label-position="top">
        <el-form-item label="旧密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="至少6位" />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
        </el-form-item>
      </el-form>
      <el-alert v-if="pwdError" type="error" :closable="false" show-icon :title="pwdError" class="msg" />
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdSubmitting" @click="submitPassword">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 绑定/修改邮箱弹窗 -->
    <el-dialog
      v-model="emailDialogVisible"
      :title="(user && user.email) ? '修改邮箱' : '绑定邮箱'"
      width="440px"
    >
      <el-form label-position="top">
        <el-form-item label="邮箱">
          <el-input v-model="emailForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>
      </el-form>
      <el-alert v-if="emailError" type="error" :closable="false" show-icon :title="emailError" class="msg" />
      <template #footer>
        <el-button @click="emailDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="emailSubmitting" @click="submitEmail">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card {
  border-radius: var(--radius);
  margin-bottom: 24px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 8px;
}

.avatar {
  background: linear-gradient(135deg, #2b3a67 0%, #1e2a4a 100%);
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  flex-shrink: 0;
}

.basic-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.avatar-upload {
  align-self: flex-start;
}

.username {
  margin: 0;
  font-size: 22px;
  color: #1e2a4a;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e2a4a;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  text-align: center;
}

.stat {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 8px 0;
}

.stat + .stat {
  border-left: 1px solid #f0f2f7;
}

.stat-num {
  font-size: 28px;
  font-weight: 700;
  color: #2a9d8f;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

@media (max-width: 640px) {
  .stats {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .stat + .stat {
    border-left: none;
    border-top: 1px solid #f0f2f7;
    padding-top: 16px;
  }
}

.msg {
  margin-top: 8px;
}
</style>