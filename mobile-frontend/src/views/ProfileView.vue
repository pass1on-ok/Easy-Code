<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Cell, CellGroup } from 'vant'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'
import { getPurchasedCourses } from '@/services/courses'

defineOptions({ name: 'ProfileView' })
const { t } = useI18n()

const router = useRouter()
const authStore = useAuthStore()
const enrolledCount = ref(0)

const isLoggedIn = computed(() => authStore.isAuthenticated)
const displayName = computed(() => {
  const u = authStore.user
  if (!u) return ''
  if (u.first_name || u.last_name) return [u.first_name, u.last_name].filter(Boolean).join(' ')
  return u.username
})

onMounted(async () => {
  if (authStore.isAuthenticated) {
    try {
      const list = await getPurchasedCourses()
      enrolledCount.value = list.length
    } catch {
      enrolledCount.value = 0
    }
  }
})

function goLogin() {
  router.push({ name: 'login' })
}

function goSignup() {
  router.push({ name: 'signup' })
}

function goMyCourses() {
  router.push({ name: 'my-courses' })
}

function goTeacher() {
  router.push({ name: 'teacher' })
}

function goAbout() {
  router.push({ name: 'about' })
}

function goNotifications() {
  router.push({ name: 'notifications' })
}

function goEditProfile() {
  router.push({ name: 'edit-profile' })
}

function logout() {
  authStore.logout()
  router.replace({ name: 'home' })
}
</script>

<template>
  <div class="profile">
    <div class="content">
      <h1 class="page-title">{{ t('profile.title') }}</h1>
      <!-- User card -->
      <div v-if="isLoggedIn" class="user-card">
        <div class="user-card__avatar">
          <span class="user-card__avatar-icon">👤</span>
        </div>
        <div class="user-card__info">
          <div class="user-card__name">{{ displayName }}</div>
          <div v-if="authStore.user?.email" class="user-card__email">✉ {{ authStore.user.email }}</div>
        </div>
      </div>
      <!-- Stats -->
      <div v-if="isLoggedIn" class="stats-row">
        <div class="stat-card">
          <div class="stat-card__icon">☑</div>
          <div class="stat-card__label">{{ t('profile.enrolled') }}</div>
          <div class="stat-card__value">{{ enrolledCount }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-card__icon">📜</div>
          <div class="stat-card__label">{{ t('profile.certificates') }}</div>
          <div class="stat-card__value">0</div>
        </div>
        <div class="stat-card">
          <div class="stat-card__icon">⏱</div>
          <div class="stat-card__label">{{ t('profile.hours') }}</div>
          <div class="stat-card__value">0</div>
        </div>
      </div>
      <div class="card">
        <CellGroup v-if="isLoggedIn" :border="false">
          <Cell :title="t('profile.fullName')" :value="displayName" />
          <Cell :title="t('profile.username')" :value="authStore.user?.username" />
          <Cell v-if="authStore.user?.email" :title="t('profile.email')" :value="authStore.user.email" />
          <Cell :title="t('profile.editProfile')" is-link @click="goEditProfile" />
          <Cell :title="t('profile.myCourses')" is-link @click="goMyCourses" />
          <Cell :title="t('profile.notifications')" is-link @click="goNotifications" />
          <Cell :title="t('profile.teacherPanel')" is-link @click="goTeacher" />
          <Cell :title="t('profile.about')" is-link @click="goAbout" />
          <Cell :title="t('profile.logout')" is-link @click="logout" />
        </CellGroup>
        <template v-else>
          <CellGroup :border="false">
            <Cell :title="t('profile.login')" is-link @click="goLogin" />
            <Cell :title="t('profile.signup')" is-link @click="goSignup" />
            <Cell :title="t('profile.teacherPanel')" is-link @click="goTeacher" />
            <Cell :title="t('profile.about')" is-link @click="goAbout" />
          </CellGroup>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile {
  min-height: 100vh;
  background: var(--edu-bg);
}
.content {
  padding: 20px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--edu-text);
  margin: 0 0 16px 0;
}
.user-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: var(--edu-radius);
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--edu-shadow);
}
.user-card__avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--edu-primary-soft);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.user-card__avatar-icon {
  font-size: 28px;
}
.user-card__name {
  font-size: 18px;
  font-weight: 700;
  color: var(--edu-text);
  margin-bottom: 4px;
}
.user-card__email {
  font-size: 13px;
  color: var(--edu-text-secondary);
}
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 20px;
  align-items: stretch;
}
.stat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  min-height: 120px;
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: var(--edu-radius-sm);
  padding: 14px 8px;
  text-align: center;
  box-shadow: var(--edu-shadow);
}
.stat-card__icon {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  margin-bottom: 8px;
  background: var(--edu-primary-soft);
  color: var(--edu-primary);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  line-height: 1;
}
.stat-card__label {
  font-size: 11px;
  color: var(--edu-text-secondary);
  line-height: 1.3;
  margin-bottom: 6px;
  min-height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-card__value {
  font-size: 18px;
  font-weight: 700;
  color: var(--edu-primary);
  margin-top: auto;
}
.card {
  background: var(--edu-bg-card);
  border-radius: var(--edu-radius);
  overflow: hidden;
  box-shadow: var(--edu-shadow);
}
.card :deep(.van-cell) {
  padding: 14px 20px;
}
.card :deep(.van-cell__value) {
  color: var(--edu-text-secondary);
}
.card :deep(.van-cell--clickable:active) {
  background: var(--edu-primary-soft);
}
</style>
