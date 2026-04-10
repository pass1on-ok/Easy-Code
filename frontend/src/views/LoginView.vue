<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <h1>{{ t('auth.welcomeBack') }}</h1>
          <p>{{ t('auth.loginSubtitle') }}</p>
        </div>

        <form @submit.prevent="handleSubmit" class="auth-form">
          <div class="form-group">
            <label for="username">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
              </svg>
              {{ t('auth.username') }}
            </label>
            <input
              id="username"
              v-model="credentials.username"
              type="text"
              :placeholder="t('auth.usernamePlaceholder')"
              required
            />
          </div>

          <div class="form-group">
            <label for="password">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              {{ t('auth.password') }}
            </label>
            <input
              id="password"
              v-model="credentials.password"
              type="password"
              :placeholder="t('auth.passwordPlaceholder')"
              required
            />
          </div>

          <div v-if="error" class="error-message">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            {{ error }}
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
            <span v-if="loading" class="spinner-small"></span>
            <span v-else>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M10 17l5-5-5-5M15 12H3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              {{ t('auth.signIn') }}
            </span>
          </button>
        </form>

        <div class="auth-footer">
          <p>
            {{ t('auth.noAccount') }}
            <RouterLink to="/signup" class="link">{{ t('auth.signUpLink') }}</RouterLink>
          </p>
        </div>
      </div>

      <div class="auth-illustration">
        <svg width="120" height="120" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect x="2" y="3" width="20" height="14" rx="2" stroke="currentColor" stroke-width="2"/>
          <path d="M8 21h8M12 17v4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <path d="M7 8l3 3-3 3M11 11h4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <h2>{{ t('auth.startLearning') }}</h2>
        <p>{{ t('auth.startLearningDescription') }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useLanguageStore } from '@/stores/language'

const router = useRouter()
const authStore = useAuthStore()
const languageStore = useLanguageStore()

const credentials = reactive({
  username: '',
  password: ''
})

const loading = computed(() => authStore.loading)
const error = computed(() => authStore.error)
const currentLanguage = computed(() => languageStore.currentLanguage)

// Make translation reactive by depending on currentLanguage
const t = (key: string) => {
  const _ = currentLanguage.value;
  return languageStore.t(key);
}

const handleSubmit = async () => {
  const success = await authStore.login(credentials)
  if (success) {
    router.push('/')
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.auth-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-width: 1000px;
  width: 100%;
  gap: 0;
  background: var(--color-card-bg);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: var(--color-card-shadow-hover);
}

.auth-card {
  padding: 3rem;
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-header h1 {
  font-size: 2rem;
  font-weight: 800;
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
}

.auth-header p {
  color: var(--color-text-secondary);
  font-size: 1rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: var(--color-text-primary);
  font-size: 0.95rem;
}

.form-group input {
  padding: 0.875rem 1rem;
  border: 2px solid var(--color-border);
  border-radius: 10px;
  font-size: 1rem;
  transition: all 0.3s ease;
  outline: none;
  background: var(--color-input-bg);
  color: var(--color-text-primary);
}

.form-group input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.error-message {
  background: var(--color-bg-secondary);
  border: 2px solid var(--color-error);
  color: var(--color-error);
  padding: 1rem;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.btn {
  padding: 1rem 2rem;
  border-radius: 10px;
  font-weight: 600;
  font-size: 1rem;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-block {
  width: 100%;
}

.spinner-small {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.auth-footer {
  text-align: center;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid var(--color-border);
}

.auth-footer p {
  color: var(--color-text-secondary);
}

.link {
  color: #667eea;
  font-weight: 600;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

.auth-illustration {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 3rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
}

.auth-illustration i {
  font-size: 6rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.auth-illustration h2 {
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 1rem;
}

.auth-illustration p {
  font-size: 1.125rem;
  opacity: 0.9;
  max-width: 300px;
}

@media (max-width: 968px) {
  .auth-container {
    grid-template-columns: 1fr;
    max-width: 500px;
  }

  .auth-illustration {
    display: none;
  }

  .auth-card {
    padding: 2rem;
  }
}

@media (max-width: 640px) {
  .auth-page {
    padding: 1rem;
  }
  
  .auth-card {
    padding: 1.5rem;
    border-radius: 16px;
  }
  
  .auth-header h1 {
    font-size: 1.75rem;
  }
  
  .auth-header p {
    font-size: 0.9375rem;
  }
  
  .form-group label {
    font-size: 0.875rem;
  }
  
  .form-group input {
    padding: 0.75rem 0.875rem;
    font-size: 0.9375rem;
  }
  
  .btn {
    padding: 0.875rem 1.5rem;
    font-size: 0.9375rem;
  }
  
  .auth-footer {
    margin-top: 1.5rem;
    padding-top: 1.5rem;
  }
}

@media (max-width: 380px) {
  .auth-card {
    padding: 1.25rem;
  }
  
  .auth-header h1 {
    font-size: 1.5rem;
  }
}
</style>
