<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/request'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const set = ref(null)
const loading = ref(true)

// 只有创建者本人或管理员能编辑/删除
const canManage = computed(() => {
  if (!set.value) return false
  return userStore.isAdmin || String(userStore.id) === String(set.value.creatorId)
})

const difficultyMap = {
  EASY: { label: '简单', type: 'success' },
  MEDIUM: { label: '中等', type: 'warning' },
  HARD: { label: '困难', type: 'danger' }
}
function difficultyText(d) { return difficultyMap[d] ? difficultyMap[d].label : d }
function difficultyType(d) { return difficultyMap[d] ? difficultyMap[d].type : 'info' }

async function loadDetail() {
  loading.value = true
  const result = await request.get('/problem-set/' + route.params.id)
  if (result.code === 200) {
    set.value = result.data
  }
  loading.value = false
}

function goProblem(id) {
  router.push('/problem/' + id)
}
function goEdit() {
  router.push('/problem-set/' + route.params.id + '/edit')
}
async function doDelete() {
  try {
    await ElMessageBox.confirm('确定删除这个题单吗？删除后无法恢复。', '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return // 用户取消
  }
  const result = await request.delete('/problem-set/' + route.params.id)
  if (result.code === 200) {
    ElMessage.success('题单已删除')
    router.push('/problem-sets')
  } else {
    ElMessage.error(result.message || '删除失败')
  }
}
function goBack() {
  router.push('/problem-sets')
}

onMounted(loadDetail)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <el-button @click="goBack">返回题单列表</el-button>
      <div class="actions">
        <el-button v-if="canManage" type="warning" plain @click="goEdit">编辑</el-button>
        <el-button v-if="canManage" type="danger" plain @click="doDelete">删除</el-button>
      </div>
    </div>

    <el-card v-loading="loading" shadow="never" class="card">
      <template v-if="set">
        <div class="head">
          <h2 class="set-title">{{ set.title }}</h2>
          <el-tag :type="set.isPublic ? 'success' : 'info'" effect="light">
            {{ set.isPublic ? '公开' : '私有' }}
          </el-tag>
        </div>
        <p class="desc">{{ set.description || '（无简介）' }}</p>

        <el-divider content-position="left">题目（{{ set.problems.length }}）</el-divider>

        <el-empty v-if="set.problems.length === 0" description="这个题单还没有题目" />
        <el-table v-else :data="set.problems" stripe @row-click="(row) => goProblem(row.id)" class="table">
          <el-table-column prop="title" label="标题" min-width="240" />
          <el-table-column label="难度" width="120">
            <template #default="{ row }">
              <el-tag :type="difficultyType(row.difficulty)" effect="light" size="small">
                {{ difficultyText(row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </template>
      <el-empty v-else-if="!loading" description="题单不存在或无权访问" />
    </el-card>
  </div>
</template>

<style scoped>
.actions {
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

.set-title {
  margin: 0;
  color: #1e2a4a;
  font-size: 22px;
  font-weight: 700;
}

.desc {
  color: #606266;
  margin: 12px 0 8px;
}

.table {
  cursor: pointer;
}
</style>