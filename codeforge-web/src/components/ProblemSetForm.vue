<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../api/request'

const route = useRoute()
const router = useRouter()
const isEdit = !!route.params.id

const title = ref('')
const description = ref('')
const isPublic = ref(false)
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

async function loadDetail() {
  const result = await request.get('/problem-set/' + route.params.id)
  if (result.code === 200) {
    const d = result.data
    title.value = d.title
    description.value = d.description || ''
    isPublic.value = !!d.isPublic
    selectedIds.value = (d.problems || []).map(p => p.id)
  }
  loading.value = false
}

function toggle(id) {
  const i = selectedIds.value.indexOf(id)
  if (i >= 0) {
    selectedIds.value.splice(i, 1)   // 已在选中里 → 取消
  } else {
    selectedIds.value.push(id)       // 未选中 → 加入（顺序即题单顺序）
  }
}

async function submit() {
  errorMsg.value = ''
  if (!title.value.trim()) {
    errorMsg.value = '题单名称不能为空'
    return
  }
  saving.value = true
  const body = {
    title: title.value,
    description: description.value,
    isPublic: isPublic.value,
    problemIds: selectedIds.value
  }
  const result = isEdit
    ? await request.put('/problem-set/' + route.params.id, body)
    : await request.post('/problem-set', body)
  saving.value = false
  if (result.code === 200) {
    router.push('/problem-sets')
  } else {
    errorMsg.value = result.message || '保存失败'
  }
}

onMounted(async () => {
  await loadProblems()
  if (isEdit) {
    await loadDetail()
  } else {
    loading.value = false
  }
})
</script>

<template>
  <div class="page form-page">
    <div class="page-header">
      <h2 class="title">{{ isEdit ? '编辑题单' : '创建题单' }}</h2>
    </div>

    <el-card shadow="never" class="card">
      <el-form label-position="top">
        <el-form-item label="题单名称 *">
          <el-input v-model="title" placeholder="例如：动态规划入门" maxlength="200" />
        </el-form-item>

        <el-form-item label="简介">
          <el-input v-model="description" type="textarea" :rows="3" placeholder="这个题单是练什么的..." />
        </el-form-item>

        <el-form-item label="公开">
          <el-switch v-model="isPublic" active-text="公开" inactive-text="私有" />
        </el-form-item>

        <el-form-item label="选择题目（勾选顺序 = 题单顺序）">
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
          <el-button @click="router.push('/problem-sets')">取消</el-button>
          <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.card {
  border-radius: var(--radius);
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