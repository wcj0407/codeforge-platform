<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/request'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const users = ref([])
const loading = ref(true)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const roleOptions = [
  { value: 'USER', label: '普通用户' },
  { value: 'AUTHOR', label: '出题人' },
  { value: 'ADMIN', label: '管理员' }
]
const roleMap = {
  USER: { label: '普通用户', type: 'info' },
  AUTHOR: { label: '出题人', type: 'warning' },
  ADMIN: { label: '管理员', type: 'danger' }
}
function getRoleName(r) { return roleMap[r] ? roleMap[r].label : r }
function getRoleType(r) { return roleMap[r] ? roleMap[r].type : 'info' }

// 是不是当前登录的管理员自己：是则不给操作入口，避免把自己禁用/降权
function isSelf(row) {
  return String(row.id) === String(userStore.id)
}

// 管理员查看该用户的提交记录
function goUserSubmissions(row) {
  router.push({
    path: '/submissions',
    query: { userId: row.id, username: row.username }
  })
}

async function loadUsers() {
  loading.value = true
  try {
    const result = await request.get('/admin/users', {
      params: { page: page.value, size: size.value }
    })
    if (result.code === 200) {
      users.value = result.data.records
      total.value = result.data.total
    }
  } catch (e) {
    console.error('加载用户列表失败:', e)
  } finally {
    loading.value = false
  }
}

// 禁用 / 启用账号
async function changeStatus(row, val) {
  try {
    const result = await request.put('/admin/users/' + row.id + '/status', { status: val })
    if (result.code === 200) {
      row.status = val
      ElMessage.success(val === 1 ? '已启用该账号' : '已禁用该账号')
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 修改角色
async function changeRole(row, val) {
  try {
    const result = await request.put('/admin/users/' + row.id + '/role', { role: val })
    if (result.code === 200) {
      row.role = val
      ElMessage.success('角色已更新')
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败，请稍后重试')
  }
}

function formatTime(t) {
  if (!t) return '-'
  return String(t).replace('T', ' ')
}

// 管理员重置密码：生成临时密码并展示给管理员，由管理员转告用户
async function resetPassword(row) {
  try {
    await ElMessageBox.confirm(
      '确定重置用户「' + row.username + '」的密码吗？',
      '重置密码',
      { type: 'warning', confirmButtonText: '重置', cancelButtonText: '取消' }
    )
  } catch (e) {
    return
  }
  try {
    const result = await request.put('/admin/users/' + row.id + '/reset-password')
    if (result.code === 200) {
      ElMessageBox.alert(
        '该用户的临时密码为：' + result.data + '\n请转告用户登录后自行修改密码。',
        '重置成功',
        { confirmButtonText: '知道了' }
      )
    } else {
      ElMessage.error(result.message || '重置失败')
    }
  } catch (e) {
    ElMessage.error('重置失败，请稍后重试')
  }
}

onMounted(loadUsers)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div class="head-left">
        <h2 class="title">用户管理</h2>
        <p class="subtitle">共 {{ total }} 个用户</p>
      </div>
    </div>

    <el-card shadow="never" class="card">
      <el-table v-loading="loading" :data="users" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column label="邮箱" min-width="200">
          <template #default="{ row }">{{ row.email || '未绑定' }}</template>
        </el-table-column>
        <el-table-column label="角色" width="150">
          <template #default="{ row }">
            <el-select
              v-if="!isSelf(row)"
              :model-value="row.role"
              size="small"
              class="role-select"
              @change="(val) => changeRole(row, val)"
            >
              <el-option
                v-for="opt in roleOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
            <el-tag v-else :type="getRoleType(row.role)" effect="light" size="small">
              {{ getRoleName(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="130">
          <template #default="{ row }">
            <el-switch
              v-if="!isSelf(row)"
              :model-value="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => changeStatus(row, val)"
            />
            <el-tag v-else type="success" effect="plain" size="small">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" min-width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button
              size="small"
              @click.stop="goUserSubmissions(row)"
            >查看提交</el-button>
            <el-button
              v-if="!isSelf(row)"
              type="danger"
              plain
              size="small"
              @click.stop="resetPassword(row)"
            >重置密码</el-button>
            <span v-else class="self-hint">当前账号</span>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无用户" />
        </template>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="prev, pager, next, total"
          background
          @current-change="loadUsers"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.card {
  border-radius: var(--radius);
}

.role-select {
  width: 120px;
}

.self-hint {
  color: #909399;
  font-size: 13px;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>