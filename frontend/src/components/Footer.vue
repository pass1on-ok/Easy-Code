<template>
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-content">
        <div class="footer-section">
          <h3 class="footer-title">{{ t('footer.title') }}</h3>
          <p class="footer-description">
            {{ t('footer.description') }}
          </p>
        </div>

        <div class="footer-section">
          <h4 class="section-title">{{ t('footer.quickLinks') }}</h4>
          <ul class="footer-links">
            <li><RouterLink to="/">{{ t('nav.home') }}</RouterLink></li>
            <li><RouterLink to="/my-courses">{{ t('nav.myCourses') }}</RouterLink></li>
            <li><RouterLink to="/profile">{{ t('nav.profile') }}</RouterLink></li>
            <li><RouterLink to="/teacher">{{ t('footer.forTeachers') }}</RouterLink></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4 class="section-title">{{ t('footer.connect') }}</h4>
          <div class="social-icons">
            <a href="https://facebook.com" target="_blank" class="social-icon">
              <i class="fab fa-facebook-f"></i>
            </a>
            <a href="https://twitter.com" target="_blank" class="social-icon">
              <i class="fab fa-twitter"></i>
            </a>
            <a href="https://instagram.com" target="_blank" class="social-icon">
              <i class="fab fa-instagram"></i>
            </a>
            <a href="https://linkedin.com" target="_blank" class="social-icon">
              <i class="fab fa-linkedin-in"></i>
            </a>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <p>{{ t('footer.copyright') }}</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useLanguageStore } from '@/stores/language'

const languageStore = useLanguageStore()
const currentLanguage = computed(() => languageStore.currentLanguage)
const t = (key: string) => {
  const _ = currentLanguage.value; // Creates reactive dependency
  return languageStore.t(key);
}
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #2d3748 0%, #1a202c 100%);
  color: white;
  padding: 3rem 2rem 1.5rem;
  margin-top: auto;
  width: 100%;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 3rem;
  margin-bottom: 2rem;
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-title {
  font-size: 1.75rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
}

.footer-description {
  color: var(--color-text-tertiary, #cbd5e0);
  line-height: 1.6;
  font-size: 0.95rem;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.footer-links a {
  color: var(--color-text-tertiary, #cbd5e0);
  text-decoration: none;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.footer-links a:hover {
  color: #667eea;
  transform: translateX(5px);
}

.footer-links a::before {
  content: '→';
  opacity: 0;
  transition: opacity 0.3s ease;
}

.footer-links a:hover::before {
  opacity: 1;
}

.social-icons {
  display: flex;
  gap: 1rem;
}

.social-icon {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 1.125rem;
}

.social-icon:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: translateY(-5px) scale(1.1);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.footer-bottom {
  padding-top: 2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
  color: var(--color-text-tertiary, #a0aec0);
  font-size: 0.875rem;
}

@media (max-width: 968px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .footer-section {
    text-align: center;
  }
  
  .footer-links {
    align-items: center;
  }

  .social-icons {
    justify-content: center;
  }
}

@media (max-width: 640px) {
  .footer {
    padding: 2rem 0 1rem;
  }

  .footer-container {
    padding: 0 1rem;
  }

  .footer-content {
    gap: 1.5rem;
  }
  
  .footer-title {
    font-size: 1.5rem;
  }
  
  .footer-description {
    font-size: 0.875rem;
  }
  
  .section-title {
    font-size: 1rem;
  }

  .social-icon {
    width: 40px;
    height: 40px;
    font-size: 1rem;
  }
  
  .footer-bottom {
    padding-top: 1.5rem;
    font-size: 0.8125rem;
  }
}
</style>
