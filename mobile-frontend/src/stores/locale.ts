import { defineStore } from 'pinia'
import { ref } from 'vue'

export type LocaleCode = 'kz' | 'en' | 'py'

const STORAGE_KEY = 'easycode_locale'

function getStored(): LocaleCode {
  try {
    const v = localStorage.getItem(STORAGE_KEY) as LocaleCode | null
    if (v === 'kz' || v === 'en' || v === 'py') return v
  } catch {}
  return 'kz'
}

export const useLocaleStore = defineStore('locale', () => {
  const current = ref<LocaleCode>(getStored())

  function setLocale(code: LocaleCode) {
    current.value = code
    try {
      localStorage.setItem(STORAGE_KEY, code)
    } catch {}
  }

  return { current, setLocale }
})
