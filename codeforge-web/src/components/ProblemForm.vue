<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../api/request'

const route = useRoute()
const router = useRouter()
const isEditMode = ref(false)
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const currentStatus = ref('')

// 表单数据
const formData = ref({
  title: '',
  difficulty: 'EASY',
  tags: [],
  description: '',
  inputFormat: '',
  outputFormat: '',
  timeLimit: 1000,
  memoryLimit: 256,
  testCases: [{ input: '', output: '', isPublic: true }]
})

const tagInput = ref('')

const difficultyOptions = [
  { value: 'EASY', label: '简单' },
  { value: 'MEDIUM', label: '中等' },
  { value: 'HARD', label: '困难' }
]

// 如果是编辑模式，加载题目数据
onMounted(async () => {
  if (route.params.id) {
    isEditMode.value = true
    await loadProblem()
  }
})

async function loadProblem() {
  loading.value = true
  try {
    const result = await request.get('/problem/' + route.params.id)
    if (result.code === 200) {
      const problem = result.data
      currentStatus.value = problem.status
      formData.value = {
        title: problem.title,
        difficulty: problem.difficulty,
        tags: problem.tags || [],
        description: problem.description,
        inputFormat: problem.inputFormat || '',
        outputFormat: problem.outputFormat || '',
        timeLimit: problem.timeLimit,
        memoryLimit: problem.memoryLimit,
        testCases: problem.testCases && problem.testCases.length > 0
          ? problem.testCases.map(tc => ({ input: tc.input, output: tc.output, isPublic: tc.isPublic !== false }))
          : [{ input: '', output: '', isPublic: true }]
      }
    }
  } catch (error) {
    errorMsg.value = '加载题目失败'
  } finally {
    loading.value = false
  }
}

function addTag() {
  const tag = tagInput.value.trim()
  if (tag && !formData.value.tags.includes(tag)) {
    formData.value.tags.push(tag)
    tagInput.value = ''
  }
}

function removeTag(index) {
  formData.value.tags.splice(index, 1)
}

function addTestCase() {
  formData.value.testCases.push({ input: '', output: '', isPublic: true })
}

function removeTestCase(index) {
  if (formData.value.testCases.length > 1) {
    formData.value.testCases.splice(index, 1)
  }
}

function validateForm() {
  if (!formData.value.title.trim()) {
    errorMsg.value = '标题不能为空'
    return false
  }
  if (formData.value.tags.length === 0) {
    errorMsg.value = '至少添加一个标签'
    return false
  }
  if (!formData.value.description.trim()) {
    errorMsg.value = '描述不能为空'
    return false
  }
  if (!formData.value.timeLimit || formData.value.timeLimit <= 0) {
    errorMsg.value = '时间限制必须大于0'
    return false
  }
  if (!formData.value.memoryLimit || formData.value.memoryLimit <= 0) {
    errorMsg.value = '内存限制必须大于0'
    return false
  }
  if (formData.value.testCases.length === 0) {
    errorMsg.value = '至少添加一个测试用例'
    return false
  }
  for (let i = 0; i < formData.value.testCases.length; i++) {
    const tc = formData.value.testCases[i]
    if (!tc.input.trim() || !tc.output.trim()) {
      errorMsg.value = `测试用例 ${i + 1} 的输入或输出不能为空`
      return false
    }
  }
  return true
}

