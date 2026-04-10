<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-illustration">
        <svg width="120" height="120" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M22 10v6M2 10l10-5 10 5-10 5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M6 12v5c3 1.5 7 1.5 10 0v-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <h2>{{ t('auth.joinCommunity') }}</h2>
        <p>{{ t('auth.joinCommunityDescription') }}</p>
      </div>

      <div class="auth-card">
        <div class="auth-header">
          <h1>{{ t('auth.createAccount') }}</h1>
          <p>{{ t('auth.signupSubtitle') }}</p>
        </div>

        <form @submit.prevent="handleSubmit" class="auth-form">
          <div class="form-row">
            <div class="form-group">
              <label for="first_name">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                </svg>
                {{ t('auth.firstName') }}
              </label>
              <input
                id="first_name"
                v-model="formData.first_name"
                type="text"
                :placeholder="t('auth.firstNamePlaceholder')"
              />
            </div>

            <div class="form-group">
              <label for="last_name">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                </svg>
                {{ t('auth.lastName') }}
              </label>
              <input
                id="last_name"
                v-model="formData.last_name"
                type="text"
                :placeholder="t('auth.lastNamePlaceholder')"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="username">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M12 12a3 3 0 1 0 0-6 3 3 0 0 0 0 6zM7 20.662C7 18.623 9.239 17 12 17s5 1.623 5 3.662" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              {{ t('auth.username') }} *
            </label>
            <input
              id="username"
              v-model="formData.username"
              type="text"
              :placeholder="t('auth.usernamePlaceholder')"
              required
            />
          </div>

          <div class="form-group">
            <label for="email">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="currentColor" stroke-width="2"/>
                <path d="M22 6l-10 7L2 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              {{ t('auth.email') }} *
            </label>
            <input
              id="email"
              v-model="formData.email"
              type="email"
              :placeholder="t('auth.emailPlaceholder')"
              required
            />
          </div>

          <div class="form-group">
            <label for="password">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              {{ t('auth.password') }} *
            </label>
            <input
              id="password"
              v-model="formData.password"
              type="password"
              :placeholder="t('auth.passwordPlaceholder')"
              required
              minlength="6"
            />
          </div>

          <div class="form-group">
            <label for="password2">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              {{ t('auth.confirmPassword') }} *
            </label>
            <input
              id="password2"
              v-model="formData.password2"
              type="password"
              :placeholder="t('auth.confirmPasswordPlaceholder')"
              required
              minlength="6"
            />
          </div>

          <div v-if="error" class="error-message">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            {{ error }}
          </div>

          <div v-if="passwordError" class="error-message">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            {{ passwordError }}
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="loading || !!passwordError">
            <span v-if="loading" class="spinner-small"></span>
            <span v-else>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="8.5" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                <line x1="20" y1="8" x2="20" y2="14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <line x1="17" y1="11" x2="23" y2="11" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              {{ t('auth.createAccount') }}
            </span>
          </button>
        </form>

        <div class="auth-footer">
          <p>
            {{ t('auth.haveAccount') }}
            <RouterLink to="/login" class="link">{{ t('auth.signInLink') }}</RouterLink>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useLanguageStore } from '@/stores/language'

const router = useRouter()
const authStore = useAuthStore()
const languageStore = useLanguageStore()

const formData = reactive({
  username: '',
  email: '',
  password: '',
  password2: '',
  first_name: '',
  last_name: ''
})

const loading = computed(() => authStore.loading)
const error = computed(() => authStore.error)
const currentLanguage = computed(() => languageStore.currentLanguage)

// Make translation reactive by depending on currentLanguage
const t = (key: string) => {
  const _ = currentLanguage.value;
  return languageStore.t(key);
}

const passwordError = computed(() => {
  if (formData.password && formData.password2 && formData.password !== formData.password2) {
    return t('auth.passwordMismatch')
  }
  return null
})

const handleSubmit = async () => {
  if (passwordError.value) return

  const success = await authStore.signup(formData)
  if (success) {
    router.push('/login')
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
  max-width: 1100px;
  width: 100%;
  gap: 0;
  background: var(--color-card-bg);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: var(--color-card-shadow-hover);
}

.auth-card {
  padding: 3rem;
  max-height: 90vh;
  overflow-y: auto;
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
  gap: 1.25rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
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
  font-size: 0.95rem;
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
  border-top: 1px solid #e2e8f0;
}

.auth-footer p {
  color: #718096;
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
    max-width: 550px;
  }

  .auth-illustration {
    display: none;
  }

  .auth-card {
    padding: 2rem;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .auth-page {
    padding: 1rem;
  }
  
  .auth-card {
    padding: 1.5rem;
    max-height: none;
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
  
  .error-message {
    padding: 0.875rem;
    font-size: 0.875rem;
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
