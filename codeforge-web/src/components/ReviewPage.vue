<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/request'

const problems = ref([])
const loading = ref(true)
const errorMsg = ref('')

// 后端存英文枚举，前端展示中文 + Element Plus 标签类型
const difficultyMap = {
  EASY: { label: '简单', type: 'success' },
  MEDIUM: { label: '中等', type: 'warning' },
  HARD: { label: '困难', type: 'danger' }
}
function difficultyText(d) { return difficultyMap[d] ? difficultyMap[d].label : d }
function difficultyType(d) { return difficultyMap[d] ? difficultyMap[d].type : 'info' }

async function loadReviews() {
  loading.value = true
  errorMsg.value = ''
  try {
    // 只拉待审核状态的题目
    const result = await request.get('/problem', {
      params: { status: 'PENDING_REVIEW', page: 1, size: 100 }
    })
    if (result.code === 200) {
      problems.value = result.data.records || []
    } else {
      errorMsg.value = result.message || '加载待审核题目失败'
    }
  } catch (error) {
    errorMsg.value = '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 通过：PENDING_REVIEW -> PUBLISHED
async function approve(problem) {
  try {
    const result = await request.post('/problem/' + problem.id + '/review', {
      approved: true
    })
    if (result.code === 200) {
      ElMessage.success('已通过审核')
      await loadReviews()
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 驳回：PENDING_REVIEW -> REJECTED，原因必填
async function reject(problem) {
  let comment = ''
  try {
    const { value } = await ElMessageBox.prompt('请输入驳回原因', '驳回题目', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '例如：题面描述不清晰，缺少输入输出格式',
      inputValidator: (val) => (val && val.trim() ? true : '驳回原因不能为空')
    })
    comment = value.trim()
  } catch (e) {
    return // 用户取消
  }
  try {
    const result = await request.post('/problem/' + problem.id + '/review', {
      approved: false,
      comment
    })
    if (result.code === 200) {
      ElMessage.success('已驳回')
      await loadReviews()
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  }
}

onMounted(loadReviews)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2 class="title">待审核题目</h2>
    </div>

    <p v-if="loading" class="state">加载中...</p>
    <p v-else-if="errorMsg" class="error">{{ errorMsg }}</p>

    <el-card v-else shadow="never" class="card">
      <el-table :data="problems" stripe>
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="difficultyType(row.difficulty)" effect="light" size="small">
              {{ difficultyText(row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="approve(row)">通过</el-button>
            <el-button type="danger" size="small" @click="reject(row)">驳回</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无待审核题目" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
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

.card {
  border-radius: var(--radius);
}
</style>