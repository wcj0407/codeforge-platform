<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'
import CodeEditor from './CodeEditor.vue'

const route = useRoute()
const router = useRouter()
const problem = ref(null)
const loading = ref(true)
const currentUser = ref(null)
const actionError = ref('')

// 只有题目作者本人或管理员可以编辑/提交审核/下架
const canManage = computed(() => {
  if (!currentUser.value || !problem.value) return false
  return currentUser.value.role === 'ADMIN' || currentUser.value.id === problem.value.creatorId
})

// 后端存英文枚举，前端翻译成中文 + Element Plus 标签类型
const difficultyMap = {
  EASY: { label: '简单', type: 'success' },
  MEDIUM: { label: '中等', type: 'warning' },
  HARD: { label: '困难', type: 'danger' }
}
function difficultyText(d) { return difficultyMap[d] ? difficultyMap[d].label : d }
function difficultyType(d) { return difficultyMap[d] ? difficultyMap[d].type : 'info' }

// 只展示公开样例（作者/管理员即使拿到隐藏用例，也不在这里展示）
const publicTestCases = computed(() => {
  if (!problem.value || !problem.value.testCases) return []
  return problem.value.testCases.filter(tc => tc.isPublic)
})

async function loadUser() {
  try {
    const result = await request.get('/user/me')
    if (result.code === 200) {
      currentUser.value = result.data
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

async function loadDetail() {
  loading.value = true
  const result = await request.get('/problem/' + route.params.id)
  if (result.code === 200) {
    problem.value = result.data
  }
  loading.value = false
}

// 提交审核：DRAFT -> PENDING_REVIEW（作者本人或管理员）
async function submitReview() {
  actionError.value = ''
  try {
    const result = await request.post('/problem/' + route.params.id + '/submit-review')
    if (result.code === 200) {
      await loadDetail()
    } else {
      actionError.value = result.message || '操作失败'
    }
  } catch (error) {
    actionError.value = '操作失败，请稍后重试'
  }
}

// 下架：PUBLISHED -> OFFLINE（作者本人或管理员）
async function offline() {
  actionError.value = ''
  try {
    const result = await request.post('/problem/' + route.params.id + '/offline')
    if (result.code === 200) {
      await loadDetail()
    } else {
      actionError.value = result.message || '操作失败'
    }
  } catch (error) {
    actionError.value = '操作失败，请稍后重试'
  }
}

function goBack() {
  router.push('/problems')
}

function goToEdit() {
  router.push('/problem/' + route.params.id + '/edit')
}

function handleSubmitSuccess(submission) {
  router.push('/submission/' + submission.id)
}

const copiedKey = ref('')

async function copyText(text, key) {
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text)
    } else {
      // 降级：非安全上下文或旧浏览器用临时 textarea + execCommand
      const ta = document.createElement('textarea')
      ta.value = text
      ta.style.position = 'fixed'
      ta.style.opacity = '0'
      document.body.appendChild(ta)
      ta.select()
      document.execCommand('copy')
      document.body.removeChild(ta)
    }
    ElMessage.success({ message: '已复制', duration: 1500 })
    copiedKey.value = key
    setTimeout(() => { copiedKey.value = '' }, 1500)
  } catch (e) {
    ElMessage.error('复制失败，请手动选择文本')
  }
}

