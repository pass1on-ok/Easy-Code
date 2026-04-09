<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Empty, Loading, Image as VanImage, Button, showSuccessToast, showFailToast } from 'vant'
import { getCourseBySlug, enrollCourse, getPurchasedCourses } from '@/services/courses'
import { getQuiz, getCourseProgress } from '@/services/quiz'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'
import type { CourseDetail, Video, CourseProgress } from '@/types'
import { jsPDF } from 'jspdf'

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

const progress = ref<CourseProgress | null>(null)
const progressLoading = ref(false)

function progressStorageKey() {
  const u = authStore.user?.username || 'guest'
  return `easycode.lessonProgress.${u}.${slug.value}`
}

function loadCompletedVideoIds(): number[] {
  try {
    const raw = localStorage.getItem(progressStorageKey())
    if (!raw) return []
    const parsed = JSON.parse(raw)
    if (!Array.isArray(parsed)) return []
    return parsed.filter((x) => typeof x === 'number')
  } catch {
    return []
  }
}

const completedVideoIds = ref<number[]>(loadCompletedVideoIds())
const completedSet = computed(() => new Set(completedVideoIds.value))

// Quiz pass status per video: videoId -> { hasQuiz, passed }
const quizStatusMap = ref<Map<number, { hasQuiz: boolean; passed: boolean }>>(new Map())

async function loadQuizStatusForVideo(videoId: number) {
  if (!authStore.isAuthenticated) return
  try {
    const quiz = await getQuiz(videoId)
    quizStatusMap.value = new Map(quizStatusMap.value).set(videoId, {
      hasQuiz: quiz.questions.length > 0,
      passed: quiz.passed ?? false,
    })
  } catch {
    // ignore — don't block the user if quiz status can't be loaded
  }
}

// True only when current lesson has no quiz, or the quiz has been passed
const canCompleteCurrentLesson = computed(() => {
  const v = currentVideo.value
  if (!v) return false
  const status = quizStatusMap.value.get(v.id)
  if (status === undefined) return false // still loading
  if (!status.hasQuiz) return true // no quiz required
  return status.passed
})

// Whether quiz is required but not yet passed for the current lesson
const currentLessonNeedsQuiz = computed(() => {
  const v = currentVideo.value
  if (!v) return false
  const status = quizStatusMap.value.get(v.id)
  return !!status?.hasQuiz && !status.passed
})

const isCourseCompleted = computed(() => {
  const total = videos.value.length
  if (total === 0) return false
  return videos.value.every((v) => completedSet.value.has(v.id))
})

function saveCompletedVideoIds() {
  localStorage.setItem(progressStorageKey(), JSON.stringify(completedVideoIds.value))
}

function isLessonCompleted(video: Video) {
  return completedSet.value.has(video.id)
}

function markCurrentLessonCompleted() {
  const v = currentVideo.value
  if (!v) return
  if (completedSet.value.has(v.id)) return
  const status = quizStatusMap.value.get(v.id)
  if (status?.hasQuiz && !status.passed) {
    showFailToast(t('detail.mustPassQuizFirst'))
    return
  }
  completedVideoIds.value = [...completedVideoIds.value, v.id]
  saveCompletedVideoIds()
  showSuccessToast(t('detail.lessonCompletedToast'))
}

function safeFilename(name: string) {
  return name.replace(/[\\/:*?"<>|]+/g, '-').trim()
}

function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}

function formatDate(d: Date) {
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}

function randomCertId() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  let out = 'EC-'
  for (let i = 0; i < 8; i++) out += chars[Math.floor(Math.random() * chars.length)]
  return out
}

function uint8ToBase64(u8: Uint8Array) {
  let binary = ''
  const chunk = 0x8000
  for (let i = 0; i < u8.length; i += chunk) {
    binary += String.fromCharCode(...u8.subarray(i, i + chunk))
  }
  return btoa(binary)
}

