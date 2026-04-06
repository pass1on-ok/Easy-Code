<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Empty, Loading, Image as VanImage, Button, showSuccessToast, showFailToast } from 'vant'
import { getCourseBySlug, enrollCourse, getPurchasedCourses } from '@/services/courses'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'
import type { CourseDetail, Video } from '@/types'

defineOptions({ name: 'CourseDetailView' })
const { t } = useI18n()

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const slug = computed(() => route.params.slug as string)

const detail = ref<CourseDetail | null>(null)
const loading = ref(true)
const actionLoading = ref(false)
const error = ref('')
const isEnrolled = ref(false)
const currentVideoIndex = ref(0)

const isPaid = computed(() => (detail.value?.price ?? 0) > 0)
const hasVideos = computed(() => (detail.value?.videos?.length ?? 0) > 0)
const hasMaterials = computed(() => (detail.value?.materials?.length ?? 0) > 0)
const videos = computed(() => detail.value?.videos ?? [])
const currentVideo = computed(() => videos.value[currentVideoIndex.value] ?? null)

onMounted(async () => {
  try {
    detail.value = await getCourseBySlug(slug.value)
    if (authStore.isAuthenticated) {
      try {
        const purchased = await getPurchasedCourses()
        isEnrolled.value = purchased.some((c: { slug: string }) => c.slug === slug.value)
      } catch {
        isEnrolled.value = false
      }
    }
  } catch (e: any) {
    const d = e.response?.data
    error.value = d?.message || d?.error || e.message || t('detail.loadFail')
  } finally {
    loading.value = false
  }
})

function setLesson(index: number) {
  currentVideoIndex.value = index
}

async function onEnroll() {
  if (!authStore.isAuthenticated) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  actionLoading.value = true
  try {
    await enrollCourse(slug.value)
    isEnrolled.value = true
    showSuccessToast(t('detail.enrollSuccess'))
  } catch (e: any) {
    const d = e.response?.data
    const msg = d?.message || d?.error || e.message || t('detail.enrollFail')
    showFailToast(msg)
  } finally {
    actionLoading.value = false
  }
}

function onPurchase() {
  if (!authStore.isAuthenticated) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  router.push({ name: 'checkout', params: { slug: slug.value } })
}
</script>

<template>
  <div class="detail">
    <Loading v-if="loading" type="spinner" vertical class="loading-full">{{ t('detail.loading') }}</Loading>
    <template v-else-if="error">
      <div class="content"><Empty :description="error" image-size="80" /></div>
    </template>
    <template v-else-if="detail">
      <!-- 已报名：第二张图布局 - 顶栏 + 课时标题 + 视频 + 任务卡片 -->
      <template v-if="isEnrolled && hasVideos">
        <header class="lesson-header">
          <button type="button" class="lesson-header__back" :aria-label="t('detail.back')" @click="router.back()">
            <span class="lesson-header__back-icon">≡</span>
          </button>
          <button type="button" class="lesson-header__ai-btn" @click="router.push({ name: 'ai-chat' })">
            <span class="lesson-header__ai-icon">★</span> {{ t('detail.aiTeacher') }}
          </button>
        </header>
        <div class="lesson-content">
          <h1 class="lesson-title">
            {{ t('detail.lesson') }} {{ currentVideo?.serial_number ?? 1 }}. {{ currentVideo?.title ?? detail.name }}
          </h1>
          <div class="video-wrap">
            <video
              v-if="currentVideo?.video_url"
              :src="currentVideo.video_url"
              class="video-player"
              controls
              playsinline
              preload="metadata"
            />
            <div v-else class="video-placeholder">
              <div class="video-placeholder__icon">▶</div>
              <p class="video-placeholder__text">{{ t('detail.videoPlay') }}</p>
            </div>
          </div>
          <div class="task-card">
            <div class="task-card__head">
              <span class="task-card__icon">📖</span>
              <span class="task-card__label">{{ currentVideo?.serial_number ?? 1 }}-{{ t('detail.taskLabel') }}</span>
            </div>
            <p class="task-card__prompt">{{ t('detail.taskPrompt') }}</p>
            <pre class="task-card__code"><code># Код мысалы
name = "Студент"
age = 18
is_student = True</code></pre>
            <p class="task-card__desc">
              {{ currentVideo?.description || t('detail.taskDescDefault') }}
            </p>
          </div>
          <div v-if="videos.length > 1" class="lesson-list">
            <button
              v-for="(v, i) in videos"
              :key="v.id"
              type="button"
              class="lesson-list__item"
              :class="{ active: currentVideoIndex === i }"
              @click="setLesson(i)"
            >
              {{ t('detail.lesson') }} {{ v.serial_number }}. {{ v.title }}
            </button>
          </div>
        </div>
      </template>

      <!-- 已报名但无视频 -->
      <template v-else-if="isEnrolled">
        <header class="lesson-header">
          <button type="button" class="lesson-header__back" @click="router.back()"><span class="lesson-header__back-icon">←</span></button>
          <span class="lesson-header__title">{{ detail.name }}</span>
        </header>
        <div class="content">
          <Empty :description="t('detail.noVideos')" image-size="80" />
        </div>
      </template>

      <!-- 未报名：概览 + 报名/购买 -->
      <template v-else>
        <header class="lesson-header lesson-header--simple">
          <button type="button" class="lesson-header__back" @click="router.back()"><span class="lesson-header__back-icon">←</span></button>
          <span class="lesson-header__title">{{ detail.name }}</span>
        </header>
        <div class="content">
          <div class="course-hero" v-if="detail.thumbnail">
            <VanImage :src="detail.thumbnail" fit="cover" width="100%" height="180" radius="12" />
          </div>
          <div v-else class="course-hero course-hero--placeholder" />
          <div class="course-info">
            <h1 class="course-name">{{ detail.name }}</h1>
            <p class="course-desc" v-if="detail.description">{{ detail.description }}</p>
            <div class="course-meta">
              <span class="price">{{ detail.price }} 〒</span>
              <span v-if="detail.length" class="length">{{ detail.length }} {{ t('detail.hours') }}</span>
            </div>
            <template v-if="hasVideos">
              <h3 class="block-title">{{ t('detail.videos') }}</h3>
              <div class="video-list">
                <div v-for="v in detail.videos" :key="v.id" class="video-item">
                  <span class="video-num">{{ v.serial_number }}</span>
                  <span class="video-title">{{ v.title }}</span>
                </div>
              </div>
            </template>
            <div class="actions">
              <Button
                v-if="isPaid"
                type="primary"
                block
                round
                @click="onPurchase"
                class="action-btn"
              >
                {{ t('detail.buy') }}
              </Button>
              <Button
                v-else
                type="primary"
                block
                round
                :loading="actionLoading"
                @click="onEnroll"
                class="action-btn"
              >
                {{ t('detail.enroll') }}
              </Button>
            </div>
          </div>
        </div>
      </template>
    </template>
  </div>