async function submitForm() {
  errorMsg.value = ''
  successMsg.value = ''

  if (!validateForm()) {
    return
  }

  loading.value = true

  try {
    let result
    if (isEditMode.value) {
      result = await request.put('/problem/' + route.params.id, formData.value)
    } else {
      result = await request.post('/problem', formData.value)
    }

    if (result.code === 200) {
      successMsg.value = isEditMode.value ? '更新成功！' : '创建成功！'
      setTimeout(() => {
        router.push('/problems')
      }, 1500)
    } else {
      errorMsg.value = result.message || '操作失败'
    }
  } catch (error) {
    errorMsg.value = '操作失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 把草稿题目提交给管理员审核：DRAFT -> PENDING_REVIEW
async function submitReview() {
  errorMsg.value = ''
  successMsg.value = ''
  loading.value = true
  try {
    const result = await request.post('/problem/' + route.params.id + '/submit-review')
    if (result.code === 200) {
      currentStatus.value = 'PENDING_REVIEW'
      successMsg.value = '已提交审核，等待管理员处理'
    } else {
      errorMsg.value = result.message || '提交审核失败'
    }
  } catch (error) {
    errorMsg.value = '操作失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/problems')
}
</script>

<template>
  <div class="page form-page">
    <div class="page-header">
      <h2 class="title">{{ isEditMode ? '编辑题目' : '创建题目' }}</h2>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-card v-loading="loading && isEditMode" shadow="never" class="card">
      <el-form label-position="top">
        <el-form-item label="题目标题 *">
          <el-input
            v-model="formData.title"
            placeholder="请输入题目标题"
            maxlength="200"
          />
        </el-form-item>

        <el-form-item label="难度 *">
          <el-select v-model="formData.difficulty" class="full">
            <el-option
              v-for="opt in difficultyOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="标签 *">
          <div class="tag-edit">
            <el-input
              v-model="tagInput"
              placeholder="输入标签后回车或点添加"
              class="tag-input"
              @keyup.enter="addTag"
            />
            <el-button @click="addTag">添加</el-button>
          </div>
          <div v-if="formData.tags.length > 0" class="tags">
            <el-tag
              v-for="(tag, index) in formData.tags"
              :key="index"
              closable
              @close="removeTag(index)"
            >{{ tag }}</el-tag>
          </div>
        </el-form-item>

        <el-form-item label="题目描述 *">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="6"
            placeholder="请输入题目描述"
          />
        </el-form-item>

        <el-form-item label="输入格式">
          <el-input
            v-model="formData.inputFormat"
            type="textarea"
            :rows="3"
            placeholder="例如：第一行一个整数 n，第二行 n 个整数（可选）"
          />
        </el-form-item>

        <el-form-item label="输出格式">
          <el-input
            v-model="formData.outputFormat"
            type="textarea"
            :rows="3"
            placeholder="例如：一个整数，表示结果（可选）"
          />
        </el-form-item>

        <div class="limit-row">
          <el-form-item label="时间限制 (ms) *">
            <el-input-number v-model="formData.timeLimit" :min="1" :step="100" class="full" />
          </el-form-item>
          <el-form-item label="内存限制 (MB) *">
            <el-input-number v-model="formData.memoryLimit" :min="1" :step="64" class="full" />
          </el-form-item>
        </div>

        <el-form-item>
          <template #label>
            <div class="case-header">
              <span>测试用例 *</span>
              <el-button type="primary" plain @click="addTestCase">添加测试用例</el-button>
            </div>
          </template>
          <div class="cases">
            <div v-for="(testCase, index) in formData.testCases" :key="index" class="case">
              <div class="case-title">
                <span>用例 {{ index + 1 }}</span>
                <div class="case-ops">
                  <el-checkbox v-model="testCase.isPublic">公开样例</el-checkbox>
                  <el-button
                    v-if="formData.testCases.length > 1"
                    type="danger"
                    plain
                    size="small"
                    @click="removeTestCase(index)"
                  >删除</el-button>
                </div>
              </div>
              <div class="case-grid">
                <div>
                  <span class="case-label">输入</span>
                  <el-input
                    v-model="testCase.input"
                    type="textarea"
                    :rows="3"
                    placeholder="测试输入"
                  />
                </div>
                <div>
                  <span class="case-label">输出</span>
                  <el-input
                    v-model="testCase.output"
                    type="textarea"
                    :rows="3"
                    placeholder="期望输出"
                  />
                </div>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-alert v-if="errorMsg" type="error" :closable="false" show-icon :title="errorMsg" class="msg" />
        <el-alert v-if="successMsg" type="success" :closable="false" show-icon :title="successMsg" class="msg" />

        <div class="actions">
          <el-button
            type="primary"
            :loading="loading"
            @click="submitForm"
          >{{ isEditMode ? '更新题目' : '创建题目' }}</el-button>
          <el-button
            v-if="isEditMode && currentStatus === 'DRAFT'"
            type="warning"
            :loading="loading"
            @click="submitReview"
          >提交审核</el-button>
          <el-button @click="goBack">取消</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.card {
  border-radius: var(--radius);
}

.full {
  width: 100%;
}

.tag-edit {
  display: flex;
  gap: 8px;
  width: 100%;
}

.tag-input {
  flex: 1;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.limit-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.case-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cases {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
}

.case {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
}

.case-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
  color: #1e2a4a;
}

.case-ops {
  display: flex;
  align-items: center;
  gap: 12px;
}

.case-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.case-label {
  display: block;
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
}

.msg {
  margin-bottom: 16px;
}

.actions {
  display: flex;
  gap: 12px;
}
</style>