<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NavBar, Empty } from 'vant'
import { getCourses } from '@/services/courses'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'
import type { Course } from '@/types'

defineOptions({ name: 'TeacherView' })
const { t } = useI18n()

const router = useRouter()
const authStore = useAuthStore()

const stats = ref({
  myCourses: 0,
  totalStudents: 0,
  totalRevenue: 0,
  averageRating: 0,
})
const teacherCourses = ref<Course[]>([])
const loading = ref(true)

onMounted(async () => {
  loading.value = true
  try {
    const list = await getCourses()
    teacherCourses.value = list
    stats.value.myCourses = list.length
    stats.value.totalStudents = 0
    stats.value.totalRevenue = 0
    stats.value.averageRating = 0
  } catch {
    teacherCourses.value = []
  } finally {
    loading.value = false
  }
})

function openWebDashboard() {
  const base = import.meta.env.DEV ? 'http://127.0.0.1:8000' : ''
  window.open(`${base}/teacher/dashboard/`, '_blank')
}

function goDetail(slug: string) {
  router.push({ name: 'course-detail', params: { slug } })
}
</script>

<template>
  <div class="teacher">
    <NavBar
      :title="t('teacher.title')"
      left-arrow
      @click-left="router.back()"
      fixed
      placeholder
    />
    <div class="content">
      <p class="teacher-desc">{{ t('teacher.desc') }}</p>
      <!-- Stats -->
      <div class="stats-grid">
        <div class="stat-box">
          <div class="stat-box__icon">☑</div>
          <div class="stat-box__label">{{ t('teacher.myCourses') }}</div>
          <div class="stat-box__value">{{ stats.myCourses }}</div>
        </div>
        <div class="stat-box">
          <div class="stat-box__icon">☑</div>
          <div class="stat-box__label">{{ t('teacher.students') }}</div>
          <div class="stat-box__value">{{ stats.totalStudents.toLocaleString() }}</div>
        </div>
        <div class="stat-box">
          <div class="stat-box__icon">○</div>
          <div class="stat-box__label">{{ t('teacher.revenue') }}</div>
          <div class="stat-box__value">${{ stats.totalRevenue.toLocaleString() }}</div>
        </div>
        <div class="stat-box">
          <div class="stat-box__icon">⭐</div>
          <div class="stat-box__label">{{ t('teacher.rating') }}</div>
          <div class="stat-box__value">{{ stats.averageRating }}</div>
        </div>
      </div>
      <button type="button" class="btn-create" @click="openWebDashboard">
        <span class="btn-create__icon">+</span> {{ t('teacher.createCourse') }}
      </button>
      <h2 class="section-title">{{ t('teacher.myCoursesList') }}</h2>
      <div v-if="loading" class="loading-text">{{ t('home.loading') }}</div>
      <Empty v-else-if="teacherCourses.length === 0" :description="t('teacher.noCourses')" image-size="80" />
      <div v-else class="course-list">
        <div v-for="c in teacherCourses" :key="c.id" class="teacher-course-card">
          <div class="teacher-course-card__main">
            <h3 class="teacher-course-card__title">{{ c.name }}</h3>
            <span class="teacher-course-card__status teacher-course-card__status--published">PUBLISHED</span>
          </div>
          <div class="teacher-course-card__meta">
            <span>☑ 0 {{ t('teacher.studentsCount') }}</span>
            <span>⭐ 0</span>
            <span>☑ 0 {{ t('teacher.videosCount') }}</span>
          </div>
          <button type="button" class="btn-edit" @click="goDetail(c.slug)">{{ t('teacher.viewCourse') }}</button>
        </div>
      </div>
      <p class="web-note">Курс құру және өңдеу веб-нұсқада қолжетімді.</p>
    </div>
  </div>
</template>

<style scoped>
.teacher {
  min-height: 100vh;
  background: var(--edu-bg);
}
.content {
  padding: 20px;
  padding-top: 16px;
}
.teacher-desc {
  font-size: 14px;
  color: var(--edu-text-secondary);
  margin: 0 0 20px 0;
}
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 20px;
}
.stat-box {
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: var(--edu-radius-sm);
  padding: 16px;
  box-shadow: var(--edu-shadow);
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.stat-box__icon {
  width: 36px;
  height: 36px;
  background: var(--edu-primary-soft);
  color: var(--edu-primary);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}
.stat-box__label {
  font-size: 10px;
  font-weight: 600;
  color: var(--edu-text-secondary);
  letter-spacing: 0.02em;
}
.stat-box__value {
  font-size: 20px;
  font-weight: 700;
  color: var(--edu-text);
}
.btn-create {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 20px;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  background: var(--edu-primary);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  margin-bottom: 24px;
  -webkit-tap-highlight-color: transparent;
}
.btn-create__icon {
  font-size: 20px;
  line-height: 1;
}
.section-title {
  font-size: 17px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 12px 0;
}
.loading-text {
  font-size: 14px;
  color: var(--edu-text-secondary);
  margin-bottom: 16px;
}
.course-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.teacher-course-card {
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: var(--edu-radius-sm);
  padding: 16px;
  box-shadow: var(--edu-shadow);
}
.teacher-course-card__main {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}
.teacher-course-card__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.teacher-course-card__status {
  font-size: 11px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 6px;
  flex-shrink: 0;
}
.teacher-course-card__status--published {
  background: #dcfce7;
  color: #16a34a;
}
.teacher-course-card__status--draft {
  background: #ffedd5;
  color: #ea580c;
}
.teacher-course-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: var(--edu-text-secondary);
  margin-bottom: 12px;
}
.btn-edit {
  width: 100%;
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 600;
  color: var(--edu-primary);
  background: #fff;
  border: 2px solid var(--edu-primary-soft);
  border-radius: 10px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.web-note {
  font-size: 12px;
  color: var(--edu-text-secondary);
  margin-top: 20px;
  text-align: center;
}
</style>
