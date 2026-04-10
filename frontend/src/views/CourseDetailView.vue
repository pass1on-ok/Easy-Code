<template>
  <div class="course-detail-page">
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>{{ t('common.loading') }}</p>
    </div>

    <div v-else-if="error" class="error-container">
      <div class="error-message">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <h2>{{ t('course.notFound') }}</h2>
        <p>{{ error }}</p>
        <RouterLink to="/" class="btn btn-primary">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9 22V12h6v10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          {{ t('course.backToHome') }}
        </RouterLink>
      </div>
    </div>

    <div v-else-if="course" class="course-content">
      <section class="course-hero">
        <div class="container">
          <div class="hero-content">
            <h1>{{ localizedCourse.name }}</h1>
            <p class="course-description">{{ localizedCourse.summary }}</p>
            <div v-if="completionStatus?.enrolled === false" class="course-meta">
              <span class="price">{{ course.price }} ₸</span>
              <span v-if="course.discount" class="discount">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="7" cy="7" r="1.5" fill="currentColor"/>
                </svg>
                Save {{ course.discount }}%
              </span>
            </div>
            <div class="hero-actions">
              <div v-if="completionStatus?.enrolled === true" class="enrolled-badge">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <polyline points="22 4 12 14.01 9 11.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
                </svg>
                {{ t('course.enrolled') }}
              </div>
              <RouterLink 
                v-else-if="completionStatus?.enrolled === false" 
                :to="`/checkout/${course.slug}`" 
                class="btn btn-large btn-primary"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="9" cy="21" r="1" fill="currentColor"/>
                  <circle cx="20" cy="21" r="1" fill="currentColor"/>
                  <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                {{ t('course.enrollNow') }}
              </RouterLink>
              <div v-else class="loading-badge">
                <div class="spinner-small"></div>
                {{ t('course.checkingEnrollment') }}
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="course-details">
        <div class="container">
          <div class="details-grid">
            <div class="main-content">
              <h2>
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                  <path d="M12 16v-4M12 8h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
                {{ t('course.aboutCourse') }}
              </h2>
              <p>{{ localizedCourse.description }}</p>

            <div v-if="course.videos && course.videos.length > 0" class="videos-section">
                <h2>
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <polygon points="10 8 16 12 10 16" fill="currentColor"/>
                  </svg>
                  {{ t('course.courseVideos') }}
                </h2>
                
                <!-- No Access Warning -->
                <div v-if="!course.has_access && course.videos.length === 1" class="no-access-warning">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <p>{{ t('course.purchaseToAccess') }}</p>
                  <RouterLink :to="`/checkout/${course.slug}`" class="btn btn-small btn-primary">
                    {{ t('course.buy') }}
                  </RouterLink>
                </div>
                
                <div v-if="selectedVideo" class="video-player-container">
                  <div class="video-player">
                    <iframe
                      :src="selectedVideoEmbedUrl"
                      frameborder="0"
                      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                      allowfullscreen
                    ></iframe>
                  </div>
                  <div class="video-info">
                    <h3>{{ selectedVideo.title }}</h3>
                    <p v-if="selectedVideo.description">{{ selectedVideo.description }}</p>
                    <button
                      v-if="completionStatus?.enrolled"
                      @click="openTestModal"
                      class="btn-test"
                    >
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M8 6h13M8 12h13M8 18h13M3 6h.01M3 12h.01M3 18h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      </svg>
                      {{ t('course.takeTest') }}
                    </button>
                  </div>
                </div>
                
                <div class="videos-list">
                  <div 
                    v-for="video in course.videos" 
                    :key="video.id" 
                    class="video-item"
                    :class="{ 'active': selectedVideo?.id === video.id, 'locked': !course.has_access && !video.is_preview }"
                    @click="selectVideo(video)"
                  >
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M23 7l-7 5 7 5V7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="currentColor"/>
                      <rect x="1" y="5" width="15" height="14" rx="2" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    <div>
                      <h4>{{ video.serial_number }}. {{ video.title }}</h4>
                      <p v-if="video.description">{{ video.description }}</p>
                      <span v-if="video.is_preview" class="preview-badge">Preview</span>
                      <span v-if="!course.has_access && !video.is_preview" class="locked-badge">
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                          <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                        Locked
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="sidebar">
              <!-- Course Completion Status -->
              <div v-if="completionStatus?.enrolled" class="info-card completion-card">
                <h3>Your Progress</h3>
                <div class="progress-stats">
                  <div class="stat-item">
                    <span class="stat-label">Tests Completed</span>
                    <span class="stat-value">{{ completionStatus.completed_tests }} / {{ completionStatus.total_tests }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">Pass Rate</span>
                    <span class="stat-value">{{ completionStatus.passed_tests }} / {{ completionStatus.total_tests }}</span>
                  </div>
                  
                  <!-- Final Exam Button -->
                  <button
                    v-if="canTakeFinalExam && !completionStatus.completed"
                    @click="openFinalTestModal"
                    class="btn-final-exam"
                  >
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                    {{ t('finalTest.takeFinalExam') }}
                  </button>
                  <p v-else-if="!canTakeFinalExam && !completionStatus.completed" class="final-exam-locked">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                      <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ t('finalTest.completeAllTests') }}
                  </p>
                  
                  <!-- Course Completed Badge -->
                  <div v-if="completionStatus.completed" class="completion-badge">
                    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <polyline points="22 4 12 14.01 9 11.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <p>Course Completed!</p>
                  </div>
                  
                  <!-- Certificate Button -->
                  <button
                    v-if="completionStatus.can_generate_certificate"
                    @click="openCertificateModal"
                    class="btn-certificate"
                  >
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M4 22h16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    Get Certificate
                  </button>
                </div>
              </div>
              
              <div class="info-card">
                <h3>{{ t('course.courseIncludes') }}</h3>
                <ul>
                  <li>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M23 7l-7 5 7 5V7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <rect x="1" y="5" width="15" height="14" rx="2" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    {{ course.videos?.length || 0 }} {{ t('course.videoLectures') }}
                  </li>
                  <li>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ course.materials?.length || 0 }} {{ t('course.downloadableResources') }}
                  </li>
                  <li>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                    {{ t('course.lifetimeAccess') }}
                  </li>
                  <li>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M4 7h16M4 12h16M4 17h10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      <path d="M20 17l-2 2 4 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ t('course.certificate') }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
    
    <!-- Test Modal -->
    <TestModal 
      :isOpen="showTestModal"
      :video="selectedVideo"
      :courseSlug="course?.slug || ''"
      @close="closeTestModal"
      @testCompleted="handleTestCompleted"
    />
    
    <!-- Certificate Modal -->
    <CertificateModal
      :isOpen="showCertificateModal"
      :courseSlug="course?.slug || ''"
      @close="closeCertificateModal"
    />
    
    <!-- Final Test Modal -->
    <FinalTestModal
      :isOpen="showFinalTestModal"
      :courseSlug="course?.slug || null"
      :courseName="course?.name || ''"
      @close="closeFinalTestModal"
      @testCompleted="handleFinalTestCompleted"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref, watch } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { useCoursesStore } from '@/stores/courses'
