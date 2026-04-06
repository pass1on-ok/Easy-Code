<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Empty, Loading, Image as VanImage } from 'vant'
import { getPurchasedCourses } from '@/services/courses'
import { useI18n } from '@/i18n'
import type { PurchasedCourse } from '@/types'

defineOptions({ name: 'MyCoursesView' })
const { t } = useI18n()

const router = useRouter()
const list = ref<PurchasedCourse[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    list.value = await getPurchasedCourses()
  } catch (e: any) {
    error.value = e.response?.data?.error || e.message || t('myCourses.loadFail')
  } finally {
    loading.value = false
  }
})

function goDetail(slug: string) {
  router.push({ name: 'course-detail', params: { slug } })
}
</script>

<template>
  <div class="my-courses">
    <div class="content">
      <h1 class="page-title">{{ t('myCourses.title') }}</h1>
      <p class="page-desc">{{ t('myCourses.desc') }}</p>
      <Loading v-if="loading" type="spinner" vertical>{{ t('home.loading') }}</Loading>
      <template v-else-if="error">
        <Empty :description="error" image-size="80" />
      </template>
      <template v-else-if="list.length === 0">
        <Empty :description="t('myCourses.noCourses')" image-size="80" />
      </template>
      <div v-else class="course-list">
        <button
          v-for="c in list"
          :key="c.id"
          type="button"
          class="course-card"
          @click="goDetail(c.slug)"
        >
          <div class="course-card__thumb">
            <VanImage v-if="c.thumbnail" :src="c.thumbnail" fit="cover" width="100%" height="120" radius="8" />
            <div v-else class="thumb-placeholder" />
          </div>
          <div class="course-card__body">
            <h3 class="course-card__title">{{ c.name }}</h3>
            <p class="course-card__desc">{{ c.description || '' }}</p>
            <div class="course-card__meta" v-if="c.completed !== undefined">
              <span v-if="c.completed" class="badge">{{ t('myCourses.badgeDone') }}</span>
              <span v-else class="badge ongoing">{{ t('myCourses.badgeOngoing') }}</span>
            </div>
          </div>
          <span class="course-card__arrow">→</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.my-courses {
  min-height: 100vh;
  background: var(--edu-bg);
}
.content {
  padding: 20px;
  min-height: 50vh;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 4px 0;
}
.page-desc {
  font-size: 13px;
  color: var(--edu-text-secondary);
  margin: 0 0 16px 0;
}
.course-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.course-card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  width: 100%;
  text-align: left;
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: var(--edu-radius-sm);
  padding: 12px;
  box-shadow: var(--edu-shadow);
  cursor: pointer;
  transition: transform 0.2s;
  -webkit-tap-highlight-color: transparent;
}
.course-card:active {
  transform: scale(0.98);
}
.course-card__thumb {
  flex-shrink: 0;
  width: 100px;
  height: 70px;
  border-radius: 8px;
  overflow: hidden;
  background: var(--edu-border);
}
.thumb-placeholder {
  width: 100%;
  height: 100%;
  background: var(--edu-primary-soft);
}
.course-card__body {
  flex: 1;
  min-width: 0;
}
.course-card__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.course-card__desc {
  font-size: 13px;
  color: var(--edu-text-secondary);
  margin: 0 0 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.course-card__meta .badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 6px;
  background: var(--edu-primary-soft);
  color: var(--edu-primary);
}
.course-card__meta .badge.ongoing {
  background: #e0f2fe;
  color: #0284c7;
}
.course-card__arrow {
  flex-shrink: 0;
  font-size: 18px;
  color: var(--edu-primary);
  font-weight: 600;
}
</style>
