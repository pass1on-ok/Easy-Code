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

export interface AuthResponse {
  access: string
  refresh: string
  user?: {
    id: number
    username: string
    email: string
  }
}

export const authService = {
  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    const response = await api.post('/api/token/', credentials)
    if (response.data.access) {
      localStorage.setItem('access_token', response.data.access)
      localStorage.setItem('refresh_token', response.data.refresh)
    }
    return response.data
  },

  async signup(data: SignupData) {
    const response = await api.post('/api/signup/', data)
    // Automatically login after signup
    if (response.data.access) {
      localStorage.setItem('access_token', response.data.access)
      localStorage.setItem('refresh_token', response.data.refresh)
    }
    return response.data
  },

  logout() {
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
  },

  isAuthenticated(): boolean {
    return !!localStorage.getItem('access_token')
  },

  async getCurrentUser() {
    const response = await api.get('/user/api/me/')
    return response.data
  }
}
