import api from './api'

export interface LoginCredentials {
  username: string
  password: string
}

export interface SignupData {
  username: string
  email: string
  password: string
  password2: string
  first_name?: string
  last_name?: string
}

export const authService = {
  async login(credentials: LoginCredentials) {
    const { data } = await api.post('/api/token/', credentials)
    if (data.access) {
      localStorage.setItem('access_token', data.access)
      localStorage.setItem('refresh_token', data.refresh)
    }
    return data
  },

  async signup(payload: SignupData) {
    const { data } = await api.post('/api/signup/', payload)
    if (data.access) {
      localStorage.setItem('access_token', data.access)
      localStorage.setItem('refresh_token', data.refresh)
    }
    return data
  },

  logout() {
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
  },

  isAuthenticated(): boolean {
    return !!localStorage.getItem('access_token')
  },

  async refreshToken(): Promise<{ access: string; refresh?: string }> {
    const refresh = localStorage.getItem('refresh_token')
    if (!refresh) throw new Error('No refresh token')
    const { data } = await api.post<{ access: string; refresh?: string }>('/api/token/refresh/', { refresh })
    return data
  },

  async getCurrentUser() {
    const { data } = await api.get('/user/api/me/')
    return data
  },

  async updateProfile(payload: { first_name?: string; last_name?: string; email?: string; bio?: string; avatar?: string }) {
    const { data } = await api.patch('/user/api/me/', payload)
    return data
  },
}