onMounted(() => {
  loadDetail()
  loadUser()
})
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2 class="title">{{ problem ? problem.title : '加载中...' }}</h2>
      <div class="header-actions">
        <el-button
          v-if="canManage && problem && problem.status === 'DRAFT'"
          type="primary"
          @click="submitReview"
        >提交审核</el-button>
        <el-button
          v-if="canManage && problem && problem.status === 'PUBLISHED'"
          type="danger"
          plain
          @click="offline"
        >下架题目</el-button>
        <el-button
          v-if="canManage"
          type="warning"
          plain
          @click="goToEdit"
        >编辑题目</el-button>
        <el-button @click="goBack">返回列表</el-button>
      </div>
    </div>

    <!-- 被驳回题目：展示管理员填的驳回原因 -->
    <el-alert
      v-if="problem && problem.status === 'REJECTED' && problem.reviewComment"
      type="warning"
      :closable="false"
      show-icon
      class="reject-notice"
    >
      <template #title>
        <strong>审核被驳回：</strong>{{ problem.reviewComment }}
      </template>
    </el-alert>

    <p v-if="actionError" class="action-error">{{ actionError }}</p>

    <el-card v-loading="loading" shadow="never" class="detail-card">
      <template v-if="problem">
        <div class="meta">
          <el-tag :type="difficultyType(problem.difficulty)" effect="light">
            {{ difficultyText(problem.difficulty) }}
          </el-tag>
          <el-tag v-for="t in problem.tags" :key="t" type="info" effect="plain">
            {{ t }}
          </el-tag>
        </div>

        <div class="section">
          <h3 class="section-title">题目描述</h3>
          <p class="desc">{{ problem.description }}</p>
        </div>

        <div class="io">
          <div v-if="problem.inputFormat" class="io-item">
            <span class="io-label">输入格式</span>
            <span class="io-value">{{ problem.inputFormat }}</span>
          </div>
          <div v-if="problem.outputFormat" class="io-item">
            <span class="io-label">输出格式</span>
            <span class="io-value">{{ problem.outputFormat }}</span>
          </div>
        </div>

        <div class="limits">
          <el-tag type="info" effect="plain" size="small">时间限制 {{ problem.timeLimit }} ms</el-tag>
          <el-tag type="info" effect="plain" size="small">内存限制 {{ problem.memoryLimit }} MB</el-tag>
        </div>

        <!-- 公开样例：让用户先看清输入输出格式 -->
        <div v-if="publicTestCases.length > 0" class="section samples-section">
          <h3 class="section-title">示例</h3>
          <div class="samples">
            <div v-for="(tc, i) in publicTestCases" :key="i" class="sample">
              <div class="sample-head">示例 {{ i + 1 }}</div>
              <div class="sample-grid">
                <div>
                  <div class="sample-label-row">
                    <span class="sample-label">输入</span>
                    <el-button
                      link
                      size="small"
                      class="copy-btn"
                      @click="copyText(tc.input, 'in' + i)"
                    >{{ copiedKey === 'in' + i ? '已复制' : '复制' }}</el-button>
                  </div>
                  <pre class="sample-box">{{ tc.input }}</pre>
                </div>
                <div>
                  <div class="sample-label-row">
                    <span class="sample-label">输出</span>
                    <el-button
                      link
                      size="small"
                      class="copy-btn"
                      @click="copyText(tc.output, 'out' + i)"
                    >{{ copiedKey === 'out' + i ? '已复制' : '复制' }}</el-button>
                  </div>
                  <pre class="sample-box">{{ tc.output }}</pre>
                </div>
              </div>
            </div>
          </div>
        </div>

        <CodeEditor
          :problem-id="problem.id"
          @submit-success="handleSubmitSuccess"
        />
      </template>
      <el-empty v-else-if="!loading" description="题目不存在或已下线" />
    </el-card>
  </div>
</template>

<style scoped>
.header-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.reject-notice {
  margin-bottom: 16px;
}

.action-error {
  color: #f56c6c;
  margin: 0 0 16px;
}

.detail-card {
  border-radius: var(--radius);
}

.meta {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 4px;
}

.section {
  margin-bottom: 24px;
}

.section-title {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 600;
  color: #1e2a4a;
}

.desc {
  white-space: pre-wrap;
  color: #333;
  margin: 0;
}

.io {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 40px;
  margin-bottom: 20px;
}

.io-item {
  font-size: 14px;
  color: #606266;
}

.io-label {
  font-weight: 600;
  color: #1e2a4a;
  margin-right: 8px;
}

.limits {
  display: flex;
  gap: 8px;
}

.samples-section {
  margin-top: 24px;
}

.samples {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.sample {
  border: 1px solid #ebeef5;
  border-radius: 10px;
  overflow: hidden;
}

.sample-head {
  padding: 8px 14px;
  background: #f5f8f7;
  font-size: 13px;
  font-weight: 600;
  color: #1e2a4a;
}

.sample-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 12px 14px;
}

.sample-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}

.sample-label {
  font-size: 13px;
  color: #909399;
}

.copy-btn {
  padding: 0;
  font-size: 12px;
}

.sample-box {
  margin: 0;
  padding: 10px 12px;
  background: #f7f9fa;
  border: 1px solid #eceff3;
  border-radius: 6px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #1e2a4a;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow: auto;
}

@media (max-width: 640px) {
  .sample-grid {
    grid-template-columns: 1fr;
  }
}
</style>