import './assets/main.css'
import './assets/theme.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useThemeStore } from './stores/theme'
import { useLanguageStore } from './stores/language'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)
app.use(router)

// Initialize theme and language
const themeStore = useThemeStore()
const languageStore = useLanguageStore()
themeStore.initTheme()
languageStore.initLanguage()

app.mount('#app')