</template>

<style scoped>
.detail {
  min-height: 100vh;
  background: var(--edu-bg);
}
.loading-full {
  padding: 60px 20px;
}
.content {
  padding: 20px;
  padding-bottom: 40px;
}

/* 学习视图顶栏 */
.lesson-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: linear-gradient(135deg, var(--header-from) 0%, var(--header-to) 100%);
  color: #fff;
  min-height: 52px;
}
.lesson-header--simple {
  background: var(--edu-bg-card);
  color: var(--edu-text);
  border-bottom: 1px solid var(--edu-border);
}
.lesson-header__back {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 10px;
  color: inherit;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.lesson-header--simple .lesson-header__back {
  background: var(--edu-primary-soft);
  color: var(--edu-primary);
}
.lesson-header__back-icon {
  font-size: 20px;
  line-height: 1;
}
.lesson-header__title {
  font-size: 16px;
  font-weight: 600;
  flex: 1;
  text-align: center;
  margin: 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.lesson-header__ai-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: #ec4899;
  border: none;
  border-radius: 999px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.lesson-header__ai-icon {
  font-size: 16px;
}

/* 课时内容 */
.lesson-content {
  padding: 20px 16px 40px;
}
.lesson-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 16px 0;
  line-height: 1.4;
}
.video-wrap {
  border-radius: 12px;
  overflow: hidden;
  background: #000;
  margin-bottom: 20px;
  aspect-ratio: 16/9;
}
.video-player {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.video-placeholder {
  width: 100%;
  height: 100%;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--edu-border);
  color: var(--edu-text-secondary);
}
.video-placeholder__icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: var(--edu-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-bottom: 12px;
}
.video-placeholder__text {
  font-size: 14px;
  margin: 0;
}

/* 任务卡片 */
.task-card {
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: var(--edu-shadow);
}
.task-card__head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
.task-card__icon {
  font-size: 20px;
}
.task-card__label {
  font-size: 13px;
  font-weight: 700;
  color: var(--edu-primary);
}
.task-card__prompt {
  font-size: 14px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0 0 10px 0;
}
.task-card__code {
  background: #1e1e2e;
  color: #cdd6f4;
  border-radius: 10px;
  padding: 14px;
  font-size: 13px;
  line-height: 1.5;
  overflow-x: auto;
  margin: 0 0 14px 0;
  font-family: ui-monospace, 'Cascadia Code', monospace;
}
.task-card__code code {
  white-space: pre;
}
.task-card__desc {
  font-size: 14px;
  color: var(--edu-text-secondary);
  line-height: 1.6;
  margin: 0;
}

/* 课时列表 */
.lesson-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.lesson-list__item {
  width: 100%;
  padding: 12px 16px;
  text-align: left;
  font-size: 14px;
  color: var(--edu-text);
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: 10px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.lesson-list__item.active {
  border-color: var(--edu-primary);
  background: var(--edu-primary-soft);
  color: var(--edu-primary);
  font-weight: 600;
}

/* 未报名样式 */
.course-hero {
  margin-bottom: 16px;
  border-radius: 12px;
  overflow: hidden;
}
.course-hero--placeholder {
  height: 180px;
  background: linear-gradient(135deg, var(--edu-primary-soft) 0%, var(--edu-primary) 100%);
}
.course-info {
  background: var(--edu-bg-card);
  border-radius: var(--edu-radius);
  padding: 20px;
  box-shadow: var(--edu-shadow);
}
.course-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 12px 0;
}
.course-desc {
  font-size: 14px;
  color: var(--edu-text-secondary);
  line-height: 1.5;
  margin: 0 0 16px 0;
}
.course-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  font-size: 14px;
}
.course-meta .price {
  font-weight: 700;
  color: var(--edu-primary);
}
.course-meta .length {
  color: var(--edu-text-secondary);
}
.block-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0 0 10px 0;
}
.video-list {
  margin-bottom: 16px;
}
.video-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: var(--edu-bg);
  border-radius: 8px;
  margin-bottom: 6px;
}
.video-num {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--edu-primary-soft);
  color: var(--edu-primary);
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}
.video-title {
  flex: 1;
  font-size: 14px;
  color: var(--edu-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.actions {
  margin-top: 24px;
}
.action-btn :deep(.van-button--primary) {
  background: var(--edu-primary);
  border-color: var(--edu-primary);
}
</style>