import { useAuthStore } from '@/stores/auth'
import { useLanguageStore } from '@/stores/language'
import testService from '@/services/test'
import TestModal from '@/components/TestModal.vue'
import CertificateModal from '@/components/CertificateModal.vue'
import FinalTestModal from '@/components/FinalTestModal.vue'

const route = useRoute()
const coursesStore = useCoursesStore()
const authStore = useAuthStore()
const languageStore = useLanguageStore()

const selectedVideo = ref<any>(null)
const showTestModal = ref(false)
const showCertificateModal = ref(false)
const showFinalTestModal = ref(false)
const completionStatus = ref<any>(null)

const selectedVideoEmbedUrl = computed(() => {
  if (!selectedVideo.value?.video_url) return ''
  const rawUrl = selectedVideo.value.video_url

  if (rawUrl.startsWith('http://') || rawUrl.startsWith('https://')) {
    return rawUrl
  }

  if (rawUrl.includes('youtube.com/embed')) {
    return rawUrl
  }

  if (rawUrl.includes('watch?v=')) {
    return rawUrl.replace('watch?v=', 'embed/')
  }

  return `https://www.youtube.com/embed/${rawUrl}`
})

const course = computed(() => coursesStore.currentCourse)
const loading = computed(() => coursesStore.loading)
const error = computed(() => coursesStore.error)
const currentLanguage = computed(() => languageStore.currentLanguage)

