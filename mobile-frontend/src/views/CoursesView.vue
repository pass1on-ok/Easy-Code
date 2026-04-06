<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Empty, Loading, Image as VanImage } from 'vant'
import { getCourses, getPurchasedCourses } from '@/services/courses'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'
import type { Course } from '@/types'
import type { PurchasedCourse } from '@/types'

defineOptions({ name: 'CoursesView' })
const { t } = useI18n()

const router = useRouter()
const authStore = useAuthStore()
const list = ref<Course[]>([])
const purchasedSlugs = ref<Set<string>>(new Set())
const loading = ref(true)
const error = ref('')

const displayName = computed(() => {
  const u = authStore.user
  if (!u) return 'Easy Code'
  if (u.first_name || u.last_name) return [u.first_name, u.last_name].filter(Boolean).join(' ')
  return u.username
})

onMounted(async () => {
  try {
    list.value = await getCourses()
    if (authStore.isAuthenticated) {
      try {
        const purchased: PurchasedCourse[] = await getPurchasedCourses()
        purchasedSlugs.value = new Set(purchased.map((c) => c.slug))
      } catch {
        purchasedSlugs.value = new Set()
      }
    }
  } catch (e: any) {
    error.value = e.response?.data?.error || e.message || t('courses.loadFail')
  } finally {
    loading.value = false
  }
})

function isEnrolled(slug: string) {
  return purchasedSlugs.value.has(slug)
}

function goDetail(slug: string) {
  router.push({ name: 'course-detail', params: { slug } })
}

// 进度占位：后端无进度接口时显示 0，或可根据课程视频数模拟
function getProgress(c: Course) {
  const total = c.length ? Number(c.length) * 4 : 51
  return { completed: 0, total }
}
</script>

<template>
  <div class="courses">
    <!-- 顶部横幅 -->
    <section class="courses-banner">
      <div class="courses-banner__bg" />
      <div class="courses-banner__overlay" />
      <div class="courses-banner__content">
        <h1 class="courses-banner__title">Easy Code</h1>
        <p class="courses-banner__subtitle">{{ t('courses.bannerSubtitle') }}</p>
      </div>
    </section>

    <!-- 欢迎 / 个人区 -->
    <section class="welcome-block">
      <div class="welcome-block__avatar">
        <span class="welcome-block__avatar-text">{{ displayName.charAt(0).toUpperCase() }}</span>
      </div>
      <h2 class="welcome-block__name">{{ displayName }}</h2>
      <p class="welcome-block__text">
        {{ t('courses.welcomeText') }}
      </p>
    </section>

    <!-- 我们的课程 -->
    <section class="content">
      <h2 class="section-head">
        <span class="section-head__icon">📚</span>
        {{ t('courses.ourCourses') }}
      </h2>
      <Loading v-if="loading" type="spinner" vertical>{{ t('home.loading') }}</Loading>
      <template v-else-if="error">
        <Empty :description="error" image-size="80" />
      </template>
      <template v-else-if="list.length === 0">
        <Empty :description="t('home.noCourses')" image-size="80" />
      </template>
      <div v-else class="course-list">
        <article v-for="c in list" :key="c.id" class="course-card" @click="goDetail(c.slug)">
          <div class="course-card__thumb">
            <VanImage v-if="c.thumbnail" :src="c.thumbnail" fit="cover" width="100%" height="140" radius="12" />
            <div v-else class="thumb-placeholder" />
          </div>
          <div class="course-card__body">
            <h3 class="course-card__title">{{ c.name }}</h3>
            <template v-if="isEnrolled(c.slug)">
              <div class="course-card__progress">
                <span class="course-card__progress-text">{{ getProgress(c).completed }} / {{ getProgress(c).total }} {{ t('courses.lessons') }}</span>
                <span class="course-card__progress-pct">0%</span>
              </div>
              <div class="progress-bar">
                <div class="progress-bar__fill" style="width: 0%" />
              </div>
            </template>
            <template v-else>
              <p class="course-card__desc">{{ (c.description || '').slice(0, 60) }}{{ (c.description && c.description.length > 60) ? '...' : '' }}</p>
              <div class="course-card__meta">
                <span class="price">{{ c.discount ? Math.round(c.price * (1 - c.discount / 100)) : c.price }} 〒</span>
              </div>
              <div class="course-card__actions">
                <button type="button" class="btn-details" @click.stop="goDetail(c.slug)">{{ t('courses.details') }}</button>
                <button type="button" class="btn-enroll" @click.stop="goDetail(c.slug)">{{ t('courses.enroll') }}</button>
              </div>
            </template>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<style scoped>
