<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../api/request'

const route = useRoute()
const router = useRouter()
const submission = ref(null)
const loading = ref(true)

// 未出最终结果前的中间状态，需要轮询刷新
const PENDING_STATUSES = ['PENDING', 'RUNNING', 'JUDGING']
let pollTimer = null

async function loadSubmission(silent = false) {
  if (!silent) {
    loading.value = true
  }
  try {
    const result = await request.get('/submission/' + route.params.id)
    if (result.code === 200) {
      submission.value = result.data
    }
  } catch (error) {
    console.error('加载提交详情失败:', error)
  } finally {
    if (!silent) {
      loading.value = false
    }
  }
}

// 判题中时每 2 秒刷新一次，出结果后自动停止
function startPolling() {
  if (pollTimer) return
  pollTimer = setInterval(() => {
    const status = submission.value && submission.value.status
    if (!status || !PENDING_STATUSES.includes(status)) {
      stopPolling()
      return
    }
    loadSubmission(true)
  }, 2000)
}

function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

function goBack() {
  router.push('/submissions')
}

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

onMounted(async () => {
  await loadSubmission()
  startPolling()
})
onUnmounted(stopPolling)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2 class="title">提交详情 #{{ route.params.id }}</h2>
      <el-button @click="goBack">返回列表</el-button>
    </div>

    <div v-loading="loading">
      <template v-if="submission">
        <el-card shadow="never" class="card">
          <template #header><span class="card-title">基本信息</span></template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="提交ID">{{ submission.id }}</el-descriptions-item>
            <el-descriptions-item label="题目">
              <router-link :to="'/problem/' + submission.problemId" class="problem-link">
                {{ submission.problemTitle || ('题目 #' + submission.problemId) }}
              </router-link>
            </el-descriptions-item>
            <el-descriptions-item label="题目版本">{{ submission.problemVersion }}</el-descriptions-item>
            <el-descriptions-item label="用户">
              {{ submission.username || ('用户 #' + submission.userId) }}
            </el-descriptions-item>
            <el-descriptions-item label="编程语言">{{ submission.language }}</el-descriptions-item>
            <el-descriptions-item label="判题状态">
              <el-tag :type="getStatusType(submission.status)" effect="light">
                {{ getStatusText(submission.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="得分">{{ submission.score !== null ? submission.score : '-' }}</el-descriptions-item>
            <el-descriptions-item label="耗时">{{ submission.timeUsed !== null ? submission.timeUsed + ' ms' : '-' }}</el-descriptions-item>
            <el-descriptions-item label="内存">{{ submission.memoryUsed !== null ? submission.memoryUsed + ' KB' : '-' }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatTime(submission.submitTime) }}</el-descriptions-item>
            <el-descriptions-item label="判题时间">{{ formatTime(submission.judgeTime) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card v-if="submission.errorMessage" shadow="never" class="card">
          <template #header><span class="card-title">错误信息</span></template>
          <pre class="error-message">{{ submission.errorMessage }}</pre>
        </el-card>

        <el-card shadow="never" class="card">
          <template #header><span class="card-title">提交代码</span></template>
          <pre class="code-block"><code>{{ submission.code }}</code></pre>
        </el-card>
      </template>
      <el-empty v-else-if="!loading" description="提交记录不存在" />
    </div>
  </div>
</template>

<style scoped>
.card {
  border-radius: var(--radius);
  margin-bottom: 24px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e2a4a;
}

.problem-link {
  color: #2a9d8f;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
}

.problem-link:hover {
  text-decoration: underline;
}

.error-message {
  margin: 0;
  padding: 16px;
  background: #fef0f0;
  border-left: 4px solid #f56c6c;
  border-radius: 6px;
  color: #c45656;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.code-block {
  margin: 0;
  padding: 16px;
  background: #f6f8fa;
  border: 1px solid #e6e9f0;
  border-radius: 8px;
  overflow-x: auto;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #1e2a4a;
}

.code-block code {
  white-space: pre;
}
</style>