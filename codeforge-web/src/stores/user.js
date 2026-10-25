import { defineStore } from 'pinia'

// 刷新页面后，从 localStorage 恢复上次登录留下的信息，避免状态丢失
function readInitialUser() {
  return {
    token: localStorage.getItem('token') || '',
    id: localStorage.getItem('userId') || '',
    username: localStorage.getItem('username') || '',
    role: localStorage.getItem('role') || '',
    avatar: localStorage.getItem('avatar') || ''
  }
}

// 这是前端全局唯一的「用户状态」来源：
// 登录后往这里写；其它任何组件要判断「是否登录 / 什么角色」都从 useUserStore() 读
export const useUserStore = defineStore('user', {
  state: () => readInitialUser(),

  getters: {
    // 是否已登录
    isLogin: (state) => !!state.token,
    // 是否「出题人及以上」（能进出题端）
    isAuthor: (state) => state.role === 'AUTHOR' || state.role === 'ADMIN',
    // 是否管理员（能进管理端）
    isAdmin: (state) => state.role === 'ADMIN'
  },

  actions: {
    // 登录成功后调用：把后端返回的 { token, user } 一次性写入（内存 + localStorage）
    setLoginInfo(data) {
      const user = data.user || {}
      this.token = data.token
      this.id = String(user.id ?? '')
      this.username = user.username || ''
      this.role = user.role || ''
      this.avatar = user.avatar || ''
      localStorage.setItem('token', this.token)
      localStorage.setItem('userId', this.id)
      localStorage.setItem('username', this.username)
      localStorage.setItem('role', this.role)
      localStorage.setItem('avatar', this.avatar)
    },

    // 头像上传成功后同步导航栏显示
    setAvatar(url) {
      this.avatar = url || ''
      localStorage.setItem('avatar', this.avatar)
    },

    // 登出 / token 失效时调用：清空内存与 localStorage
    logout() {
      this.token = ''
      this.id = ''
      this.username = ''
      this.role = ''
      this.avatar = ''
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      localStorage.removeItem('avatar')
    }
  }
})