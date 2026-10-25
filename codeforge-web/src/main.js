import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 注册 Pinia：之后任何组件都能通过 useXxxStore() 读写全局状态
app.use(createPinia())
// 注册 Element Plus 组件库
app.use(ElementPlus, { locale: zhCn })
app.use(router)

app.mount('#app')