import axios from 'axios'

// mobile-backend (Spring Boot) 默认 8080；可用 .env.development 中 VITE_API_BASE_URL 覆盖
const API_BASE =
  import.meta.env.VITE_API_BASE_URL ||
  (import.meta.env.DEV ? 'http://127.0.0.1:8080' : '')

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
  // JWT 走 Authorization，无需 Cookie；与后端 CORS 配置一致时可关 withCredentials
  withCredentials: false,
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token')
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  },
  (e) => Promise.reject(e)
)

api.interceptors.response.use(
  (res) => res,
  async (err) => {
    const orig = err.config
    if (err.response?.status === 401 && !orig._retry) {
      orig._retry = true
      try {
        const refresh = localStorage.getItem('refresh_token')
        const { data } = await api.post('/api/token/refresh/', { refresh })
        localStorage.setItem('access_token', data.access)
        if (data.refresh) localStorage.setItem('refresh_token', data.refresh)
        orig.headers.Authorization = `Bearer ${data.access}`
        return api(orig)
      } catch {
        localStorage.removeItem('access_token')
        localStorage.removeItem('refresh_token')
        const redirect = encodeURIComponent(window.location.pathname + window.location.search)
        window.location.href = `/login?session_expired=1&redirect=${redirect}`
        return Promise.reject(err)
      }
    }
    return Promise.reject(err)
  }
)

export default api
