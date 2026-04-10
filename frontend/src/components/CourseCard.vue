<template>
  <div class="course-card">
    <div class="course-image">
      <img 
        :src="getImageUrl(course.thumbnail, course.slug)"
        :alt="localizedCourse.name"
        :data-slug="course.slug"
        @error="handleImageError" 
      />
      <div v-if="course.discount" class="discount-badge">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="currentColor"/>
          <circle cx="7" cy="7" r="1.5" fill="white"/>
        </svg>
        {{ t('course.save') }} {{ course.discount }}%
      </div>
    </div>

    <div class="course-body">
      <h3 class="course-title">{{ localizedCourse.name }}</h3>
      <p class="course-description">{{ truncateText(localizedCourse.description, 100) }}</p>

      <div class="course-footer">
        <div v-if="!isPurchased" class="course-price">
          <span class="price-amount">{{ course.price }} ₸</span>
        </div>

        <div class="course-actions">
          <RouterLink v-if="isPurchased" :to="`/course/${course.slug}`" class="btn btn-primary btn-full">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <polygon points="5 3 19 12 5 21 5 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="currentColor"/>
            </svg>
            {{ t('myCourses.continueLearning') }}
          </RouterLink>
          <template v-else>
            <RouterLink :to="`/course/${course.slug}`" class="btn btn-secondary">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M12 16v-4M12 8h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              {{ t('course.details') }}
            </RouterLink>
            <RouterLink :to="`/checkout/${course.slug}`" class="btn btn-primary">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="9" cy="21" r="1" fill="currentColor"/>
                <circle cx="20" cy="21" r="1" fill="currentColor"/>
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              {{ t('course.enroll') }}
            </RouterLink>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import type { Course } from '@/types'
import { useLanguageStore } from '@/stores/language'

const props = defineProps<{
  course: Course
  isPurchased?: boolean
}>()

const languageStore = useLanguageStore()
const currentLanguage = computed(() => languageStore.currentLanguage)
const t = (key: string) => {
  const _ = currentLanguage.value; // Creates reactive dependency
  return languageStore.t(key);
}

const localizedCourse = computed(() => {
  const slug = props.course.slug || ''
  const titleKey = `course.${slug}.title`
  const descriptionKey = `course.${slug}.description`

  return {
    name: languageStore.tCourse(titleKey, props.course.name),
    description: languageStore.tCourse(descriptionKey, props.course.description)
  }
})

const getImageUrl = (thumbnail: any, slug?: string): string => {
  if (thumbnail && typeof thumbnail === 'string') {
    if (thumbnail.startsWith('http') || thumbnail.startsWith('/')) {
      return thumbnail
    }
    return `/${thumbnail.replace(/^\/+/, '')}`
  }

  if (thumbnail?.url) {
    if (thumbnail.url.startsWith('http') || thumbnail.url.startsWith('/')) {
      return thumbnail.url
    }
    return `/${thumbnail.url.replace(/^\/+/, '')}`
  }

  if (slug) {
    const slugName = slug.toLowerCase()
    if (slugName.includes('python')) return '/images/course-python.svg'
    if (slugName.includes('cplusplus') || slugName.includes('c++')) return '/images/course-cplusplus.svg'
    if (slugName.includes('javascript')) return '/images/course-javascript.svg'
    if (slugName.includes('react')) return '/images/course-react.svg'
    if (slugName.includes('vue')) return '/images/course-vuejs.svg'
    if (slugName.includes('unity')) return '/images/course-unity.svg'
  }

  return '/images/course-generic.svg'
}

const handleImageError = (event: Event) => {
  const target = event.target as HTMLImageElement
  const slug = target.dataset.slug
  target.onerror = null
  console.error('Course image failed to load:', target.src)
  if (slug) {
    const fallback = getImageUrl(null, slug)
    target.src = fallback
  } else {
    target.src = '/images/course-generic.svg'
  }
}

const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text
  return text.slice(0, maxLength) + '...'
}
</script>

<style scoped>
.course-card {
  background: var(--color-card-bg);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px var(--color-card-shadow);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px var(--color-card-shadow-hover);
}

.course-image {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-image img {
  transform: scale(1.05);
}

.discount-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3);
}

.course-body {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  flex: 1;
}

.course-title {
  font-size: 1.375rem;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
  line-height: 1.3;
}

.course-description {
  color: var(--color-text-secondary);
  font-size: 0.9375rem;
  line-height: 1.6;
  margin: 0;
  flex: 1;
}

.course-footer {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: auto;
}

.course-price {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.price-amount {
  font-size: 1.75rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.course-actions {
  display: flex;
  gap: 0.75rem;
}

.btn {
  flex: 1;
  padding: 0.75rem 1rem;
  border-radius: 10px;
  text-decoration: none;
  font-weight: 600;
  text-align: center;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-size: 0.9375rem;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: var(--color-card-bg);
  border: 2px solid #667eea;
  color: #667eea;
}

.btn-secondary:hover {
  background: var(--color-bg-secondary);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.btn-full {
  width: 100%;
}

@media (max-width: 640px) {
  .course-image {
    height: 180px;
  }
  
  .course-body {
    padding: 1.25rem;
  }
  
  .course-title {
    font-size: 1.25rem;
  }
  
  .course-description {
    font-size: 0.875rem;
  }
  
  .price-amount {
    font-size: 1.5rem;
  }
  
  .btn {
    padding: 0.625rem 0.875rem;
    font-size: 0.875rem;
  }
  
  .discount-badge {
    top: 0.75rem;
    right: 0.75rem;
    padding: 0.375rem 0.75rem;
    font-size: 0.8125rem;
  }
}

@media (max-width: 380px) {
  .course-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>
