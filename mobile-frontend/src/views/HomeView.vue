<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Loading, Empty } from 'vant'
import { getCourses, getPurchasedCourses } from '@/services/courses'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'
import type { Course } from '@/types'
import type { PurchasedCourse } from '@/types'

defineOptions({ name: 'HomeView' })
const { t } = useI18n()

const router = useRouter()
const authStore = useAuthStore()
const heroImgError = ref(false)
const courses = ref<Course[]>([])
const coursesLoading = ref(true)
const coursesError = ref('')
const myCourses = ref<PurchasedCourse[]>([])
const myCoursesLoading = ref(false)

const isLoggedIn = computed(() => authStore.isAuthenticated)

onMounted(async () => {
  try {
    courses.value = await getCourses()
  } catch (e: any) {
    coursesError.value = e.response?.data?.error || e.message || t('home.loadCoursesFail')
  } finally {
    coursesLoading.value = false
  }
  if (authStore.isAuthenticated) {
    myCoursesLoading.value = true
    try {
      myCourses.value = await getPurchasedCourses()
    } catch {
      myCourses.value = []
    } finally {
      myCoursesLoading.value = false
    }
  }
})

function goToCourses() {
  router.push({ name: 'courses' })
}

function goToAIChat() {
  router.push({ name: 'ai-chat' })
}

function goDetail(slug: string) {
  router.push({ name: 'course-detail', params: { slug } })
}

function onImgError() {
  heroImgError.value = true
}
</script>

