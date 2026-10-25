import axios from 'axios'

// 1. 创建一个"定制版"的 axios 实例：统一前缀、统一超时
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 2. 请求拦截器（门卫A）：每次发请求前，自动给请求贴上 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

// 3. 响应拦截器（门卫B）：统一把结果剥一层（返回 res.data），顺便处理"未登录"
request.interceptors.response.use(
  response => response.data,
  error => {
    const code = error.response && error.response.data && error.response.data.code
    if (code === 40100) {
      // token 失效：清干净本地登录信息，避免导航栏残留旧用户名/角色
      const authKeys = ['token', 'userId', 'username', 'role', 'avatar']
      authKeys.forEach(key => localStorage.removeItem(key))
      window.location.reload()
    }
    return Promise.reject(error)
  }
)

export default request