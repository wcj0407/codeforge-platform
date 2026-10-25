<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from './stores/user'

// 全局导航栏：每个页面抬头都能看到这一排「公共招牌」
// 用 userStore 的角色判断，决定不同角色能看到哪些入口
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const initial = computed(() => (userStore.username || 'U').charAt(0).toUpperCase())

// 导航项根据角色动态拼装，模板里只负责渲染
const navLinks = computed(() => {
  const links = [
    { to: '/problems', label: '题目列表' },
    { to: '/problem-sets', label: '题单' },
    { to: '/exams', label: '考核' },
    { to: '/submissions', label: '我的代码提交' },
    { to: '/profile', label: '个人中心' }
  ]
  if (userStore.isAuthor) {
    links.push({ to: '/problem/create', label: '出题' })
    links.push({ to: '/my-problems', label: '我的题目' })
  }
  if (userStore.isAdmin) {
    links.push({ to: '/review', label: '审核' })
    links.push({ to: '/admin/users', label: '用户管理' })
  }
  return links
})

function handleCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<template>
  <div class="app">
    <header
      v-if="userStore.isLogin && !route.meta.hideNav"
      class="navbar"
    >
      <div class="brand" @click="router.push('/problems')">
        <span class="logo-mark">&lt;/&gt;</span>
        <span class="brand-name">CodeForge</span>
      </div>

      <nav class="links">
        <router-link
          v-for="link in navLinks"
          :key="link.to"
          :to="link.to"
        >{{ link.label }}</router-link>
      </nav>

      <div class="right">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-trigger">
            <el-avatar :size="30" class="user-avatar" :src="userStore.avatar || undefined">{{ initial }}</el-avatar>
            <span class="username">{{ userStore.username }}</span>
            <span class="caret"></span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    <router-view v-slot="{ Component }">
      <transition name="page-fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 28px;
  height: 64px;
  padding: 0 28px;
  box-sizing: border-box;
  background: linear-gradient(120deg, #2b3a67 0%, #1e2a4a 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: 0 4px 20px rgba(30, 42, 74, 0.18);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
  cursor: pointer;
  user-select: none;
}

.logo-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.18);
  color: #6fd6c8;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.brand-name {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.4px;
}

.links {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  min-width: 0;
  overflow-x: auto;
  scrollbar-width: none;
}

.links::-webkit-scrollbar {
  display: none;
}

.links a {
  position: relative;
  padding: 8px 14px;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.72);
  font-size: 14px;
  text-decoration: none;
  white-space: nowrap;
  transition: background 0.2s, color 0.2s;
}

.links a:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.links a.router-link-active {
  color: #fff;
  font-weight: 500;
  background: rgba(42, 157, 143, 0.3);
}

.right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  border-radius: 999px;
  cursor: pointer;
  transition: background 0.2s;
  outline: none;
}

.user-trigger:hover {
  background: rgba(255, 255, 255, 0.08);
}

.user-avatar {
  background: linear-gradient(135deg, #2a9d8f 0%, #24887b 100%);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.username {
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
}

.caret {
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 5px solid rgba(255, 255, 255, 0.6);
  transition: transform 0.2s;
}

.user-trigger:hover .caret {
  border-top-color: #fff;
}

@media (max-width: 900px) {
  .navbar {
    gap: 16px;
    padding: 0 16px;
  }

  .brand-name {
    display: none;
  }

  .username {
    max-width: 90px;
  }
}
</style>