<template>
  <div class="home">
    <div class="content">
      <!-- Hero: Learn to Code / Build Your Future -->
      <section class="hero">
        <div class="hero__bg">
          <img v-if="!heroImgError" src="/easy-code-hero.png" alt="" class="hero__img" @error="onImgError" />
          <div v-else class="hero__img-placeholder" />
          <div class="hero__overlay" />
        </div>
        <div class="hero__content">
          <h1 class="hero__title">{{ t('home.heroTitle') }}</h1>
          <p class="hero__subtitle">{{ t('home.heroSubtitle') }}</p>
          <p class="hero__desc">
            {{ t('home.heroDesc') }}
          </p>
          <div class="hero__actions">
            <button type="button" class="hero-btn hero-btn--primary" @click="goToCourses">
              <span class="hero-btn__icon">📄</span> {{ t('home.btnStart') }}
            </button>
            <button type="button" class="hero-btn hero-btn--outline" @click="goToAIChat">
              <span class="hero-btn__icon hero-btn__icon--ai">🤖</span> {{ t('home.btnAITeacher') }}
            </button>
          </div>
        </div>
      </section>

      <!-- Why Choose Easy Code -->
      <section class="why-section">
        <h2 class="why-title">{{ t('home.whyTitle') }}</h2>
        <div class="why-grid">
          <div class="why-card">
            <div class="why-card__icon">🎓</div>
            <h3 class="why-card__title">{{ t('home.whyExpert') }}</h3>
            <p class="why-card__text">{{ t('home.whyExpertDesc') }}</p>
          </div>
          <div class="why-card">
            <div class="why-card__icon">⏱</div>
            <h3 class="why-card__title">{{ t('home.whyPace') }}</h3>
            <p class="why-card__text">{{ t('home.whyPaceDesc') }}</p>
          </div>
          <div class="why-card">
            <div class="why-card__icon">📜</div>
            <h3 class="why-card__title">{{ t('home.whyCert') }}</h3>
            <p class="why-card__text">{{ t('home.whyCertDesc') }}</p>
          </div>
          <div class="why-card">
            <div class="why-card__icon">👥</div>
            <h3 class="why-card__title">{{ t('home.whyCommunity') }}</h3>
            <p class="why-card__text">{{ t('home.whyCommunityDesc') }}</p>
          </div>
        </div>
      </section>

      <!-- 已登录：展示「我们的课程」= 已购课程，绿色卡片点击进入课程 -->
      <section v-if="isLoggedIn" class="courses-section">
        <h2 class="subsection-title">{{ t('home.myCourses') }}</h2>
        <Loading v-if="myCoursesLoading" type="spinner" vertical>{{ t('home.loading') }}</Loading>
        <Empty v-else-if="myCourses.length === 0" :description="t('home.noCoursesYet')" image-size="80" />
        <div v-else class="course-list">
          <button
            v-for="c in myCourses"
            :key="c.id"
            type="button"
            class="course-card course-card--active"
            @click="goDetail(c.slug)"
          >
            <div class="course-card__thumb">
              <img v-if="c.thumbnail" :src="c.thumbnail" alt="" class="thumb-img" />
              <div v-else class="thumb-placeholder" />
            </div>
            <div class="course-card__body">
              <span class="course-card__badge">{{ t('home.badgeActive') }}</span>
              <h3 class="course-card__title">{{ c.name }}</h3>
              <p class="course-card__desc">{{ (c.description || '').slice(0, 50) }}{{ (c.description && c.description.length > 50) ? '...' : '' }}</p>
            </div>
            <span class="course-card__arrow">→</span>
          </button>
        </div>
        <button type="button" class="link-all" @click="goToCourses">{{ t('home.allCourses') }}</button>
      </section>

      <!-- 热门课程 -->
      <section class="courses-section">
        <h2 class="subsection-title">{{ isLoggedIn ? t('home.otherCourses') : t('home.popularCourses') }}</h2>
        <p class="subsection-desc">{{ t('home.coursesDesc') }}</p>
        <Loading v-if="coursesLoading" type="spinner" vertical>{{ t('home.loading') }}</Loading>
        <Empty v-else-if="coursesError" :description="coursesError" image-size="80" />
        <Empty v-else-if="courses.length === 0" :description="t('home.noCourses')" image-size="80" />
        <div v-else class="course-list">
          <button
            v-for="c in courses"
            :key="c.id"
            type="button"
            class="course-card"
            @click="goDetail(c.slug)"
          >
            <div class="course-card__thumb">
              <img v-if="c.thumbnail" :src="c.thumbnail" alt="" class="thumb-img" />
              <div v-else class="thumb-placeholder" />
            </div>
            <div class="course-card__body">
              <h3 class="course-card__title">{{ c.name }}</h3>
              <p class="course-card__desc">{{ (c.description || '').slice(0, 60) }}{{ (c.description && c.description.length > 60) ? '...' : '' }}</p>
              <span class="course-card__price">{{ c.discount ? Math.round(c.price * (1 - c.discount / 100)) : c.price }} ₸</span>
            </div>
            <span class="course-card__arrow">→</span>
          </button>
        </div>
        <button v-if="!coursesLoading && courses.length > 0" type="button" class="link-all" @click="goToCourses">
          {{ t('home.allCourses') }}
        </button>
      </section>

      <!-- Footer -->
      <footer class="footer">
        <div class="footer__brand">Easy Code</div>
        <p class="footer__desc">
          {{ t('home.footerDesc') }}
        </p>
        <div class="footer__social">
          <span class="footer__social-title">{{ t('home.contactUs') }}</span>
          <div class="footer__icons">
            <a href="https://facebook.com" target="_blank" rel="noopener" class="footer__icon" aria-label="Facebook">f</a>
            <a href="https://twitter.com" target="_blank" rel="noopener" class="footer__icon" aria-label="Twitter">𝕏</a>
            <a href="https://instagram.com" target="_blank" rel="noopener" class="footer__icon" aria-label="Instagram">📷</a>
            <a href="https://linkedin.com" target="_blank" rel="noopener" class="footer__icon" aria-label="LinkedIn">in</a>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  background: var(--edu-bg);
}
.content {
  padding: 28px 20px 32px;
}
/* Hero – 简约 */
.hero {
  position: relative;
  border-radius: var(--edu-radius);
  overflow: hidden;
  margin-bottom: 32px;
  background: var(--header-from);
  min-height: 200px;
}
.hero__bg {
  position: absolute;
  inset: 0;
}
.hero__img,
.hero__img-placeholder {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.hero__img {
  opacity: 0.22;
}
.hero__img-placeholder {
  background: var(--header-to);
}
.hero__overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    rgba(143, 131, 243, 0.94) 0%,
    rgba(99, 86, 217, 0.98) 100%
  );
}
.hero__content {
  position: relative;
  z-index: 1;
  padding: 32px 24px 28px;
  color: #fff;
}
.hero__title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 6px 0;
  letter-spacing: -0.02em;
}
.hero__subtitle {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 14px 0;
  opacity: 0.92;
}
.hero__desc {
  font-size: 13px;
  line-height: 1.55;
  margin: 0 0 24px 0;
  opacity: 0.88;
  max-width: 300px;
}
.hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
}
.hero-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.hero-btn--primary {
  background: #fff;
  color: var(--edu-primary-dark);
}
.hero-btn--outline {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.4);
}
.hero-btn__icon {
  font-size: 16px;
}
.hero-btn__icon--ai {
  font-size: 18px;
}
/* Why Choose – 简约卡片 */
.why-section {
  margin-bottom: 36px;
}
.why-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0 0 18px 0;
  letter-spacing: -0.01em;
}
.why-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.why-card {
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: var(--edu-radius-sm);
  padding: 18px 14px;
  box-shadow: var(--edu-shadow);
  text-align: center;
}
.why-card__icon {
  width: 44px;
  height: 44px;
  margin: 0 auto 10px;
  background: var(--edu-primary-soft);
  color: var(--edu-primary);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}
