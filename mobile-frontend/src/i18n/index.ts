import { computed } from 'vue'
import { useLocaleStore } from '@/stores/locale'
import { messages } from './messages'
import type { LocaleCode } from './messages'

export { messages }
export type { LocaleCode }

/**
 * 根据当前语言返回文案；切换语言后界面自动更新
 */
export function useI18n() {
  const localeStore = useLocaleStore()

  const locale = computed(() => localeStore.current)

  function t(key: string): string {
    const localeMessages = messages[localeStore.current]
    const value = localeMessages[key]
    if (value !== undefined) return value
    // fallback to kz then en
    return messages.kz[key] ?? messages.en[key] ?? key
  }

  return { t, locale }
}
