<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NavBar, Button } from 'vant'
import { useI18n } from '@/i18n'
import { confirmPayment } from '@/services/payment'

defineOptions({ name: 'PaymentSuccessView' })
const { t } = useI18n()

const route = useRoute()
const router = useRouter()
const sessionId = route.query.session_id as string
const courseId = Number(route.query.course_id)

onMounted(async () => {
  if (sessionId && courseId) {
    try {
      await confirmPayment(sessionId, courseId)
    } catch (_e) {
      // 可能已确认过，忽略
    }
  }
})

function goCourses() {
  router.replace({ name: 'courses' })
}
function goMyCourses() {
  router.replace({ name: 'my-courses' })
}
</script>

<template>
  <div class="page">
    <NavBar :title="t('paymentSuccess.title')" left-arrow @click-left="goCourses" fixed placeholder />
    <div class="content">
      <div class="result-block">
        <div class="result-icon success">✓</div>
        <h2 class="result-title">{{ t('paymentSuccess.result') }}</h2>
      </div>
      <div class="actions">
        <Button block round type="primary" @click="goMyCourses">{{ t('paymentSuccess.myCourses') }}</Button>
        <Button block round plain @click="goCourses">{{ t('paymentSuccess.courseList') }}</Button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: var(--edu-bg);
}
.content {
  padding: 20px;
}
.actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 24px;
}
.result-block {
  text-align: center;
  padding: 40px 20px;
}
.result-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  margin: 0 auto 16px;
  font-size: 32px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}
.result-icon.success {
  background: #dcfce7;
  color: #16a34a;
}
.result-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0;
}
.actions :deep(.van-button--primary) {
  background: var(--edu-primary);
  border-color: var(--edu-primary);
}
</style>
