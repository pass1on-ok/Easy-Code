<template>
  <div class="my-courses-page">
    <div class="container">
      <div class="page-header">
        <h1>
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M8 7h8M8 11h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          {{ t('myCourses.title') }}
        </h1>
        <p>{{ t('myCourses.subtitle') }}</p>
      </div>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>{{ t('common.loading') }}</p>
      </div>

      <div v-else-if="error" class="error-message">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <p>{{ error }}</p>
      </div>

      <div v-else-if="purchasedCourses.length === 0" class="empty-state">
        <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M22 10v6M2 10l10-5 10 5-10 5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M6 12v5c3 1.5 7 1.5 10 0v-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <h2>{{ t('myCourses.noCourses') }}</h2>
        <p>{{ t('myCourses.noCoursesDescription') }}</p>
        <RouterLink to="/" class="btn btn-primary">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
            <path d="M21 21l-4.35-4.35" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          {{ t('myCourses.browseCourses') }}
        </RouterLink>
      </div>

      <div v-else class="courses-grid">
        <CourseCard v-for="course in purchasedCourses" :key="course.id" :course="course" :isPurchased="true" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useCoursesStore } from '@/stores/courses'
import { useLanguageStore } from '@/stores/language'
import CourseCard from '@/components/CourseCard.vue'

const coursesStore = useCoursesStore()
const languageStore = useLanguageStore()

const purchasedCourses = computed(() => coursesStore.purchasedCourses)
const loading = computed(() => coursesStore.loading)
const error = computed(() => coursesStore.error)
const currentLanguage = computed(() => languageStore.currentLanguage)

// Make translation reactive by depending on currentLanguage
const t = (key: string) => {
  const _ = currentLanguage.value;
  return languageStore.t(key);
}

onMounted(() => {
  coursesStore.fetchPurchasedCourses()
})
</script>

<style scoped>
.my-courses-page {
  padding: 3rem 0;
  min-height: 80vh;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-header h1 {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.page-header p {
  font-size: 1.125rem;
  color: var(--color-text-secondary);
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 2rem;
}

.loading,
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--color-border);
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

.empty-state i {
  font-size: 5rem;
  color: var(--color-text-tertiary);
  margin-bottom: 1.5rem;
}

.empty-state h2 {
  font-size: 2rem;
  color: var(--color-text-primary);
  margin-bottom: 1rem;
}

.empty-state p {
  color: var(--color-text-secondary);
  font-size: 1.125rem;
  margin-bottom: 2rem;
}

.btn {
  padding: 1rem 2rem;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.4);
}

.error-message {
  text-align: center;
  padding: 2rem;
  background: var(--color-bg-secondary);
  border: 2px solid var(--color-error);
  border-radius: 12px;
  color: var(--color-error);
}

@media (max-width: 768px) {
  .courses-grid {
    grid-template-columns: 1fr;
  }
  
  .page-header h1 {
    font-size: 2rem;
    flex-direction: column;
    gap: 0.5rem;
  }
}

@media (max-width: 640px) {
  .my-courses-page {
    padding: 2rem 0;
  }
  
  .container {
    padding: 0 1rem;
  }
  
  .page-header {
    margin-bottom: 2rem;
  }
  
  .page-header h1 {
    font-size: 1.75rem;
  }
  
  .page-header p {
    font-size: 1rem;
  }
  
  .courses-grid {
    gap: 1.5rem;
  }
  
  .empty-state {
    padding: 3rem 1.5rem;
  }
  
  .empty-state i {
    font-size: 4rem;
  }
  
  .empty-state h2 {
    font-size: 1.75rem;
  }
  
  .empty-state p {
    font-size: 1rem;
  }
  
  .btn {
    padding: 0.875rem 1.75rem;
  }
}

@media (max-width: 380px) {
  .page-header h1 {
    font-size: 1.5rem;
  }
  
  .empty-state i {
    font-size: 3.5rem;
  }
  
  .empty-state h2 {
    font-size: 1.5rem;
  }
}
</style>
