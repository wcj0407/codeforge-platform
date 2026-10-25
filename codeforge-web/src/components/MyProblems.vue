<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()
const problems = ref([])
const loading = ref(true)
const errorMsg = ref('')
const activeStatus = ref('ALL')

// 题目状态：后端枚举 -> 中文 + Element Plus 标签类型
const statusMap = {
  DRAFT: { label: '草稿', type: 'info' },
  PENDING_REVIEW: { label: '待审核', type: 'warning' },
  PUBLISHED: { label: '已发布', type: 'success' },
  REJECTED: { label: '已驳回', type: 'danger' },
  OFFLINE: { label: '已下线', type: 'info' }
}

const statusTabs = [
  { value: 'ALL', label: '全部' },
  { value: 'DRAFT', label: '草稿' },
  { value: 'PENDING_REVIEW', label: '待审核' },
  { value: 'PUBLISHED', label: '已发布' },
  { value: 'REJECTED', label: '已驳回' },
  { value: 'OFFLINE', label: '已下线' }
]

function statusText(status) { return statusMap[status] ? statusMap[status].label : status }
function statusType(status) { return statusMap[status] ? statusMap[status].type : 'info' }

// 按当前选中的状态过滤
const filteredProblems = computed(() => {
  if (activeStatus.value === 'ALL') return problems.value
  return problems.value.filter(p => p.status === activeStatus.value)
})

async function loadMyProblems() {
  loading.value = true
  errorMsg.value = ''
  try {
    const result = await request.get('/problem/my', { params: { page: 1, size: 100 } })
    if (result.code === 200) {
      problems.value = result.data.records || []
    } else {
      errorMsg.value = result.message || '加载失败'
    }
  } catch (error) {
    errorMsg.value = '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push('/problem/' + id)
}
function goCreate() {
  router.push('/problem/create')
}

onMounted(loadMyProblems)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2 class="title">我的题目</h2>
      <el-button type="primary" @click="goCreate">创建题目</el-button>
    </div>

    <el-card shadow="never" class="card">
      <el-radio-group v-model="activeStatus" class="tabs">
        <el-radio-button
          v-for="tab in statusTabs"
          :key="tab.value"
          :value="tab.value"
        >{{ tab.label }}</el-radio-button>
      </el-radio-group>

      <p v-if="loading" class="state">加载中...</p>
      <p v-else-if="errorMsg" class="error">{{ errorMsg }}</p>

      <el-table
        v-else
        :data="filteredProblems"
        stripe
        @row-click="(row) => goDetail(row.id)"
        class="table"
      >
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ row.createTime || '-' }}</template>
        </el-table-column>
        <template #empty>
          <el-empty :description="filteredProblems.length === 0 ? '该状态下暂无题目' : '暂无题目'" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.card {
  border-radius: var(--radius);
}

.tabs {
  margin-bottom: 20px;
}

.state {
  color: #909399;
  text-align: center;
  padding: 40px;
}

.error {
  color: #f56c6c;
  text-align: center;
  padding: 40px;
}

.table {
  cursor: pointer;
}
</style>