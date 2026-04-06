<script setup lang="ts">
import { Empty } from 'vant'
import { useI18n } from '@/i18n'

defineOptions({ name: 'NotificationsView' })
const { t } = useI18n()
</script>

<template>
  <div class="notifications">
    <header class="header">
      <h1 class="section-title">{{ t('notifications.title') }}</h1>
    </header>
    <div class="content">
      <Empty :description="t('notifications.empty')" image-size="80" />
    </div>
  </div>
</template>

<style scoped>
.notifications {
  min-height: 100vh;
  background: var(--edu-bg);
}
.header {
  padding: 24px 20px 16px;
  background: var(--edu-bg-card);
  border-bottom: 1px solid var(--edu-border);
}
.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--edu-text);
  margin: 0;
  letter-spacing: -0.02em;
}
.content {
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 40vh;
}
</style>
