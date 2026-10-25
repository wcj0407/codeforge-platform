<script setup>
import { ref } from 'vue'
import request from '../api/request'

const props = defineProps({
  problemId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['submit-success'])

// Java 判题约定：类名必须是 Solution，入口是 main，从标准输入读、用标准输出写
const JAVA_TEMPLATE = `import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

    }
}
`

const code = ref(JAVA_TEMPLATE)
const language = ref('JAVA')
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const languages = [
  { value: 'JAVA', label: 'Java' }
]

async function submitCode() {
  errorMsg.value = ''
  successMsg.value = ''

  if (!code.value.trim()) {
    errorMsg.value = '代码不能为空'
    return
  }

  submitting.value = true

  try {
    const result = await request.post('/submission', {
      problemId: props.problemId,
      code: code.value,
      language: language.value
    })

    if (result.code === 200) {
      successMsg.value = `提交成功！提交ID: ${result.data.id}`
      emit('submit-success', result.data)

      // 3秒后清空成功消息
      setTimeout(() => {
        successMsg.value = ''
      }, 3000)
    } else {
      errorMsg.value = result.message || '提交失败'
    }
  } catch (error) {
    errorMsg.value = '提交失败，请稍后重试'
  } finally {
    submitting.value = false
  }
}

// 清空 = 恢复模板骨架，只清掉用户敲的代码
function clearCode() {
  code.value = JAVA_TEMPLATE
  errorMsg.value = ''
  successMsg.value = ''
}
</script>

<template>
  <el-card shadow="never" class="editor-card">
    <div class="editor-header">
      <h3>代码提交</h3>
      <div class="controls">
        <el-select v-model="language" class="lang-select">
          <el-option
            v-for="lang in languages"
            :key="lang.value"
            :label="lang.label"
            :value="lang.value"
          />
        </el-select>
        <el-button @click="clearCode">清空</el-button>
      </div>
    </div>

    <textarea
      v-model="code"
      class="code-textarea"
      spellcheck="false"
      :disabled="submitting"
    ></textarea>

    <div class="editor-footer">
      <div class="msg">
        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
        <p v-if="successMsg" class="success">{{ successMsg }}</p>
      </div>
      <el-button
        type="primary"
        :loading="submitting"
        @click="submitCode"
      >{{ submitting ? '提交中...' : '提交代码' }}</el-button>
    </div>
  </el-card>
</template>

<style scoped>
.editor-card {
  margin-top: 24px;
  border-radius: var(--radius);
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.editor-header h3 {
  margin: 0;
  font-size: 16px;
  color: #1e2a4a;
}

.controls {
  display: flex;
  gap: 8px;
  align-items: center;
}

.lang-select {
  width: 140px;
}

.code-textarea {
  width: 100%;
  min-height: 320px;
  padding: 16px;
  border: 1px solid #e6e9f0;
  border-radius: 8px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.7;
  resize: vertical;
  box-sizing: border-box;
  background: #fbfcfe;
  color: #1e2a4a;
}

.code-textarea:focus {
  outline: none;
  border-color: #2a9d8f;
  background: #fff;
}

.code-textarea:disabled {
  background: #f5f7fa;
  cursor: not-allowed;
}

.editor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 14px;
}

.msg {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.error {
  color: #f56c6c;
  font-size: 14px;
  margin: 0;
}

.success {
  color: #2a9d8f;
  font-size: 14px;
  margin: 0;
  font-weight: 500;
}
</style>