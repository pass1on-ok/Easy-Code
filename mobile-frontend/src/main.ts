import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'

// Vant 按需引入样式在组件内引入，这里可引入全局样式
import 'vant/lib/index.css'

const pinia = createPinia()
const app = createApp(App)
app.use(pinia)
app.use(router)

// Ensure theme store is initialized so dark class is applied from storage
useThemeStore()

// Restore auth state from localStorage on every page load/refresh
useAuthStore().initialize()

app.mount('#app')
