import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService, type LoginCredentials, type SignupData } from '@/services/auth'
import type { User } from '@/types'

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
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Login failed'
      return false
    } finally {
      loading.value = false
    }
  }

  async function signup(data: SignupData) {
    loading.value = true
    error.value = null
    try {
      const response = await authService.signup(data)
      if (response.user) {
        user.value = response.user
      }
      return true
    } catch (err: any) {
      error.value = err.response?.data?.error || err.response?.data?.detail || 'Signup failed'
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
      console.error('Failed to fetch user:', err)
      // If token is invalid, clear auth state
      if (err.response?.status === 401) {
        authService.logout()
      }
      user.value = null
    }
  }

  async function initialize() {
    if (authService.isAuthenticated()) {
      await fetchCurrentUser()
    }
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
    initialize
  }
})
