<script setup lang="ts">
import { useRouter } from 'vue-router'
import { NavBar, Button } from 'vant'
import { useI18n } from '@/i18n'

defineOptions({ name: 'PaymentCancelledView' })

const router = useRouter()
const { t } = useI18n()

function goBack() {
  router.back()
}
function goCourses() {
  router.replace({ name: 'courses' })
}
</script>

<template>
  <div class="page">
    <NavBar :title="t('paymentCancelled.title')" left-arrow @click-left="goBack" fixed placeholder />
    <div class="content">
      <div class="result-block">
      <div class="result-icon warning">!</div>
      <h2 class="result-title">{{ t('paymentCancelled.result') }}</h2>
      <p class="result-desc">{{ t('paymentCancelled.desc') }}</p>
    </div>
      <div class="actions">
        <Button block round type="primary" @click="goBack">{{ t('paymentCancelled.back') }}</Button>
        <Button block round plain @click="goCourses">{{ t('paymentCancelled.courseList') }}</Button>
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
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}
.result-icon.warning {
  background: #fef3c7;
  color: #d97706;
}
.result-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0 0 8px 0;
}
.result-desc {
  font-size: 14px;
  color: var(--edu-text-secondary);
  margin: 0;
}
.actions :deep(.van-button--primary) {
  background: var(--edu-primary);
  border-color: var(--edu-primary);
}
</style>
