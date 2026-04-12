<template>
  <div class="home-page">
    <section class="hero">
      <div class="hero-container">
        <div class="hero-content">
          <h1 class="hero-title">
            Learn to Code,
            <span class="gradient-text">Build Your Future</span>
          </h1>
          <p class="hero-description">
            Master programming with interactive courses, hands-on projects, and expert guidance.
            Start your coding journey today!
          </p>
          <div class="hero-actions">
            <RouterLink to="/signup" class="btn btn-large btn-primary">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L2 7l10 5 10-5-10-5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="currentColor"/>
                <path d="M2 17l10 5 10-5M2 12l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              Get Started
            </RouterLink>
            <a href="#courses" class="btn btn-large btn-outline">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <polygon points="10 8 16 12 10 16" fill="currentColor"/>
              </svg>
              Explore Courses
            </a>
          </div>
        </div>
        <div class="hero-illustration">
          <div class="floating-card card-1">
            <i class="fab fa-python"></i>
          </div>
          <div class="floating-card card-2">
            <i class="fab fa-js"></i>
          </div>
          <div class="floating-card card-3">
            <i class="fab fa-react"></i>
          </div>
          <div class="floating-card card-4">
            <i class="fab fa-vuejs"></i>
          </div>
        </div>
      </div>
    </section>

    <section id="courses" class="courses-section">
      <div class="section-header">
        <h2 class="section-title">Our Popular Courses</h2>
        <p class="section-description">
          Choose from our wide range of courses designed by industry experts
        </p>
      </div>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>Loading courses...</p>
      </div>

      <div v-else-if="error" class="error-message">
        <i class="fas fa-exclamation-circle"></i>
        <p>{{ error }}</p>
      </div>

      <div v-else class="courses-grid">
        <CourseCard v-for="course in courses" :key="course.id" :course="course" />
      </div>

      <div v-if="courses.length === 0 && !loading && !error" class="empty-state">
        <i class="fas fa-book-open"></i>
        <h3>No courses available yet</h3>
        <p>Check back soon for new courses!</p>
      </div>
    </section>

    <section class="features-section">
      <h2 class="section-title">Why Choose Easy Code?</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 10v6M2 10l10-5 10 5-10 5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M6 12v5c3 1.5 7 1.5 10 0v-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Expert Instructors</h3>
          <p>Learn from industry professionals with years of experience</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <h3>Learn at Your Pace</h3>
          <p>Flexible schedules that fit your lifestyle</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 7h16M4 12h16M4 17h10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <path d="M20 17l-2 2 4 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Get Certified</h3>
          <p>Earn certificates upon course completion</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Community Support</h3>
          <p>Join a thriving community of learners</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useCoursesStore } from '@/stores/courses'
import CourseCard from '@/components/CourseCard.vue'

const coursesStore = useCoursesStore()

const courses = computed(() => coursesStore.courses)
const loading = computed(() => coursesStore.loading)
const error = computed(() => coursesStore.error)

onMounted(() => {
  coursesStore.fetchCourses()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 6rem 2rem;
  position: relative;
  overflow: hidden;
  min-height: 600px;
  width: 100%;
}

.hero-container {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.hero::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 600px;
  height: 600px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: float 20s infinite ease-in-out;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(180deg);
  }
}

.hero-content {
  color: white;
  z-index: 1;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 1.5rem;
}

.gradient-text {
  display: block;
  background: linear-gradient(to right, #ffd89b, #19547b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-description {
  font-size: 1.25rem;
  line-height: 1.6;
  margin-bottom: 2rem;
  opacity: 0.95;
}

.hero-actions {
  display: flex;
  gap: 1rem;
}

.hero-illustration {
  position: relative;
  height: 400px;
}

.floating-card {
  position: absolute;
  width: 100px;
  height: 100px;
  background: white;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  animation: floatCard 3s infinite ease-in-out;
}

.card-1 {
  top: 10%;
  left: 10%;
  color: #3776ab;
  animation-delay: 0s;
}

.card-2 {
  top: 50%;
  left: 50%;
  color: #f7df1e;
  animation-delay: 0.5s;
}

.card-3 {
  bottom: 20%;
  left: 20%;
  color: #61dafb;
  animation-delay: 1s;
}

.card-4 {
  top: 30%;
  right: 10%;
  color: #42b883;
  animation-delay: 1.5s;
}

@keyframes floatCard {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

.courses-section,
.features-section {
  padding: 5rem 2rem;
  width: 100%;
}

.section-header,
.courses-grid,
.features-grid {
  max-width: 1400px;
  margin-left: auto;
  margin-right: auto;
}

.features-section {
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
}

.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 800;
  color: #2d3748;
  margin-bottom: 1rem;
}

.section-description {
  font-size: 1.125rem;
  color: #718096;
  max-width: 600px;
  margin: 0 auto;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 2rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 3rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.2);
}

.feature-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 2rem;
}

.feature-card h3 {
  font-size: 1.25rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 0.75rem;
}

.feature-card p {
  color: #718096;
  line-height: 1.6;
}

.btn {
  padding: 0.875rem 2rem;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1rem;
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

.btn-outline {
  background: transparent;
  border: 2px solid white;
  color: white;
}

.btn-outline:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-3px);
}

.loading,
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #718096;
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

.empty-state i {
  font-size: 4rem;
  color: #cbd5e0;
  margin-bottom: 1rem;
}

.error-message {
  text-align: center;
  padding: 2rem;
  background: #fff5f5;
  border: 2px solid #fc8181;
  border-radius: 12px;
  color: #c53030;
}

.error-message i {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

@media (max-width: 1200px) {
  .hero {
    gap: 3rem;
    padding: 5rem 2rem;
  }
  
  .courses-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (max-width: 968px) {
  .hero {
    padding: 3rem 1.5rem;
  }
  
  .hero-container {
    grid-template-columns: 1fr;
  }

  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-description {
    font-size: 1.125rem;
  }

  .hero-illustration {
    display: none;
  }

  .hero-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .hero-actions .btn {
    width: 100%;
  }

  .courses-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .courses-section,
  .features-section {
    padding: 3rem 1.5rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
}

@media (max-width: 640px) {
  .hero {
    padding: 2.5rem 1rem;
  }
  
  .hero-title {
    font-size: 2rem;
    line-height: 1.3;
  }
  
  .hero-description {
    font-size: 1rem;
  }
  
  .btn-large {
    padding: 0.875rem 2rem;
    font-size: 1rem;
  }

  .section-title {
    font-size: 1.75rem;
  }
  
  .section-description {
    font-size: 1rem;
  }
  
  .courses-section,
  .features-section {
    padding: 2.5rem 1rem;
  }
  
  .section-header {
    margin-bottom: 2rem;
  }
}

@media (max-width: 380px) {
  .hero-title {
    font-size: 1.75rem;
  }
  
  .hero-actions {
    gap: 0.75rem;
  }
  
  .btn-large {
    padding: 0.75rem 1.5rem;
    font-size: 0.95rem;
  }
}
</style>