const canTakeFinalExam = computed(() => {
  if (!completionStatus.value) return false
  return completionStatus.value.passed_tests === completionStatus.value.total_tests
})

// Make translation reactive
const t = (key: string) => {
  const _ = currentLanguage.value;
  return languageStore.t(key);
}

const localizedCourse = computed(() => {
  if (!course.value) {
    return { name: '', summary: '', description: '' }
  }

  const slug = course.value.slug || ''
  const titleKey = `course.${slug}.title`
  const descriptionKey = `course.${slug}.description`
  const summaryKey = `course.${slug}.summary`
  const fullDescription = languageStore.tCourse(descriptionKey, course.value.description)
  const summary = languageStore.tCourse(summaryKey, '') || fullDescription

  return {
    name: languageStore.tCourse(titleKey, course.value.name),
    summary,
    description: fullDescription
  }
})

const selectVideo = (video: any) => {
  selectedVideo.value = video
}

const openTestModal = () => {
  if (!selectedVideo.value) return
  showTestModal.value = true
}

const closeTestModal = () => {
  showTestModal.value = false
}

const openCertificateModal = () => {
  if (!course.value?.slug) return
  showCertificateModal.value = true
}

const closeCertificateModal = () => {
  showCertificateModal.value = false
}

const openFinalTestModal = () => {
  if (!course.value?.slug) return
  showFinalTestModal.value = true
}

const closeFinalTestModal = () => {
  showFinalTestModal.value = false
}

const handleFinalTestCompleted = async (passed: boolean) => {
  // Reload completion status after final test
  await loadCompletionStatus()
  
  // If passed, completion status should now show completed
  if (passed && completionStatus.value) {
    completionStatus.value.completed = true
    completionStatus.value.can_generate_certificate = true
    
    // Auto-open certificate modal on success
    setTimeout(() => {
      openCertificateModal()
    }, 2000)
  }
}

const handleTestCompleted = async (passed: boolean) => {
  // Reload completion status after test
  await loadCompletionStatus()
  
  // Don't auto-complete course - student must take final exam first
}

const loadCompletionStatus = async () => {
  if (!course.value?.slug) return
  
  // If user is not authenticated, set enrolled to false
  if (!authStore.isAuthenticated) {
    completionStatus.value = { enrolled: false, completed: false }
    return
  }
  
  try {
    const status = await testService.getCourseCompletionStatus(course.value.slug)
    completionStatus.value = status
  } catch (err) {
    console.error('Failed to load completion status:', err)
    // If error (e.g., 401/403), assume not enrolled
    completionStatus.value = { enrolled: false, completed: false }
  }
}

onMounted(async () => {
  const slug = route.params.slug as string
  await coursesStore.fetchCourseBySlug(slug)
  await loadCompletionStatus()
})

// Auto-select first video when course loads
watch(course, (newCourse) => {
  if (newCourse?.videos && newCourse.videos.length > 0 && !selectedVideo.value) {
    selectedVideo.value = newCourse.videos[0]
  }
})
</script>

<style scoped>
.course-detail-page {
  min-height: 80vh;
}

.course-hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4rem 0;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.hero-content {
  max-width: 800px;
}

