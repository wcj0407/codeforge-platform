<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../api/request'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const submissions = ref([])
const loading = ref(true)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const statusFilter = ref('')
// 管理员从用户管理跳转过来时，带上要查看的用户
const userIdFilter = ref(route.query.userId ? Number(route.query.userId) : null)
const filterUsername = ref(route.query.username || '')

const pageTitle = computed(() => {
  if (!userStore.isAdmin) return '我的代码提交'
  return userIdFilter.value ? '用户提交记录' : '代码提交'
})

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'PENDING', label: '等待中' },
  { value: 'JUDGING', label: '判题中' },
  { value: 'ACCEPTED', label: '通过' },
  { value: 'WRONG_ANSWER', label: '答案错误' },
  { value: 'TIME_LIMIT_EXCEEDED', label: '超时' },
  { value: 'MEMORY_LIMIT_EXCEEDED', label: '内存超限' },
  { value: 'RUNTIME_ERROR', label: '运行错误' },
  { value: 'COMPILE_ERROR', label: '编译错误' },
  { value: 'SYSTEM_ERROR', label: '系统错误' }
]

// 状态：后端枚举 -> 中文 + Element Plus 标签类型
const statusMeta = {
  ACCEPTED: { label: '通过', type: 'success' },
  WRONG_ANSWER: { label: '答案错误', type: 'danger' },
  TIME_LIMIT_EXCEEDED: { label: '超时', type: 'warning' },
  MEMORY_LIMIT_EXCEEDED: { label: '内存超限', type: 'warning' },
  RUNTIME_ERROR: { label: '运行错误', type: 'danger' },
  COMPILE_ERROR: { label: '编译错误', type: 'danger' },
  SYSTEM_ERROR: { label: '系统错误', type: 'danger' },
  PENDING: { label: '等待中', type: 'info' },
  JUDGING: { label: '判题中', type: 'primary' }
}
function getStatusText(status) { return statusMeta[status] ? statusMeta[status].label : status }
function getStatusType(status) { return statusMeta[status] ? statusMeta[status].type : 'info' }

async function loadSubmissions() {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value
    }
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    if (userStore.isAdmin && userIdFilter.value) {
      params.userId = userIdFilter.value
    }

    const result = await request.get('/submission', { params })

    if (result.code === 200) {
      submissions.value = result.data.records
      total.value = result.data.total
    }
  } catch (error) {
    console.error('加载提交记录失败:', error)
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push('/submission/' + id)
}

function goBack() {
  router.push('/problems')
}

function filterByStatus() {
  page.value = 1
  loadSubmissions()
}

// 清除「只看某个用户」的筛选，回到全量提交
function clearUserFilter() {
  userIdFilter.value = null
  filterUsername.value = ''
  page.value = 1
  router.replace({ path: '/submissions' })
  loadSubmissions()
}

function handleSizeChange() {
  page.value = 1
  loadSubmissions()
}

function formatTime(timeStr) {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(loadSubmissions)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2 class="title">{{ pageTitle }}</h2>
      <el-button @click="goBack">返回题目列表</el-button>
    </div>

    <el-card shadow="never" class="card">
      <div class="filter-bar">
        <span class="filter-label">筛选状态：</span>
        <el-select
          v-model="statusFilter"
          class="status-select"
          @change="filterByStatus"
        >
          <el-option
            v-for="opt in statusOptions"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
        <el-tag
          v-if="userStore.isAdmin && userIdFilter"
          type="info"
          effect="plain"
          closable
          @close="clearUserFilter"
        >
          用户：{{ filterUsername || ('#' + userIdFilter) }}
        </el-tag>
      </div>

      <el-table
        v-loading="loading"
        :data="submissions"
        stripe
        @row-click="(row) => goDetail(row.id)"
        class="table"
      >
        <el-table-column prop="id" label="提交ID" width="100" />
        <el-table-column v-if="userStore.isAdmin" label="用户" min-width="140">
          <template #default="{ row }">
            {{ row.username || ('用户 #' + row.userId) }}
          </template>
        </el-table-column>
        <el-table-column label="题目" min-width="220">
          <template #default="{ row }">
            <router-link
              :to="'/problem/' + row.problemId"
              class="problem-title"
              @click.stop
            >
              {{ row.problemTitle || ('题目 #' + row.problemId) }}
            </router-link>
          </template>
        </el-table-column>
        <el-table-column label="语言" width="100">
          <template #default="{ row }">
            <el-tag type="info" effect="plain" size="small">{{ row.language }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="130">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分数" width="80">
          <template #default="{ row }">{{ row.score !== null ? row.score : '-' }}</template>
        </el-table-column>
        <el-table-column label="耗时(ms)" width="110">
          <template #default="{ row }">{{ row.timeUsed !== null ? row.timeUsed : '-' }}</template>
        </el-table-column>
        <el-table-column label="内存(KB)" width="110">
          <template #default="{ row }">{{ row.memoryUsed !== null ? row.memoryUsed : '-' }}</template>
        </el-table-column>
        <el-table-column label="提交时间" min-width="180">
          <template #default="{ row }">{{ formatTime(row.submitTime) }}</template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无提交记录" />
        </template>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="prev, pager, next, sizes, total"
          background
          @current-change="loadSubmissions"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.card {
  border-radius: var(--radius);
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
}

.status-select {
  width: 160px;
}

.table {
  cursor: pointer;
}

.problem-title {
  color: #1e2a4a;
  font-weight: 500;
  transition: color 0.2s;
  text-decoration: none;
}

.problem-title:hover {
  color: #2a9d8f;
  text-decoration: underline;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>