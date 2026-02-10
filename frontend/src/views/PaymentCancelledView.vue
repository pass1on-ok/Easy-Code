<template>
  <div class="payment-page">
    <div class="container">
      <div class="payment-card cancelled">
        <div class="icon-wrapper">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        
        <h1>Payment Cancelled</h1>
        <p class="message">Your payment was cancelled. No charges have been made to your account.</p>
        
        <div class="info-box">
          <h3>What happened?</h3>
          <p>You chose to cancel the payment process. Your course enrollment was not completed.</p>
        </div>
        
        <div class="actions">
          <RouterLink to="/" class="btn btn-primary">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Browse Courses
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
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

.payment-card.cancelled {
  border-top: 6px solid #f59e0b;
}

.icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
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

.info-box {
  background: #fffbeb;
  border: 2px solid #fbbf24;
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
  text-align: left;
}

.info-box h3 {
  font-size: 1.25rem;
  color: #92400e;
  margin-bottom: 0.5rem;
}

.info-box p {
  color: #78350f;
  line-height: 1.6;
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

.btn-primary svg {
  stroke: currentColor;
}

@media (max-width: 768px) {
  .payment-card {
    padding: 3rem 2rem;
  }
  
  h1 {
    font-size: 1.5rem;
  }
  
  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
