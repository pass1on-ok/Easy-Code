import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService, type LoginCredentials, type SignupData } from '@/services/auth'
import type { User } from '@/types'

const NETWORK_ERROR_MSG = 'Серверге қосылу мүмкін емес. Интернет байланысын тексеріңіз немесе кейін қайталаңыз.'

function getErrorMessage(err: any, fallback: string): string {
  if (!err.response && (err.code === 'ERR_NETWORK' || err.message === 'Network Error')) {
    return NETWORK_ERROR_MSG
  }
  const d = err.response?.data
  // Spring GlobalExceptionHandler: { code, message, data }
  return d?.detail || d?.message || d?.error || fallback
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!user.value)

  async function login(credentials: LoginCredentials) {
    loading.value = true
    error.value = null
    try {
      await authService.login(credentials)
      await fetchCurrentUser()
      return true
    } catch (err: any) {
      error.value = getErrorMessage(err, 'Кіру сәтсіз аяқталды')
      return false
    } finally {
      loading.value = false
    }
  }

  async function signup(data: SignupData) {
    loading.value = true
    error.value = null
    try {
      await authService.signup(data)
      await fetchCurrentUser()
      return true
    } catch (err: any) {
      error.value = getErrorMessage(err, 'Тіркелу сәтсіз аяқталды')
      return false
    } finally {
      loading.value = false
    }
  }

  function logout() {
    authService.logout()
    user.value = null
  }

  async function fetchCurrentUser() {
    if (!authService.isAuthenticated()) {
      user.value = null
      return
    }
    try {
      user.value = await authService.getCurrentUser()
    } catch (err: any) {
      if (err.response?.status === 401) authService.logout()
      user.value = null
    }
  }

  function isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      // Refresh if token expires within the next 60 seconds
      return payload.exp * 1000 < Date.now() + 60_000
    } catch {
      return true
    }
  }

  /** Only refresh when the access token is missing or about to expire */
  async function ensureValidToken(): Promise<boolean> {
    const access = localStorage.getItem('access_token')
    const refresh = localStorage.getItem('refresh_token')

    if (!refresh) return !!access

    // Access token still valid — no refresh needed
    if (access && !isTokenExpired(access)) return true

    try {
      const data = await authService.refreshToken()
      if (data?.access) {
        localStorage.setItem('access_token', data.access)
        if (data.refresh) localStorage.setItem('refresh_token', data.refresh)
        return true
      }
    } catch {
      authService.logout()
      return false
    }
    return !!localStorage.getItem('access_token')
  }

  async function initialize() {
    if (!authService.isAuthenticated()) return
    const valid = await ensureValidToken()
    if (valid) await fetchCurrentUser()
  }

  return {
    user,
    loading,
    error,
    isAuthenticated,
    login,
    signup,
    logout,
    fetchCurrentUser,
    initialize,
  }
})
