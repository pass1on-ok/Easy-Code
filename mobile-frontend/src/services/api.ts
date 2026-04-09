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

// Mutex: ensures only one token refresh runs at a time across concurrent 401s
let refreshPromise: Promise<string | null> | null = null

api.interceptors.response.use(
  (res) => res,
  async (err) => {
    const orig = err.config
    if (err.response?.status === 401 && !orig._retry) {
      orig._retry = true
      const refresh = localStorage.getItem('refresh_token')
      if (!refresh) {
        window.location.href = `/login?session_expired=1`
        return Promise.reject(err)
      }

      // If a refresh is already in flight, wait for it instead of starting a new one
      if (!refreshPromise) {
        refreshPromise = api
          .post('/api/token/refresh/', { refresh })
          .then(({ data }) => {
            localStorage.setItem('access_token', data.access)
            if (data.refresh) localStorage.setItem('refresh_token', data.refresh)
            return data.access as string
          })
          .catch(() => {
            localStorage.removeItem('access_token')
            localStorage.removeItem('refresh_token')
            const redirect = encodeURIComponent(window.location.pathname + window.location.search)
            window.location.href = `/login?session_expired=1&redirect=${redirect}`
            return null
          })
          .finally(() => {
            refreshPromise = null
          })
      }

      const newToken = await refreshPromise
      if (!newToken) return Promise.reject(err)
      orig.headers.Authorization = `Bearer ${newToken}`
      return api(orig)
    }
    return Promise.reject(err)
  }
)

export default api