let fontLoaded = false
async function ensureCyrillicFont(doc: jsPDF) {
  if (fontLoaded) return
  // Noto Sans supports Kazakh/Cyrillic well. We load it once and reuse.
  // Cached in-memory (page reload will re-fetch).
  const res = await fetch('https://fonts.gstatic.com/s/notosans/v36/o-0IIpQlx3QUlC5A4PNr5TRA.woff2')
  if (!res.ok) throw new Error('Font download failed')
  // jsPDF works best with TTF; WOFF2 is not directly supported.
  // Fallback: use a CDN-hosted TTF.
  // (If you later want full offline support, we can bundle the TTF locally.)
  const resTtf = await fetch('https://fonts.gstatic.com/s/notosans/v36/o-0IIpQlx3QUlC5A4PNb4g.ttf')
  if (!resTtf.ok) throw new Error('Font download failed')
  const buf = await resTtf.arrayBuffer()
  const base64 = uint8ToBase64(new Uint8Array(buf))
  doc.addFileToVFS('NotoSans-Regular.ttf', base64)
  doc.addFont('NotoSans-Regular.ttf', 'NotoSans', 'normal')
  fontLoaded = true
}

const certificateLoading = ref(false)

async function onFinishCourse() {
  if (!isCourseCompleted.value) {
    showFailToast(t('detail.finishCourseNotReady'))
    return
  }
  if (certificateLoading.value) return

  // Re-fetch progress from server to ensure all quizzes are actually passed
  try {
    progress.value = await getCourseProgress(slug.value)
  } catch {
    // ignore — use cached progress
  }
  if (progress.value && progress.value.tests_total > 0 && progress.value.tests_passed < progress.value.tests_total) {
    showFailToast(t('detail.finishCourseNeedAllQuizzes'))
    return
  }
  const courseName = detail.value?.name || slug.value
  const username = authStore.user?.username || 'Student'
  const fullName = [authStore.user?.first_name, authStore.user?.last_name].filter(Boolean).join(' ').trim()
  const displayName = fullName || username
  const now = new Date()
  const certId = randomCertId()

  certificateLoading.value = true
  try {
    // Landscape A4 for a more "certificate-like" look
    const doc = new jsPDF({ unit: 'pt', format: 'a4', orientation: 'landscape' })
    await ensureCyrillicFont(doc)
    doc.setFont('NotoSans', 'normal')

    const pageWidth = doc.internal.pageSize.getWidth()
    const pageHeight = doc.internal.pageSize.getHeight()

    // Background frame
    doc.setDrawColor(22, 163, 74)
    doc.setLineWidth(3)
    doc.roundedRect(26, 26, pageWidth - 52, pageHeight - 52, 14, 14)
    doc.setDrawColor(187, 247, 208)
    doc.setLineWidth(1)
    doc.roundedRect(42, 42, pageWidth - 84, pageHeight - 84, 12, 12)

    // Brand
    doc.setTextColor(22, 163, 74)
    doc.setFontSize(16)
    doc.text('Easy Code', 60, 85)

    // Title
    doc.setTextColor(17, 24, 39)
    doc.setFontSize(34)
    doc.text(t('detail.certificateTitle'), pageWidth / 2, 125, { align: 'center' })
    doc.setTextColor(75, 85, 99)
    doc.setFontSize(13)
    doc.text(t('detail.certificateSubtitle'), pageWidth / 2, 155, { align: 'center' })

    // Recipient name
    doc.setTextColor(17, 24, 39)
    doc.setFontSize(28)
    doc.text(displayName, pageWidth / 2, 230, { align: 'center' })
    doc.setDrawColor(209, 213, 219)
    doc.setLineWidth(1)
    doc.line(pageWidth / 2 - 220, 245, pageWidth / 2 + 220, 245)

    // Course name highlight
    doc.setTextColor(22, 163, 74)
    doc.setFontSize(18)
    doc.text(courseName, pageWidth / 2, 290, { align: 'center' })

    // Body text
    doc.setTextColor(55, 65, 81)
    doc.setFontSize(14)
    const bodyLines = doc.splitTextToSize(t('detail.certificateBody', { course: courseName }), 560)
    doc.text(bodyLines, pageWidth / 2, 330, { align: 'center' })

    // Footer: date + certificate id
    doc.setTextColor(75, 85, 99)
    doc.setFontSize(12)
    doc.text(`${t('detail.certificateDate')}: ${formatDate(now)}`, 60, pageHeight - 70)
    doc.text(`ID: ${certId}`, pageWidth - 60, pageHeight - 70, { align: 'right' })

    // Signature placeholders
    doc.setDrawColor(156, 163, 175)
    doc.setLineWidth(1)
    doc.line(120, pageHeight - 120, 320, pageHeight - 120)
    doc.line(pageWidth - 320, pageHeight - 120, pageWidth - 120, pageHeight - 120)
    doc.setTextColor(107, 114, 128)
    doc.setFontSize(11)
    doc.text(t('detail.certificateSignTeacher'), 220, pageHeight - 98, { align: 'center' })
    doc.text(t('detail.certificateSignPlatform'), pageWidth - 220, pageHeight - 98, { align: 'center' })

    const blob = doc.output('blob')
    const filename = `${safeFilename(courseName)}-certificate-${formatDate(now)}.pdf`
    downloadBlob(blob, filename)
    showSuccessToast(t('detail.certificateDownloadedToast'))
  } catch {
    showFailToast(t('detail.certificateFail'))
  } finally {
    certificateLoading.value = false
  }
}

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

    if (authStore.isAuthenticated) {
      progressLoading.value = true
      try {
        progress.value = await getCourseProgress(slug.value)
      } catch {
        progress.value = null
      } finally {
        progressLoading.value = false
      }
      // Load quiz status for the first video
      if (videos.value.length > 0) {
        loadQuizStatusForVideo(videos.value[0].id)
      }
    }
  } catch (e: any) {
    const d = e.response?.data
    error.value = d?.message || d?.error || e.message || t('detail.loadFail')
  } finally {
    loading.value = false
  }
})

