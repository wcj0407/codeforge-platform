<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/request'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const exam = ref(null)
const rank = ref([])
const loading = ref(true)

const statusMap = {
  NOT_STARTED: { label: '未开始', type: 'info' },
  RUNNING: { label: '进行中', type: 'success' },
  ENDED: { label: '已结束', type: 'info' }
}
function statusText(s) { return statusMap[s] ? statusMap[s].label : s }
function statusType(s) { return statusMap[s] ? statusMap[s].type : 'info' }

const difficultyMap = {
  EASY: { label: '简单', type: 'success' },
  MEDIUM: { label: '中等', type: 'warning' },
  HARD: { label: '困难', type: 'danger' }
}
function difficultyText(d) { return difficultyMap[d] ? difficultyMap[d].label : d }
function difficultyType(d) { return difficultyMap[d] ? difficultyMap[d].type : 'info' }

function isSolved(problemId) {
  return exam.value && exam.value.solvedProblemIds && exam.value.solvedProblemIds.includes(problemId)
}

async function loadDetail() {
  loading.value = true
  const result = await request.get('/exam/' + route.params.id)
  if (result.code === 200) {
    exam.value = result.data
  }
  loading.value = false
}

async function loadRank() {
  const result = await request.get('/exam/' + route.params.id + '/rank')
  if (result.code === 200) {
    rank.value = result.data || []
  }
}

function goProblem(id) {
  router.push('/problem/' + id)
}
function goBack() {
  router.push('/exams')
}
function goEdit() {
  router.push('/exam/' + route.params.id + '/edit')
}
async function doDelete() {
  try {
    await ElMessageBox.confirm('确定删除这场考核吗？删除后无法恢复。', '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return // 用户取消
  }
  const result = await request.delete('/exam/' + route.params.id)
  if (result.code === 200) {
    ElMessage.success('考核已删除')
    router.push('/exams')
  } else {
    ElMessage.error(result.message || '删除失败')
  }
}

onMounted(() => {
  loadDetail()
  loadRank()
})
</script>

<template>
  <div class="page">
    <div class="page-header">
      <el-button @click="goBack">返回考核列表</el-button>
      <div v-if="userStore.isAdmin" class="header-actions">
        <el-button type="warning" plain @click="goEdit">编辑考核</el-button>
        <el-button type="danger" plain @click="doDelete">删除考核</el-button>
      </div>
    </div>

    <el-card v-loading="loading" shadow="never" class="card">
      <template v-if="exam">
        <div class="head">
          <h2 class="exam-title">{{ exam.title }}</h2>
          <el-tag :type="statusType(exam.status)" effect="light">
            {{ statusText(exam.status) }}
          </el-tag>
        </div>
        <p class="desc">{{ exam.description || '（无说明）' }}</p>
        <p class="time">{{ (exam.startTime || '').replace('T', ' ') }} ~ {{ (exam.endTime || '').replace('T', ' ') }}</p>

        <el-divider content-position="left">题目（{{ exam.problems.length }}）</el-divider>

        <el-empty v-if="exam.problems.length === 0" description="这场考核还没有题目" />
        <el-table v-else :data="exam.problems" stripe @row-click="(row) => goProblem(row.id)" class="table">
          <el-table-column prop="title" label="标题" min-width="220" />
          <el-table-column label="难度" width="120">
            <template #default="{ row }">
              <el-tag :type="difficultyType(row.difficulty)" effect="light" size="small">
                {{ difficultyText(row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="isSolved(row.id)" type="success" effect="plain" size="small">已通过</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <el-divider content-position="left">排名</el-divider>

        <el-empty v-if="rank.length === 0" description="暂无成绩" />
        <el-table v-else :data="rank" stripe>
          <el-table-column prop="rank" label="名次" width="100" />
          <el-table-column prop="username" label="用户" min-width="180" />
          <el-table-column prop="solvedCount" label="通过题数" width="120" />
        </el-table>
      </template>
      <el-empty v-else-if="!loading" description="考核不存在" />
    </el-card>
  </div>
</template>

<style scoped>
.header-actions {
  display: flex;
  gap: 8px;
}

.card {
  border-radius: var(--radius);
}

.head {
  display: flex;
  align-items: center;
  gap: 10px;
}

.exam-title {
  margin: 0;
  color: #1e2a4a;
  font-size: 22px;
  font-weight: 700;
}

.desc {
  color: #606266;
  margin: 12px 0 4px;
}

.time {
  color: #909399;
  font-size: 14px;
}

.table {
  cursor: pointer;
}
</style>