.courses {
  min-height: 100vh;
  background: var(--edu-bg);
  padding-bottom: 24px;
}

/* 横幅 */
.courses-banner {
  position: relative;
  height: 160px;
  overflow: hidden;
}
.courses-banner__bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #5f52b8 0%, #6b5fc7 100%);
  background-image: url('https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=800'), linear-gradient(135deg, #5f52b8 0%, #6b5fc7 100%);
  background-size: cover;
  background-position: center;
}
.courses-banner__overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(95, 82, 184, 0.5) 0%, rgba(107, 95, 199, 0.7) 100%);
}
.courses-banner__content {
  position: relative;
  z-index: 1;
  padding: 32px 20px 24px;
  color: #fff;
  text-align: center;
}
.courses-banner__title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 4px 0;
  letter-spacing: -0.02em;
  text-shadow: 0 1px 3px rgba(0,0,0,0.2);
}
.courses-banner__subtitle {
  font-size: 14px;
  font-weight: 600;
  margin: 0;
  opacity: 0.95;
  letter-spacing: 0.02em;
}

/* 欢迎区 */
.welcome-block {
  text-align: center;
  padding: 24px 20px;
  background: var(--edu-bg-card);
  margin: -24px 16px 0;
  border-radius: var(--edu-radius);
  box-shadow: var(--edu-shadow);
  position: relative;
  z-index: 2;
}
.welcome-block__avatar {
  width: 72px;
  height: 72px;
  margin: 0 auto 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--edu-primary-soft) 0%, var(--edu-primary) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.welcome-block__avatar-text {
  font-size: 28px;
  font-weight: 700;
  color: var(--edu-primary);
}
.welcome-block__name {
  font-size: 18px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 8px 0;
}
.welcome-block__text {
  font-size: 13px;
  color: var(--edu-text-secondary);
  line-height: 1.5;
  margin: 0;
}

/* 课程列表 */
.content {
  padding: 20px 16px;
}
.section-head {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 16px 0;
}
.section-head__icon {
  font-size: 22px;
}
.course-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.course-card {
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: var(--edu-radius);
  overflow: hidden;
  box-shadow: var(--edu-shadow);
  text-align: left;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.course-card__thumb {
  position: relative;
  height: 140px;
  overflow: hidden;
  background: linear-gradient(135deg, var(--edu-primary-soft) 0%, var(--edu-primary) 50%);
}
.course-card__thumb :deep(.van-image) {
  width: 100%;
  height: 100%;
  display: block;
}
.course-card__thumb :deep(.van-image__img) {
  object-fit: cover;
  width: 100%;
  height: 100%;
}
.thumb-placeholder {
  width: 100%;
  height: 100%;
  background: var(--edu-primary-soft);
}
.course-card__body {
  padding: 16px;
}
.course-card__title {
  font-size: 16px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.course-card__desc {
  font-size: 13px;
  color: var(--edu-text-secondary);
  margin: 0 0 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.course-card__progress {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  font-size: 13px;
  color: var(--edu-text-secondary);
}
.course-card__progress-pct {
  font-weight: 600;
  color: var(--edu-primary);
}
.progress-bar {
  height: 6px;
  background: var(--edu-border);
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 12px;
}
.progress-bar__fill {
  height: 100%;
  background: var(--edu-primary);
  border-radius: 3px;
  transition: width 0.3s;
}
.course-card__meta {
  margin-bottom: 12px;
}
.course-card__meta .price {
  font-size: 15px;
  font-weight: 700;
  color: var(--edu-primary);
}
.course-card__actions {
  display: flex;
  gap: 10px;
}
.btn-details {
  flex: 1;
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
.btn-enroll {
  flex: 1;
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: var(--edu-primary);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
</style>
