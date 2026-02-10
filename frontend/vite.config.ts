import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8000',
        changeOrigin: true,
      },
      '/media': {
        target: 'http://127.0.0.1:8000',
        changeOrigin: true,
      },
      '/course': {
        target: 'http://127.0.0.1:8000',
        changeOrigin: true,
      },
      '/check-out': {
        target: 'http://127.0.0.1:8000',
        changeOrigin: true,
      },
      '/purchased_courses': {
        target: 'http://127.0.0.1:8000',
        changeOrigin: true,
      },
      '/user': {
        target: 'http://127.0.0.1:8000',
        changeOrigin: true,
      },
    },
  },
})