.hero-content h1 {
  font-size: 3rem;
  font-weight: 800;
  margin-bottom: 1rem;
}

.course-description {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.95);
  margin-bottom: 2rem;
  line-height: 1.6;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 2rem;
  margin-bottom: 2rem;
}

.price {
  font-size: 2.5rem;
  font-weight: 800;
}

.discount {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.hero-actions {
  display: flex;
  gap: 1rem;
}

.enrolled-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 2.5rem;
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
  color: white;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1.125rem;
  box-shadow: 0 4px 12px rgba(72, 187, 120, 0.3);
}

.loading-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 2.5rem;
  background: var(--color-card-bg);
  color: var(--color-text-secondary);
  border-radius: 12px;
  font-weight: 600;
  font-size: 1.125rem;
  border: 2px solid var(--color-border);
}

.spinner-small {
  width: 20px;
  height: 20px;
  border: 3px solid var(--color-border);
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.btn {
  padding: 0.875rem 2rem;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
}

.btn-large {
  padding: 1rem 2.5rem;
  font-size: 1.125rem;
}

.btn-primary {
  background: var(--color-card-bg);
  color: #667eea;
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 24px rgba(255, 255, 255, 0.3);
}

.course-details {
  padding: 4rem 0;
}

.details-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 3rem;
}

