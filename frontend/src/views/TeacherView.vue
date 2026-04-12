<template>
  <div class="teacher-page">
    <div class="container">
      <div class="page-header">
        <h1><i class="fas fa-chalkboard-teacher"></i> Teacher Dashboard</h1>
        <p>Manage your courses and students</p>
      </div>

      <div class="coming-soon">
        <i class="fas fa-tools"></i>
        <h2>Teacher Dashboard Coming Soon</h2>
        <p>We're working on bringing you a comprehensive teacher dashboard to manage your courses and students.</p>
        <RouterLink to="/" class="btn btn-primary">
          <i class="fas fa-home"></i>
          Back to Home
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
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
  color: #2d3748;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.page-header p {
  font-size: 1.125rem;
  color: #718096;
  margin-top: 0.5rem;
}

.coming-soon {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.coming-soon i {
  font-size: 5rem;
  color: #667eea;
  margin-bottom: 2rem;
}

.coming-soon h2 {
  font-size: 2rem;
  color: #2d3748;
  margin-bottom: 1rem;
}

.coming-soon p {
  color: #718096;
  font-size: 1.125rem;
  max-width: 600px;
  margin: 0 auto 2rem;
  line-height: 1.6;
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
