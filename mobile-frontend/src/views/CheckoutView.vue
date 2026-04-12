<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NavBar, Loading, Empty, Button, showFailToast } from 'vant'
import { getCourseBySlug } from '@/services/courses'
import { createCheckoutSession } from '@/services/payment'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'
import type { CourseDetail } from '@/types'

defineOptions({ name: 'CheckoutView' })
const { t } = useI18n()

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const slug = computed(() => route.params.slug as string)

const course = ref<CourseDetail | null>(null)
const loading = ref(true)
const error = ref('')
const enrolling = ref(false)

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.replace({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  try {
    course.value = await getCourseBySlug(slug.value)
  } catch (e: any) {
    const d = e.response?.data
    error.value = d?.message || d?.error || e.message || t('checkout.loadFail')
  } finally {
    loading.value = false
  }
})

async function handlePay() {
  if (!authStore.isAuthenticated) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  enrolling.value = true
  try {
    const { url } = await createCheckoutSession(slug.value)
    if (url) window.location.href = url
    else showFailToast(t('checkout.noLink'))
  } catch (e: any) {
    const d = e.response?.data
    const msg = d?.message || d?.error || e.message || t('checkout.sessionFail')
    showFailToast(msg)
  } finally {
    enrolling.value = false
  }
}
</script>

<template>
  <div class="checkout">
    <NavBar :title="t('checkout.title')" left-arrow @click-left="router.back()" fixed placeholder />
    <div class="content">
      <Loading v-if="loading" type="spinner" vertical>{{ t('home.loading') }}</Loading>
      <Empty v-else-if="error" :description="error" image-size="80" />
      <template v-else-if="course">
        <div class="summary">
          <h2 class="summary-title">{{ t('checkout.course') }}</h2>
          <h3 class="course-name">{{ course.name }}</h3>
          <p v-if="course.description" class="course-desc">{{ course.description }}</p>
          <div class="price-row">
            <span class="label">{{ t('checkout.price') }}</span>
            <span class="price">{{ course.price }} ₸</span>
          </div>
        </div>
        <Button
          type="primary"
          block
          round
          :loading="enrolling"
          @click="handlePay"
          class="pay-btn"
        >
          {{ t('checkout.payWithStripe') }}
        </Button>
      </template>
    </div>
  </div>
</template>

<style scoped>
.checkout {
  min-height: 100vh;
  background: var(--edu-bg);
}
.content {
  padding: 20px;
}
.summary {
  background: var(--edu-bg-card);
  border-radius: var(--edu-radius);
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: var(--edu-shadow);
}
.summary-title {
  font-size: 14px;
  color: var(--edu-text-secondary);
  margin: 0 0 8px 0;
}
.course-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 8px 0;
}
.course-desc {
  font-size: 14px;
  color: var(--edu-text-secondary);
  margin: 0 0 12px 0;
  line-height: 1.4;
}
.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid var(--edu-border);
}
.price-row .label {
  font-size: 14px;
  color: var(--edu-text-secondary);
}
.price {
  font-size: 20px;
  font-weight: 700;
  color: var(--edu-primary);
}
.pay-btn :deep(.van-button--primary) {
  background: var(--edu-primary);
  border-color: var(--edu-primary);
}
</style>