.main-content h2 {
  font-size: 1.75rem;
  color: var(--color-text-primary);
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.main-content p {
  color: var(--color-text-primary);
  line-height: 1.8;
  font-size: 1.125rem;
  margin-bottom: 2rem;
}

.videos-section {
  margin-top: 3rem;
}

.video-player-container {
  background: var(--color-card-bg);
  border-radius: 16px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 12px var(--color-card-shadow);
}

.video-player {
  position: relative;
  width: 100%;
  padding-bottom: 56.25%; /* 16:9 aspect ratio */
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 1.5rem;
}

.video-player iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.video-info h3 {
  font-size: 1.5rem;
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
}

.video-info p {
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.btn-test {
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  background: var(--gradient-hero);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-test:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.videos-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.video-item {
  background: var(--color-card-bg);
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px var(--color-card-shadow);
  display: flex;
  gap: 1rem;
  align-items: flex-start;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.video-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

.video-item.active {
  border-color: #667eea;
  background: var(--color-bg-secondary);
}

.video-item.active svg {
  stroke: #667eea;
  fill: #667eea;
}

.video-item svg {
  flex-shrink: 0;
  margin-top: 0.25rem;
}

.video-item h4 {
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
  font-size: 1.125rem;
}

.video-item p {
  color: var(--color-text-secondary);
  font-size: 0.95rem;
  margin: 0.5rem 0 0 0;
}

.preview-badge {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 0.75rem;
  font-weight: 600;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  margin-top: 0.5rem;
}

.sidebar {
  position: sticky;
  top: 2rem;
  height: fit-content;
}

.info-card {
  background: var(--color-card-bg);
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 4px 12px var(--color-card-shadow);
}

.info-card h3 {
  font-size: 1.375rem;
  color: var(--color-text-primary);
  margin-bottom: 1.5rem;
}

.completion-card {
  margin-bottom: 2rem;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border: 2px solid #667eea;
}

.progress-stats {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: var(--color-bg-secondary);
  border-radius: 8px;
}

.stat-label {
  color: var(--color-text-secondary);
  font-size: 0.95rem;
}

.stat-value {
  color: var(--color-text-primary);
  font-weight: 600;
  font-size: 1.1rem;
}

.completion-badge {
  text-align: center;
  padding: 1.5rem;
  background: var(--color-bg-secondary);
  border-radius: 8px;
  margin-top: 0.5rem;
}

.completion-badge svg {
  stroke: #10b981;
  margin-bottom: 0.5rem;
}

.completion-badge p {
  color: #10b981;
  font-weight: 600;
  font-size: 1.1rem;
  margin: 0;
}

.btn-certificate {
  width: 100%;
  padding: 0.875rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.btn-certificate:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
}

.btn-final-exam {
  width: 100%;
  padding: 0.875rem 1.5rem;
  background: linear-gradient(135deg, #f59e0b 0%, #ef4444 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.btn-final-exam:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.4);
}

.final-exam-locked {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.875rem 1.5rem;
  background: var(--color-bg-secondary);
  border: 2px dashed var(--color-border);
  border-radius: 8px;
  color: var(--color-text-tertiary);
  font-size: 0.875rem;
  text-align: center;
  margin-top: 0.5rem;
}

.final-exam-locked svg {
  stroke: var(--color-text-tertiary);
}

.info-card ul {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.info-card li {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: var(--color-text-secondary);
  font-size: 1rem;
}

.info-card li i {
  color: #667eea;
  font-size: 1.25rem;
}

.loading,
.error-container {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
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

.error-message {
  text-align: center;
  padding: 3rem;
  background: var(--color-card-bg);
  border-radius: 20px;
  box-shadow: 0 4px 12px var(--color-card-shadow);
}

.error-message i {
  font-size: 4rem;
  color: #fc8181;
  margin-bottom: 1rem;
}

.error-message h2 {
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
}

.error-message p {
  color: var(--color-text-secondary);
  margin-bottom: 2rem;
}

.no-access-warning {
  background: rgba(239, 68, 68, 0.1);
  border: 2px solid #ef4444;
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.no-access-warning svg {
  stroke: #ef4444;
  width: 2rem;
  height: 2rem;
}

.no-access-warning p {
  color: var(--color-text-primary);
  font-weight: 600;
  margin: 0;
}

.no-access-warning .btn-small {
  padding: 0.5rem 1.5rem;
  font-size: 0.95rem;
}

.video-item.locked {
  opacity: 0.6;
  cursor: not-allowed;
}

.video-item.locked:hover {
  transform: none;
  border-color: transparent;
  box-shadow: 0 2px 8px var(--color-card-shadow);
}

.locked-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  background: #ef4444;
  color: white;
  font-size: 0.7rem;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  margin-top: 0.5rem;
}

.locked-badge svg {
  stroke: white;
  fill: white;
}

@media (max-width: 968px) {
  .details-grid {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
  }

  .hero-content h1 {
    font-size: 2rem;
  }
  
  .course-description {
    font-size: 1.125rem;
  }

  .course-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .course-details {
    padding: 3rem 1.5rem;
  }
  
  .course-hero {
    padding: 3rem 0;
  }
}

@media (max-width: 640px) {
  .hero-content h1 {
    font-size: 1.75rem;
  }
  
  .course-description {
    font-size: 1rem;
  }
  
  .price {
    font-size: 2rem;
  }
  
  .discount {
    padding: 0.375rem 0.875rem;
    font-size: 0.875rem;
  }
  
  .hero-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .btn-large {
    width: 100%;
    padding: 0.875rem 2rem;
    font-size: 1rem;
  }
  
  .main-content h2 {
    font-size: 1.5rem;
  }
  
  .main-content p {
    font-size: 1rem;
  }
  
  .video-player-container {
    padding: 1rem;
  }
  
  .video-info h3 {
    font-size: 1.25rem;
  }
  
  .video-item {
    padding: 1.25rem;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .info-card {
    padding: 1.5rem;
  }
  
  .info-card h3 {
    font-size: 1.25rem;
  }
  
  .info-card li {
    font-size: 0.9375rem;
  }
  
  .course-details {
    padding: 2.5rem 1rem;
  }
  
  .course-hero {
    padding: 2.5rem 0;
  }
  
  .container {
    padding: 0 1rem;
  }
}

@media (max-width: 380px) {
  .hero-content h1 {
    font-size: 1.5rem;
  }
  
  .price {
    font-size: 1.75rem;
  }
  
  .btn-large {
    padding: 0.75rem 1.5rem;
    font-size: 0.95rem;
  }
}
</style>