// When user switches to a different lesson, load its quiz status
watch(currentVideoIndex, (newIndex) => {
  const v = videos.value[newIndex]
  if (v) {
    loadQuizStatusForVideo(v.id)
  }
})

function setLesson(index: number) {
  currentVideoIndex.value = index
}

function goBack() {
  // router.back() may do nothing if user opened page directly
  if (window.history.length > 1) {
    router.back()
    return
  }
  router.push({ name: 'courses' })
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

function goToQuiz() {
  const v = currentVideo.value
  if (!v) return
  router.push({ name: 'quiz', params: { videoId: v.id } })
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
          <button type="button" class="lesson-header__back" :aria-label="t('detail.back')" @click="goBack">
            <span class="lesson-header__back-icon">←</span>
          </button>
          <button type="button" class="lesson-header__ai-btn" @click="router.push({ name: 'ai-chat' })">
            <span class="lesson-header__ai-icon">★</span> {{ t('detail.aiTeacher') }}
          </button>
        </header>
        <div class="lesson-content">
          <div v-if="progress || progressLoading" class="progress-card">
            <div class="progress-card__title">{{ t('detail.yourProgress') }}</div>
            <div v-if="progressLoading" class="progress-card__loading">{{ t('home.loading') }}</div>
            <template v-else-if="progress">
              <div class="progress-row">
                <span class="progress-label">{{ t('detail.testsCompleted') }}</span>
                <span class="progress-value">{{ progress.tests_completed }} / {{ progress.tests_total }}</span>
              </div>
              <div class="progress-row">
                <span class="progress-label">{{ t('detail.passRate') }}</span>
                <span class="progress-value">{{ progress.tests_passed }} / {{ progress.tests_total }}</span>
              </div>
            </template>
          </div>

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
            <div class="task-card__actions">
              <button type="button" class="test-btn" @click="goToQuiz">
                {{ t('detail.submitTest') }}
              </button>
              <button
                type="button"
                class="complete-btn"
                :disabled="!!currentVideo && (isLessonCompleted(currentVideo) || !canCompleteCurrentLesson)"
                @click="markCurrentLessonCompleted"
              >
                <span v-if="currentVideo && isLessonCompleted(currentVideo)" class="complete-btn__check">✓</span>
                {{ currentVideo && isLessonCompleted(currentVideo) ? t('detail.lessonCompleted') : t('detail.completeLesson') }}
              </button>
            </div>
            <p v-if="currentLessonNeedsQuiz" class="quiz-required-hint">
              {{ t('detail.mustPassQuizFirst') }}
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
              <span class="lesson-list__text">{{ t('detail.lesson') }} {{ v.serial_number }}. {{ v.title }}</span>
              <span v-if="isLessonCompleted(v)" class="lesson-list__done" aria-label="Completed">✓</span>
            </button>
          </div>

          <div v-if="isCourseCompleted" class="finish-course">
            <button type="button" class="finish-course__btn" :disabled="certificateLoading" @click="onFinishCourse">
              {{ t('detail.finishCourse') }}
            </button>
          </div>
        </div>
      </template>

      <!-- 已报名但无视频 -->
      <template v-else-if="isEnrolled">
        <header class="lesson-header">
          <button type="button" class="lesson-header__back" @click="goBack"><span class="lesson-header__back-icon">←</span></button>
          <span class="lesson-header__title">{{ detail.name }}</span>
        </header>
        <div class="content">
          <Empty :description="t('detail.noVideos')" image-size="80" />
        </div>
      </template>

      <!-- 未报名：概览 + 报名/购买 -->
      <template v-else>
        <header class="lesson-header lesson-header--simple">
          <button type="button" class="lesson-header__back" @click="goBack"><span class="lesson-header__back-icon">←</span></button>
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

.progress-card {
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: 12px;
  padding: 14px;
  box-shadow: var(--edu-shadow);
  margin-bottom: 14px;
}
.progress-card__title {
  font-size: 14px;
  font-weight: 800;
  color: var(--edu-text);
  margin-bottom: 10px;
}
.progress-card__loading {
  font-size: 13px;
  color: var(--edu-text-secondary);
}
.progress-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 6px 0;
}
.progress-label {
  font-size: 13px;
  color: var(--edu-text-secondary);
}
.progress-value {
  font-size: 13px;
  font-weight: 700;
  color: var(--edu-text);
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
.task-card__actions {
  margin-top: 14px;
  display: flex;
  justify-content: space-between;
  gap: 10px;
}
.test-btn {
  flex: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid #ddd6fe;
  background: #f5f3ff;
  color: #6d28d9;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.test-btn:active {
  transform: scale(0.99);
}
.complete-btn {
  flex: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid var(--edu-border);
  background: var(--edu-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.complete-btn:disabled {
  cursor: default;
  background: #22c55e;
  border-color: #16a34a;
  opacity: 0.9;
}
.complete-btn__check {
  font-size: 16px;
  line-height: 1;
}
.quiz-required-hint {
  margin: 8px 0 0 0;
  font-size: 12px;
  color: #f59e0b;
  text-align: center;
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
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}
.lesson-list__text {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.lesson-list__done {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  border-radius: 999px;
  background: #dcfce7;
  color: #16a34a;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 14px;
  border: 1px solid #86efac;
}

.finish-course {
  position: sticky;
  bottom: 14px;
  margin-top: 18px;
  padding-top: 10px;
}
.finish-course__btn {
  width: 100%;
  padding: 14px 16px;
  border-radius: 12px;
  border: none;
  background: #16a34a;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
  box-shadow: 0 10px 18px rgba(22, 163, 74, 0.22);
}
.finish-course__btn:disabled {
  opacity: 0.7;
  cursor: default;
}
.finish-course__btn:active {
  transform: scale(0.99);
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
