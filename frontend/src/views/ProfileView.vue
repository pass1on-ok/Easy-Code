<template>
  <div class="profile-page">
    <div class="container">
      <div class="page-header">
        <h1><i class="fas fa-user-circle"></i> My Profile</h1>
      </div>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>Loading profile...</p>
      </div>

      <div v-else-if="user" class="profile-content">
        <div class="profile-card">
          <div class="profile-avatar">
            <i class="fas fa-user-circle"></i>
          </div>
          <div class="profile-info">
            <h2>{{ user.username }}</h2>
            <p class="email"><i class="fas fa-envelope"></i> {{ user.email }}</p>
            <p v-if="user.first_name || user.last_name" class="name">
              <i class="fas fa-id-card"></i>
              {{ user.first_name }} {{ user.last_name }}
            </p>
          </div>
        </div>

        <div class="stats-grid">
          <div class="stat-card">
            <i class="fas fa-book"></i>
            <h3>Enrolled Courses</h3>
            <p class="stat-number">{{ purchasedCoursesCount }}</p>
          </div>
          <div class="stat-card">
            <i class="fas fa-certificate"></i>
            <h3>Certificates</h3>
            <p class="stat-number">0</p>
          </div>
          <div class="stat-card">
            <i class="fas fa-clock"></i>
            <h3>Hours Learned</h3>
            <p class="stat-number">0</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useCoursesStore } from '@/stores/courses'

const authStore = useAuthStore()
const coursesStore = useCoursesStore()

const user = computed(() => authStore.user)
const loading = computed(() => authStore.loading)
const purchasedCoursesCount = computed(() => coursesStore.purchasedCourses.length)

onMounted(() => {
  authStore.fetchCurrentUser()
  coursesStore.fetchPurchasedCourses()
})
</script>

<style scoped>
.profile-page {
  padding: 3rem 0;
  min-height: 80vh;
}

.container {
  max-width: 1200px;
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
  color: #2d3748;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.profile-card {
  background: white;
  padding: 3rem;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 2rem;
}

.profile-avatar {
  font-size: 8rem;
  color: #667eea;
}

.profile-info h2 {
  font-size: 2rem;
  color: #2d3748;
  margin-bottom: 0.75rem;
}

.profile-info p {
  color: #718096;
  font-size: 1. 125rem;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.stat-card {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  text-align: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.2);
}

.stat-card i {
  font-size: 3rem;
  color: #667eea;
  margin-bottom: 1rem;
}

.stat-card h3 {
  font-size: 1.125rem;
  color: #718096;
  margin-bottom: 0.75rem;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.loading {
  text-align: center;
  padding: 4rem 2rem;
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

@media (max-width: 768px) {
  .profile-card {
    flex-direction: column;
    text-align: center;
    padding: 2rem;
  }

  .profile-avatar {
    font-size: 6rem;
  }

  .profile-info p {
    justify-content: center;
  }
  
  .profile-info h2 {
    font-size: 1.75rem;
  }
  
  .profile-info p {
    font-size: 1rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .profile-page {
    padding: 2rem 0;
  }
  
  .container {
    padding: 0 1rem;
  }
  
  .page-header h1 {
    font-size: 2rem;
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .profile-card {
    padding: 1.5rem;
    border-radius: 16px;
  }
  
  .profile-avatar {
    font-size: 5rem;
  }
  
  .profile-info h2 {
    font-size: 1.5rem;
  }
  
  .profile-info p {
    font-size: 0.9375rem;
  }
  
  .stat-card {
    padding: 1.5rem;
  }
  
  .stat-card i {
    font-size: 2.5rem;
  }
  
  .stat-card h3 {
    font-size: 1rem;
  }
  
  .stat-number {
    font-size: 2rem;
  }
}

@media (max-width: 380px) {
  .profile-avatar {
    font-size: 4rem;
  }
  
  .profile-info h2 {
    font-size: 1.375rem;
  }
  
  .page-header h1 {
    font-size: 1.75rem;
  }
}
</style>
