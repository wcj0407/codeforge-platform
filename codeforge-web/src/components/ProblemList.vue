<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()
const problems = ref([])
const loading = ref(true)
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 筛选条件
const keyword = ref('')
const difficulty = ref('')

const difficultyOptions = [
  { value: '', label: '全部难度' },
  { value: 'EASY', label: '简单' },
  { value: 'MEDIUM', label: '中等' },
  { value: 'HARD', label: '困难' }
]

// 难度：英文枚举 -> 中文 + Element Plus 标签类型
const difficultyMap = {
  EASY: { label: '简单', type: 'success' },
  MEDIUM: { label: '中等', type: 'warning' },
  HARD: { label: '困难', type: 'danger' }
}
function difficultyText(d) { return difficultyMap[d] ? difficultyMap[d].label : d }
function difficultyType(d) { return difficultyMap[d] ? difficultyMap[d].type : 'info' }

async function loadProblems() {
  loading.value = true
  const params = { page: page.value, size: size.value }
  const kw = keyword.value.trim()
  if (kw) params.keyword = kw
  if (difficulty.value) params.difficulty = difficulty.value

  const result = await request.get('/problem', { params })
  if (result.code === 200) {
    problems.value = result.data.records
    total.value = result.data.total
  }
  loading.value = false
}

// 搜索/筛选变化时回到第一页，避免停留在越界页码
function handleSearch() {
  page.value = 1
  loadProblems()
}

function handleReset() {
  keyword.value = ''
  difficulty.value = ''
  page.value = 1
  loadProblems()
}

function handleSizeChange() {
  page.value = 1
  loadProblems()
}

function goDetail(id) {
  router.push('/problem/' + id)
}

onMounted(() => {
  loadProblems()
})
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div class="head-left">
        <h2 class="title">题目列表</h2>
        <p class="subtitle">共 {{ total }} 道题</p>
      </div>

      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索题目标题"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-select
          v-model="difficulty"
          class="diff-select"
          placeholder="全部难度"
          @change="handleSearch"
        >
          <el-option
            v-for="opt in difficultyOptions"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <el-card class="list-card" shadow="never" v-loading="loading">
      <!-- 空状态 -->
      <el-empty
        v-if="!loading && problems.length === 0"
        :description="keyword.trim() || difficulty ? '没有符合条件的题目' : '暂无题目'"
      >
        <el-button v-if="keyword.trim() || difficulty" @click="handleReset">清除筛选</el-button>
      </el-empty>

      <!-- 列表表头 -->
      <div v-if="problems.length" class="list-head">
        <span class="idx">序号</span>
        <span class="head-main">题目</span>
        <span class="head-diff">难度</span>
      </div>

      <!-- 题目列表 -->
      <ul v-if="problems.length" class="problem-list">
        <li
          v-for="(p, index) in problems"
          :key="p.id"
          class="problem-item"
          @click="goDetail(p.id)"
        >
          <span class="idx">{{ (page - 1) * size + index + 1 }}</span>
          <div class="p-main">
            <span class="p-title">{{ p.title }}</span>
            <div v-if="p.tags && p.tags.length" class="p-tags">
              <el-tag
                v-for="t in p.tags.slice(0, 2)"
                :key="t"
                type="info"
                effect="plain"
                size="small"
              >{{ t }}</el-tag>
            </div>
          </div>
          <el-tag :type="difficultyType(p.difficulty)" effect="light" size="small">
            {{ difficultyText(p.difficulty) }}
          </el-tag>
        </li>
      </ul>

      <!-- 分页 -->
      <div v-if="total > 0" class="pager">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="prev, pager, next, sizes, total"
          background
          @current-change="loadProblems"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.search-input {
  width: 280px;
}

.diff-select {
  width: 140px;
}

.list-card {
  border-radius: 16px;
}

.list-card :deep(.el-card__body) {
  padding: 8px 10px 12px;
}

.list-head {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 10px 20px 10px;
  margin-bottom: 2px;
  border-bottom: 1px solid #eef1f6;
  color: #90a0b9;
  font-size: 12px;
  letter-spacing: 0.4px;
}

.list-head .idx {
  text-align: right;
}

.head-main {
  flex: 1;
}

.head-diff {
  margin-right: 2px;
}

.problem-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.problem-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 16px 20px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s, transform 0.2s;
}

.problem-item:nth-child(even) {
  background: #fafbfd;
}

.problem-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 22%;
  height: 56%;
  width: 3px;
  border-radius: 2px;
  background: transparent;
  transition: background 0.2s;
}

.problem-item + .problem-item {
  border-top: 1px solid #f0f2f7;
}

.problem-item:hover {
  background: #f5f9f8;
  transform: translateX(2px);
}

.problem-item:hover::before {
  background: #2a9d8f;
}

.idx {
  width: 48px;
  flex-shrink: 0;
  text-align: right;
  color: #b6bec9;
  font-size: 15px;
  font-family: 'Consolas', 'Courier New', monospace;
}

.p-main {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.p-title {
  font-size: 16px;
  color: #1e2a4a;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.p-tags {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media (max-width: 720px) {
  .page-header {
    align-items: flex-start;
  }

  .search-input {
    width: 100%;
  }

  .diff-select {
    width: 100%;
  }

  .p-tags {
    display: none;
  }
}
</style>