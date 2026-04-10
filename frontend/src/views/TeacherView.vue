<template>
  <div class="teacher-page">
    <div class="container">
      <div class="page-header">
        <h1><i class="fas fa-user"></i> {{ t('teacher.dashboard') }}</h1>
        <p>{{ t('teacher.manageCoursesStudents') }}</p>
      </div>

      <!-- Stats Grid -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-book"></i>
          </div>
          <div class="stat-content">
            <h3>{{ t('teacher.myCourses') }}</h3>
            <p class="stat-number">{{ dashboard.course_count }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-users"></i>
          </div>
          <div class="stat-content">
            <h3>{{ t('teacher.totalStudents') }}</h3>
            <p class="stat-number">{{ dashboard.student_count }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-circle"></i>
          </div>
          <div class="stat-content">
            <h3>{{ t('teacher.totalRevenue') }}</h3>
            <p class="stat-number">${{ dashboard.revenue }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-star"></i>
          </div>
          <div class="stat-content">
            <h3>{{ t('teacher.avgRating') }}</h3>
            <p class="stat-number">{{ dashboard.average_rating }}</p>
          </div>
        </div>
      </div>

      <!-- Create Course Button -->
      <div class="create-course-section">
        <RouterLink to="/teacher/create" class="btn btn-primary">
          <i class="fas fa-plus"></i>
          {{ t('teacher.createCourse') }}
        </RouterLink>
      </div>

      <!-- Courses List -->
      <div class="courses-section">
        <h2 class="section-title">{{ t('teacher.myCourses') }}</h2>
        <div v-if="loading" class="teacher-loading">Loading courses...</div>
        <div v-if="errorMessage" class="teacher-error">{{ errorMessage }}</div>
        <div v-if="!loading && !errorMessage" class="courses-grid">
          <div class="course-card" v-for="course in courses" :key="course.id">
            <div class="course-header">
              <h3>{{ course.name }}</h3>
              <span class="status-badge" :class="course.active ? 'published' : 'draft'">
                {{ course.active ? t('teacher.published') : t('teacher.draft') }}
              </span>
            </div>
            <div class="course-stats">
              <div class="stat-item">
                <i class="fas fa-dollar-sign"></i>
                <span>{{ course.price }}</span>
              </div>
              <div class="stat-item">
                <i class="fas fa-percent"></i>
                <span>{{ course.discount || 0 }}%</span>
              </div>
              <div class="stat-item">
                <i class="fas fa-clock"></i>
                <span>{{ course.length || 0 }} {{ t('teacher.hours') }}</span>
              </div>
            </div>
            <div class="course-actions">
              <RouterLink :to="`/course/${course.slug}`" class="btn btn-secondary">
                <i class="fas fa-eye"></i>
                {{ t('teacher.viewCourse') }}
              </RouterLink>
            </div>
          </div>
        </div>
        <div v-if="!loading && courses.length === 0" class="teacher-empty">
          {{ t('teacher.noCoursesYet') }}
        </div>
      </div>

      <!-- Coming Soon Notice -->
      <div class="coming-soon">
        <i class="fas fa-cog"></i>
        <h2>{{ t('teacher.comingSoon') }}</h2>
        <p>{{ t('teacher.comingSoonDescription') }}</p>
        <RouterLink to="/" class="btn btn-outline">
          <i class="fas fa-home"></i>
          {{ t('teacher.backToHome') }}
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useLanguageStore } from '@/stores/language'
import { teacherService } from '@/services/teacher'
import type { Course } from '@/types'

const languageStore = useLanguageStore()
const courses = ref<Course[]>([])
const loading = ref(false)
const errorMessage = ref<string | null>(null)
const dashboard = ref({
  course_count: 0,
  student_count: 0,
  revenue: 0,
  average_rating: 0
})

const currentLanguage = computed(() => languageStore.currentLanguage)
const t = (key: string) => {
  const _ = currentLanguage.value
  return languageStore.t(key)
}

async function loadTeacherData() {
  loading.value = true
  errorMessage.value = null

  try {
    const courseResponse = await teacherService.getTeacherCourses()
    courses.value = courseResponse
    const statsResponse = await teacherService.getTeacherDashboard()
    dashboard.value = statsResponse
  } catch (err: any) {
    errorMessage.value = err.response?.data?.error || err.message || 'Failed to load teacher data.'
  } finally {
    loading.value = false
  }
}

onMounted(loadTeacherData)
</script>

<style scoped>
.teacher-page {
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
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.page-header p {
  font-size: 1.125rem;
  color: var(--color-text-secondary);
  margin-top: 0.5rem;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: var(--color-card-bg);
  padding: 2rem;
  border-radius: 16px;
  box-shadow: var(--color-card-shadow);
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--color-card-shadow-hover);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.75rem;
  color: white;
}

.stat-content h3 {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-number {
  font-size: 2rem;
  font-weight: 800;
  color: var(--color-text-primary);
}

/* Create Course Section */
.create-course-section {
  text-align: center;
  margin-bottom: 3rem;
}

/* Courses Section */
.courses-section {
  margin-bottom: 3rem;
}

.section-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 1.5rem;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.course-card {
  background: var(--color-card-bg);
  padding: 1.5rem;
  border-radius: 16px;
  box-shadow: var(--color-card-shadow);
  transition: all 0.3s ease;
}

.course-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--color-card-shadow-hover);
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.course-header h3 {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-text-primary);
  flex: 1;
}

.status-badge {
  padding: 0.375rem 0.75rem;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.status-badge.published {
  background: rgba(72, 187, 120, 0.1);
  color: #48bb78;
}

.status-badge.draft {
  background: rgba(237, 137, 54, 0.1);
  color: #ed8936;
}

.course-stats {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

.stat-item i {
  color: #667eea;
}

.course-actions {
  display: flex;
  gap: 0.75rem;
}

/* Coming Soon */
.coming-soon {
  text-align: center;
  padding: 4rem 2rem;
  background: var(--color-card-bg);
  border-radius: 20px;
  box-shadow: var(--color-card-shadow);
  margin-top: 3rem;
}

.coming-soon i {
  font-size: 5rem;
  color: #667eea;
  margin-bottom: 2rem;
}

.coming-soon h2 {
  font-size: 2rem;
  color: var(--color-text-primary);
  margin-bottom: 1rem;
}

.coming-soon p {
  color: var(--color-text-secondary);
  font-size: 1.125rem;
  max-width: 600px;
  margin: 0 auto 2rem;
  line-height: 1.6;
}

/* Buttons */
.btn {
  padding: 1rem 2rem;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
  padding: 0.75rem 1.5rem;
  font-size: 0.875rem;
}

.btn-secondary:hover {
  background: var(--color-border);
}

.btn-outline {
  background: transparent;
  color: var(--color-text-primary);
  border: 2px solid var(--color-border);
}

.btn-outline:hover {
  background: var(--color-bg-secondary);
  border-color: #667eea;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .courses-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .teacher-page {
    padding: 2rem 0;
  }
  
  .container {
    padding: 0 1rem;
  }
  
  .page-header {
    margin-bottom: 2rem;
  }
  
  .page-header h1 {
    font-size: 2rem;
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .page-header p {
    font-size: 1rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .stat-card {
    padding: 1.5rem;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 1.5rem;
  }
  
  .stat-number {
    font-size: 1.75rem;
  }
  
  .coming-soon {
    padding: 3rem 1.5rem;
    border-radius: 16px;
  }
  
  .coming-soon i {
    font-size: 4rem;
    margin-bottom: 1.5rem;
  }
  
  .coming-soon h2 {
    font-size: 1.75rem;
  }
  
  .coming-soon p {
    font-size: 1rem;
  }
  
  .btn {
    padding: 0.875rem 1.75rem;
  }
  
  .course-stats {
    flex-direction: column;
    gap: 0.75rem;
  }
}

@media (max-width: 380px) {
  .page-header h1 {
    font-size: 1.75rem;
  }
  
  .coming-soon h2 {
    font-size: 1.5rem;
  }
  
  .coming-soon i {
    font-size: 3.5rem;
  }
}
</style>
