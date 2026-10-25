<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()
const sets = ref([])
const loading = ref(true)

async function loadSets() {
  loading.value = true
  const result = await request.get('/problem-set', { params: { page: 1, size: 50 } })
  if (result.code === 200) {
    sets.value = result.data.records || []
  }
  loading.value = false
}

function goDetail(id) {
  router.push('/problem-set/' + id)
}
function goCreate() {
  router.push('/problem-set/create')
}

onMounted(loadSets)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2 class="title">题单</h2>
      <el-button type="primary" @click="goCreate">创建题单</el-button>
    </div>

    <div v-loading="loading">
      <el-empty v-if="!loading && sets.length === 0" description="暂无题单" />
      <div v-else class="cards">
        <el-card
          v-for="s in sets"
          :key="s.id"
          shadow="hover"
          class="card"
          @click="goDetail(s.id)"
        >
          <div class="card-top">
            <span class="set-title">{{ s.title }}</span>
            <el-tag :type="s.isPublic ? 'success' : 'info'" effect="light" size="small">
              {{ s.isPublic ? '公开' : '私有' }}
            </el-tag>
          </div>
          <p class="desc">{{ s.description || '（无简介）' }}</p>
          <div class="meta">
            <span>{{ s.problemCount }} 道题</span>
            <span class="time">{{ s.createTime || '' }}</span>
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

.set-title {
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

.time {
  color: #c0c4cc;
}
</style>