.why-card__title {
  font-size: 13px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0 0 4px 0;
}
.why-card__text {
  font-size: 12px;
  color: var(--edu-text-secondary);
  line-height: 1.4;
  margin: 0;
}
.subsection-desc {
  font-size: 13px;
  color: var(--edu-text-secondary);
  margin: 2px 0 14px 0;
}

.courses-section {
  margin-top: 32px;
}
.subsection-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0 0 8px 0;
  letter-spacing: -0.01em;
}
.course-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.course-card {
  display: flex;
  align-items: center;
  gap: 14px;
  width: 100%;
  text-align: left;
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: var(--edu-radius-sm);
  padding: 14px;
  box-shadow: var(--edu-shadow);
  cursor: pointer;
  transition: transform 0.02s;
  -webkit-tap-highlight-color: transparent;
}
.course-card:active {
  transform: scale(0.98);
}
.course-card__thumb {
  flex-shrink: 0;
  width: 80px;
  height: 56px;
  border-radius: 8px;
  overflow: hidden;
  background: var(--edu-border);
}
.course-card__thumb .thumb-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.course-card__thumb .thumb-placeholder {
  width: 100%;
  height: 100%;
  background: var(--edu-primary-soft);
}
.course-card__body {
  flex: 1;
  min-width: 0;
}
.course-card__title {
  font-size: 15px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.course-card__desc {
  font-size: 12px;
  color: var(--edu-text-secondary);
  margin: 0 0 4px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.course-card__price {
  font-size: 14px;
  font-weight: 600;
  color: var(--edu-primary);
}
.course-card__arrow {
  flex-shrink: 0;
  font-size: 16px;
  color: var(--edu-primary);
  font-weight: 600;
}
.course-card--active {
  border-color: var(--edu-primary);
  background: var(--edu-primary-soft);
}
.course-card--active .course-card__arrow {
  color: var(--edu-primary);
}
.course-card__badge {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  color: var(--edu-primary-dark);
  background: var(--edu-primary-soft);
  padding: 2px 8px;
  border-radius: 6px;
  margin-bottom: 4px;
}
.link-all {
  margin-top: 16px;
  width: 100%;
  padding: 12px;
  font-size: 14px;
  font-weight: 500;
  color: var(--edu-primary);
  background: none;
  border: none;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}

/* Footer – 简约 */
.footer {
  margin-top: 40px;
  padding: 28px 20px;
  background: var(--header-from);
  color: rgba(255, 255, 255, 0.94);
  border-radius: var(--edu-radius);
}
.footer__brand {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 12px;
}
.footer__desc {
  font-size: 13px;
  line-height: 1.5;
  opacity: 0.9;
  margin: 0 0 20px 0;
}
.footer__social-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
  display: block;
}
.footer__icons {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}
.footer__icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  text-decoration: none;
}
</style>
