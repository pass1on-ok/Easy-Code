import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

const STORAGE_KEY = 'easycode_theme'

function getStored(): boolean {
  try {
    const v = localStorage.getItem(STORAGE_KEY)
    if (v === 'dark') return true
    if (v === 'light') return false
  } catch {}
  return false
}

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(getStored())

  function toggle() {
    isDark.value = !isDark.value
    try {
      localStorage.setItem(STORAGE_KEY, isDark.value ? 'dark' : 'light')
    } catch {}
  }

  watch(
    isDark,
    (v) => {
      document.documentElement.classList.toggle('theme-dark', v)
    },
    { immediate: true }
  )

  return { isDark, toggle }
})
