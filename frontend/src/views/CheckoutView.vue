<template>
  <div class="checkout-page">
    <div class="container">
      <div class="checkout-content">
        <div class="checkout-card">
          <div class="card-header">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="9" cy="21" r="1" fill="currentColor"/>
              <circle cx="20" cy="21" r="1" fill="currentColor"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h1>Checkout</h1>
          </div>

          <div v-if="loading" class="loading">
            <div class="spinner"></div>
            <p>Processing...</p>
          </div>

          <div v-else-if="course" class="course-summary">
            <h2>Course Summary</h2>
            <div class="course-info">
              <h3>{{ course.name }}</h3>
              <p>{{ course.description }}</p>
              <div class="price-info">
                <span class="label">Price:</span>
                <span class="price">{{ course.price }} ₸</span>
              </div>
            </div>

            <button @click="handleEnroll" class="btn btn-primary btn-block" :disabled="enrolling">
              <span v-if="enrolling" class="spinner-small"></span>
              <span v-else>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                  <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                Pay with Stripe
              </span>
            </button>

            <div v-if="error" class="error-message">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              {{ error }}
            </div>
          </div>
        </div>

        <div class="info-panel">
          <h3>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Secure Payment
          </h3>
          <p>Your payment information is secure and encrypted.</p>
          
          <h3>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M1 4v6h6M23 20v-6h-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M20.49 9A9 9 0 0 0 5.64 5.64L1 10m22 4l-4.64 4.36A9 9 0 0 1 3.51 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            30-Day Money Back
          </h3>
          <p>Not satisfied? Get a full refund within 30 days.</p>
          
          <h3>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            Lifetime Access
          </h3>
          <p>Once enrolled, you have lifetime access to the course.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCoursesStore } from '@/stores/courses'
import { paymentService } from '@/services/payment'

const route = useRoute()
const router = useRouter()
const coursesStore = useCoursesStore()

const enrolling = ref(false)
const paymentError = ref<string | null>(null)

const course = computed(() => coursesStore.currentCourse)
const loading = computed(() => coursesStore.loading)
const error = computed(() => paymentError.value || coursesStore.error)

onMounted(() => {
  const slug = route.params.slug as string
  coursesStore.fetchCourseBySlug(slug)
})

const handleEnroll = async () => {
  enrolling.value = true
  paymentError.value = null
  const slug = route.params.slug as string
  
  try {
    const response = await paymentService.createCheckoutSession(slug)
    // Redirect to Stripe Checkout
    window.location.href = response.url
  } catch (err: any) {
    paymentError.value = err.response?.data?.error || 'Failed to create checkout session'
    enrolling.value = false
  }
}
</script>

<style scoped>
.checkout-page {
  min-height: 80vh;
  padding: 3rem 0;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.checkout-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
  align-items: start;
}

.checkout-card {
  background: white;
  border-radius: 20px;
  padding: 3rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 2px solid #e2e8f0;
}

.card-header i {
  font-size: 2rem;
  color: #667eea;
}

.card-header h1 {
  font-size: 2rem;
  color: #2d3748;
}

.course-summary h2 {
  font-size: 1.5rem;
  color: #2d3748;
  margin-bottom: 1.5rem;
}

.course-info {
  background: #f7fafc;
  padding: 2rem;
  border-radius: 12px;
  margin-bottom: 2rem;
}

.course-info h3 {
  font-size: 1.375rem;
  color: #2d3748;
  margin-bottom: 0.75rem;
}

.course-info p {
  color: #718096;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.price-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1.5rem;
  border-top: 2px solid #e2e8f0;
}

.price-info .label {
  font-size: 1.125rem;
  color: #718096;
  font-weight: 600;
}

.price {
  font-size: 2rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.btn {
  padding: 1rem 2rem;
  border-radius: 12px;
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

.info-panel {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 2rem;
}

.info-panel h3 {
  font-size: 1.125rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.info-panel h3 i {
  color: #667eea;
}

.info-panel p {
  color: #718096;
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.loading {
  text-align: center;
  padding: 3rem;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #e2e8f0;
  border-top-color: #667eea;
  border-radius: 50%;
  margin: 0 auto 1rem;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-message {
  background: #fff5f5;
  border: 2px solid #fc8181;
  color: #c53030;
  padding: 1rem;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-top: 1rem;
}

@media (max-width: 968px) {
  .checkout-content {
    grid-template-columns: 1fr;
  }

  .info-panel {
    position: static;
  }

  .checkout-card {
    padding: 2rem;
  }
}

@media (max-width: 640px) {
  .checkout-page {
    padding: 2rem 0;
  }
  
  .container {
    padding: 0 1rem;
  }
  
  .checkout-card {
    padding: 1.5rem;
    border-radius: 16px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
    margin-bottom: 1.5rem;
    padding-bottom: 1.5rem;
  }
  
  .card-header i {
    font-size: 1.75rem;
  }
  
  .card-header h1 {
    font-size: 1.75rem;
  }
  
  .course-summary h2 {
    font-size: 1.375rem;
  }
  
  .course-info {
    padding: 1.5rem;
  }
  
  .course-info h3 {
    font-size: 1.25rem;
  }
  
  .course-info p {
    font-size: 0.9375rem;
  }
  
  .price-info {
    padding-top: 1.25rem;
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .price {
    font-size: 1.75rem;
  }
  
  .info-panel {
    padding: 1.5rem;
  }
  
  .info-panel h3 {
    font-size: 1rem;
  }
  
  .info-panel p {
    font-size: 0.9375rem;
    margin-bottom: 1.25rem;
  }
  
  .btn {
    padding: 0.875rem 1.5rem;
    font-size: 0.9375rem;
  }
}

@media (max-width: 380px) {
  .checkout-card {
    padding: 1.25rem;
  }
  
  .card-header h1 {
    font-size: 1.5rem;
  }
  
  .price {
    font-size: 1.5rem;
  }
}
</style>
