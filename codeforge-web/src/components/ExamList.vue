<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const exams = ref([])
const loading = ref(true)

const statusMap = {
  NOT_STARTED: { label: '未开始', type: 'info' },
  RUNNING: { label: '进行中', type: 'success' },
  ENDED: { label: '已结束', type: 'info' }
}
function statusText(s) { return statusMap[s] ? statusMap[s].label : s }
function statusType(s) { return statusMap[s] ? statusMap[s].type : 'info' }

async function loadExams() {
  loading.value = true
  const result = await request.get('/exam', { params: { page: 1, size: 50 } })
  if (result.code === 200) {
    exams.value = result.data.records || []
  }
  loading.value = false
}

function goDetail(id) { router.push('/exam/' + id) }
function goCreate() { router.push('/exam/create') }

onMounted(loadExams)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2 class="title">考核</h2>
      <el-button v-if="userStore.isAdmin" type="primary" @click="goCreate">创建考核</el-button>
    </div>

    <div v-loading="loading">
      <el-empty v-if="!loading && exams.length === 0" description="暂无考核" />
      <div v-else class="cards">
        <el-card
          v-for="e in exams"
          :key="e.id"
          shadow="hover"
          class="card"
          @click="goDetail(e.id)"
        >
          <div class="card-top">
            <span class="exam-title">{{ e.title }}</span>
            <el-tag :type="statusType(e.status)" effect="light" size="small">
              {{ statusText(e.status) }}
            </el-tag>
          </div>
          <p class="desc">{{ e.description || '（无说明）' }}</p>
          <div class="meta">
            <span>{{ e.problemCount }} 道题</span>
            <span>{{ (e.startTime || '').replace('T', ' ') }} ~ {{ (e.endTime || '').replace('T', ' ') }}</span>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 16px;
}

.card {
  border-radius: var(--radius);
  cursor: pointer;
}

.card-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.exam-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e2a4a;
}

.desc {
  color: #606266;
  font-size: 14px;
  margin: 0 0 8px;
}

.meta {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 13px;
}
</style>