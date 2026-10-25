<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const route = useRoute()
const router = useRouter()
const isEdit = !!route.params.id

const title = ref('')
const description = ref('')
const startTime = ref('')
const endTime = ref('')
const allProblems = ref([])
const selectedIds = ref([])
const loading = ref(true)
const saving = ref(false)
const errorMsg = ref('')

async function loadProblems() {
  const result = await request.get('/problem', { params: { page: 1, size: 100 } })
  if (result.code === 200) {
    allProblems.value = result.data.records || []
  }
}

async function loadExam() {
  const result = await request.get('/exam/' + route.params.id)
  if (result.code === 200) {
    const d = result.data
    title.value = d.title
    description.value = d.description || ''
    startTime.value = d.startTime || ''
    endTime.value = d.endTime || ''
    selectedIds.value = (d.problems || []).map(p => p.id)
  }
}

function toggle(id) {
  const i = selectedIds.value.indexOf(id)
  if (i >= 0) selectedIds.value.splice(i, 1)
  else selectedIds.value.push(id)
}

async function submit() {
  errorMsg.value = ''
  if (!title.value.trim()) { errorMsg.value = '考核名称不能为空'; return }
  if (!startTime.value || !endTime.value) { errorMsg.value = '请选择开始和结束时间'; return }
  saving.value = true
  const body = {
    title: title.value,
    description: description.value,
    // el-date-picker 用 value-format 输出完整的 "yyyy-MM-ddTHH:mm:ss"
    startTime: startTime.value,
    endTime: endTime.value,
    problemIds: selectedIds.value
  }
  const result = isEdit
    ? await request.put('/exam/' + route.params.id, body)
    : await request.post('/exam', body)
  saving.value = false
  if (result.code === 200) {
    ElMessage.success(isEdit ? '考核已更新' : '考核创建成功')
    router.push('/exams')
  } else {
    errorMsg.value = result.message || '保存失败'
  }
}

onMounted(async () => {
  await loadProblems()
  if (isEdit) {
    await loadExam()
  }
  loading.value = false
})
</script>

<template>
  <div class="page form-page">
    <div class="page-header">
      <h2 class="title">{{ isEdit ? '编辑考核' : '创建考核' }}</h2>
    </div>

    <el-card shadow="never" class="card">
      <el-form label-position="top">
        <el-form-item label="考核名称 *">
          <el-input v-model="title" placeholder="例如：算法期中测验" maxlength="200" />
        </el-form-item>

        <el-form-item label="说明">
          <el-input v-model="description" type="textarea" :rows="3" placeholder="考试范围、注意事项..." />
        </el-form-item>

        <div class="time-row">
          <el-form-item label="开始时间 *">
            <el-date-picker
              v-model="startTime"
              type="datetime"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DDTHH:mm:ss"
              placeholder="选择开始时间"
              class="full"
            />
          </el-form-item>
          <el-form-item label="结束时间 *">
            <el-date-picker
              v-model="endTime"
              type="datetime"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DDTHH:mm:ss"
              placeholder="选择结束时间"
              class="full"
            />
          </el-form-item>
        </div>

        <el-form-item label="选择题目（勾选顺序 = 题目顺序）">
          <p v-if="loading" class="state">加载中...</p>
          <div v-else class="check-list">
            <el-checkbox
              v-for="p in allProblems"
              :key="p.id"
              :model-value="selectedIds.includes(p.id)"
              class="check-item"
              @change="toggle(p.id)"
            >
              {{ p.title }}
            </el-checkbox>
          </div>
        </el-form-item>

        <el-alert v-if="errorMsg" type="error" :closable="false" show-icon :title="errorMsg" class="msg" />

        <div class="footer">
          <el-button @click="router.push('/exams')">取消</el-button>
          <el-button type="primary" :loading="saving" @click="submit">
            {{ isEdit ? '保存修改' : '创建考核' }}
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.card {
  border-radius: var(--radius);
}

.time-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.full {
  width: 100%;
}

.state {
  color: #909399;
  padding: 8px 0;
}

.check-list {
  max-height: 300px;
  overflow: auto;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.check-item {
  height: auto;
  padding: 6px 8px;
  margin: 0;
  border-radius: 6px;
}

.check-item:hover {
  background: #f5f7fa;
}

.msg {
  margin-bottom: 16px;
}

.footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>