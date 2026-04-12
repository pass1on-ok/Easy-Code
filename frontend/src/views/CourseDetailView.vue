<template>
  <div class="course-detail-page">
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Loading course details...</p>
    </div>

    <div v-else-if="error" class="error-container">
      <div class="error-message">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <h2>Course Not Found</h2>
        <p>{{ error }}</p>
        <RouterLink to="/" class="btn btn-primary">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9 22V12h6v10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Back to Home
        </RouterLink>
      </div>
    </div>

    <div v-else-if="course" class="course-content">
      <section class="course-hero">
        <div class="container">
          <div class="hero-content">
            <h1>{{ course.name }}</h1>
            <p class="course-description">{{ course.description }}</p>
            <div class="course-meta">
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
              <RouterLink :to="`/checkout/${course.slug}`" class="btn btn-large btn-primary">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="9" cy="21" r="1" fill="currentColor"/>
                  <circle cx="20" cy="21" r="1" fill="currentColor"/>
                  <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                Enroll Now
              </RouterLink>
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
                About This Course
              </h2>
              <p>{{ course.description }}</p>

              <div v-if="course.videos && course.videos.length > 0" class="videos-section">
                <h2>
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <polygon points="10 8 16 12 10 16" fill="currentColor"/>
                  </svg>
                  Course Videos
                </h2>
                
                <div v-if="selectedVideo" class="video-player-container">
                  <div class="video-player">
                    <iframe
                      :src="selectedVideo.video_url"
                      frameborder="0"
                      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                      allowfullscreen
                    ></iframe>
                  </div>
                  <div class="video-info">
                    <h3>{{ selectedVideo.title }}</h3>
                    <p v-if="selectedVideo.description">{{ selectedVideo.description }}</p>
                  </div>
                </div>
                
                <div class="videos-list">
                  <div 
                    v-for="video in course.videos" 
                    :key="video.id" 
                    class="video-item"
                    :class="{ 'active': selectedVideo?.id === video.id }"
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
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="sidebar">
              <div class="info-card">
                <h3>Course Includes</h3>
                <ul>
                  <li>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M23 7l-7 5 7 5V7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <rect x="1" y="5" width="15" height="14" rx="2" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    {{ course.videos?.length || 0 }} Video lectures
                  </li>
                  <li>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ course.materials?.length || 0 }} Downloadable resources
                  </li>
                  <li>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                    Lifetime access
                  </li>
                  <li>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M4 7h16M4 12h16M4 17h10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      <path d="M20 17l-2 2 4 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    Certificate of completion
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref, watch } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { useCoursesStore } from '@/stores/courses'

const route = useRoute()
const coursesStore = useCoursesStore()

const selectedVideo = ref<any>(null)

const course = computed(() => coursesStore.currentCourse)
const loading = computed(() => coursesStore.loading)
const error = computed(() => coursesStore.error)

const selectVideo = (video: any) => {
  selectedVideo.value = video
}

onMounted(() => {
  const slug = route.params.slug as string
  coursesStore.fetchCourseBySlug(slug)
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
  color: #000000;
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
  background: white;
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
  color: #2d3748;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.main-content p {
  color: #000000;
  line-height: 1.8;
  font-size: 1.125rem;
  margin-bottom: 2rem;
}

.videos-section {
  margin-top: 3rem;
}

.video-player-container {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
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
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.video-info p {
  color: #718096;
  line-height: 1.6;
}

.videos-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.video-item {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
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
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
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
  color: #2d3748;
  margin-bottom: 0.5rem;
  font-size: 1.125rem;
}

.video-item p {
  color: #718096;
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
  background: white;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.info-card h3 {
  font-size: 1.375rem;
  color: #2d3748;
  margin-bottom: 1.5rem;
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
  color: #718096;
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
  text-align: center;
  padding: 3rem;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.error-message i {
  font-size: 4rem;
  color: #fc8181;
  margin-bottom: 1rem;
}

.error-message h2 {
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.error-message p {
  color: #718096;
  margin-bottom: 2rem;
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
