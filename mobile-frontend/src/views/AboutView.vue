<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { NavBar } from 'vant'
import { useI18n } from '@/i18n'

defineOptions({ name: 'AboutView' })

const router = useRouter()
const { t } = useI18n()

const features = computed(() => [
  { icon: '📚', title: t('about.featCourses'), desc: t('about.featCoursesDesc') },
  { icon: '📋', title: t('about.featMyCourses'), desc: t('about.featMyCoursesDesc') },
  { icon: '👤', title: t('about.featProfile'), desc: t('about.featProfileDesc') },
  { icon: '🎓', title: t('about.featTeacher'), desc: t('about.featTeacherDesc') },
  { icon: '💳', title: t('about.featPayment'), desc: t('about.featPaymentDesc') },
  { icon: '📝', title: t('about.featTests'), desc: t('about.featTestsDesc') },
  { icon: '🌐', title: t('about.featLang'), desc: t('about.featLangDesc') },
  { icon: '🌙', title: t('about.featTheme'), desc: t('about.featThemeDesc') },
])
</script>

<template>
  <div class="about">
    <NavBar :title="t('about.title')" left-arrow @click-left="router.back()" fixed placeholder />
    <div class="content">
      <div class="about-hero">
        <h1 class="about-title">Easy Code</h1>
        <p class="about-tagline">{{ t('about.tagline') }}</p>
      </div>

      <p class="about-intro">
        {{ t('about.intro') }}
      </p>

      <section class="features">
        <h2 class="section-title">{{ t('about.featuresTitle') }}</h2>
        <div v-for="(item, i) in features" :key="i" class="feature-card">
          <div class="feature-card__icon">{{ item.icon }}</div>
          <div class="feature-card__body">
            <h3 class="feature-card__title">{{ item.title }}</h3>
            <p class="feature-card__desc">{{ item.desc }}</p>
          </div>
        </div>
      </section>

      <section class="cta">
        <p class="cta-text">{{ t('about.ctaText') }}</p>
        <button type="button" class="cta-btn" @click="router.push({ name: 'courses' })">
          {{ t('about.ctaBtn') }}
        </button>
      </section>
    </div>
  </div>
</template>

<style scoped>
.about {
  min-height: 100vh;
  background: var(--edu-bg);
}
.content {
  padding: 20px;
  padding-bottom: 40px;
}
.about-hero {
  margin-bottom: 20px;
}
.about-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 8px 0;
  letter-spacing: -0.02em;
}
.about-tagline {
  font-size: 15px;
  color: var(--edu-text-secondary);
  line-height: 1.5;
  margin: 0;
}
.about-intro {
  font-size: 14px;
  color: var(--edu-text);
  line-height: 1.6;
  margin: 0 0 24px 0;
  padding: 16px;
  background: var(--edu-bg-card);
  border-radius: var(--edu-radius-sm);
  border: 1px solid var(--edu-border);
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 16px 0;
}
.features {
  margin-bottom: 28px;
}
.feature-card {
  display: flex;
  gap: 14px;
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: var(--edu-radius-sm);
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: var(--edu-shadow);
}
.feature-card__icon {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
  background: var(--edu-primary-soft);
  color: var(--edu-primary);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}
.feature-card__body {
  flex: 1;
  min-width: 0;
}
.feature-card__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0 0 6px 0;
}
.feature-card__desc {
  font-size: 13px;
  color: var(--edu-text-secondary);
  line-height: 1.5;
  margin: 0;
}
.cta {
  text-align: center;
  padding: 24px 16px;
  background: linear-gradient(135deg, var(--header-from) 0%, var(--header-to) 100%);
  color: #fff;
  border-radius: var(--edu-radius);
}
.cta-text {
  font-size: 15px;
  margin: 0 0 16px 0;
  opacity: 0.95;
}
.cta-btn {
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 600;
  color: var(--edu-primary-dark);
  background: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
</style>
