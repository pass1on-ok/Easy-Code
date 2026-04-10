<template>
  <div class="payment-page">
    <div class="container">
      <div class="payment-card success">
        <div class="icon-wrapper">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        
        <h1>Payment Successful!</h1>
        <p class="message">Thank you for your purchase. You now have access to the course.</p>
        
        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <p>Confirming your payment...</p>
        </div>
        
        <div v-else-if="course" class="course-info">
          <h2>{{ localizedCourse.name }}</h2>
          <p>Your enrollment has been confirmed.</p>
        </div>
        
        <div v-if="error" class="error-message">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          {{ error }}
        </div>
        
        <div class="actions">
          <RouterLink to="/my-courses" class="btn btn-primary">
            View My Courses
          </RouterLink>
          <RouterLink to="/" class="btn btn-outline">
            Back to Home
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, RouterLink, useRouter } from 'vue-router'
import { paymentService } from '@/services/payment'
import { useAuthStore } from '@/stores/auth'
import { useLanguageStore } from '@/stores/language'
import { useCoursesStore } from '@/stores/courses'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const coursesStore = useCoursesStore()
const languageStore = useLanguageStore()

const loading = ref(true)
const error = ref<string | null>(null)
const course = ref<any>(null)

const localizedCourse = computed(() => {
  if (!course.value) {
    return { name: '' }
  }

  const slug = course.value.slug || ''
  return {
    name: languageStore.tCourse(`course.${slug}.title`, course.value.name)
  }
})

onMounted(async () => {
  // Initialize auth if user has token
  if (!authStore.user && authStore.isAuthenticated) {
    await authStore.initialize()
  }
  
  // Check if user is authenticated
  if (!authStore.isAuthenticated) {
    error.value = 'Please login to complete your purchase'
    loading.value = false
    setTimeout(() => {
      router.push('/login')
    }, 3000)
    return
  }
  
  const sessionId = route.query.session_id as string
  const courseId = route.query.course_id as string
  
  if (!sessionId || !courseId) {
    error.value = 'Invalid payment information'
    loading.value = false
    return
  }
  
  try {
    const response = await paymentService.confirmPayment(sessionId, parseInt(courseId))
    course.value = response.course
    // Refresh purchased courses
    await coursesStore.fetchPurchasedCourses()
  } catch (err: any) {
    error.value = err.response?.data?.error || 'Failed to confirm payment'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.payment-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.container {
  width: 100%;
  max-width: 600px;
}

.payment-card {
  background: white;
  border-radius: 24px;
  padding: 4rem 3rem;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.payment-card.success {
  border-top: 6px solid #10b981;
}

.icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 50%;
  margin-bottom: 2rem;
}

.icon-wrapper svg {
  stroke: white;
  width: 64px;
  height: 64px;
}

h1 {
  font-size: 2rem;
  color: #1a202c;
  margin-bottom: 1rem;
}

.message {
  font-size: 1.125rem;
  color: #718096;
  margin-bottom: 2rem;
}

.loading {
  padding: 2rem 0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.course-info {
  background: #f7fafc;
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
}

.course-info h2 {
  font-size: 1.5rem;
  color: #667eea;
  margin-bottom: 0.5rem;
}

.course-info p {
  color: #4a5568;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background: #fee;
  color: #c53030;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.error-message svg {
  stroke: currentColor;
  flex-shrink: 0;
}

.actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.875rem 2rem;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.btn-outline {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
}

.btn-outline:hover {
  background: #f7fafc;
}

@media (max-width: 768px) {
  .payment-card {
    padding: 3rem 2rem;
  }
  
  h1 {
    font-size: 1.5rem;
  }
  
  .actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
