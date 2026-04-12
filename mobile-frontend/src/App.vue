<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import { Tabbar, TabbarItem } from 'vant'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { t, locale } = useI18n()

onMounted(() => {
  authStore.initialize()
})

// 语言切换时更新 <html lang>
watch(locale, (code) => {
  document.documentElement.lang = code === 'kz' ? 'kk' : code === 'py' ? 'ru' : 'en'
}, { immediate: true })

type TabName = 'home' | 'courses' | 'my-courses' | 'profile'
const mainTabs = ['home', 'courses', 'my-courses', 'profile'] as const
const active = ref<string>((route.name as string) || 'home')
const showTabbar = ref(true)

watch(
  () => route.name,
  (name) => {
    const n = name as string
    showTabbar.value = mainTabs.includes(n as any)
    if (mainTabs.includes(n as any)) active.value = n
    else if (route.path.startsWith('/course') || route.path === '/courses') active.value = 'courses'
    else if (route.path === '/my-courses') active.value = 'my-courses'
    else if (route.path === '/profile') active.value = 'profile'
  },
  { immediate: true }
)

function onTabChange(name: string) {
  router.push({ name: name as TabName })
}
</script>

<template>
  <div class="app" :class="{ 'app--with-tabbar': showTabbar }">
    <AppHeader />
    <main class="page-main">
      <RouterView v-slot="{ Component }">
        <KeepAlive :include="['HomeView', 'CoursesView', 'MyCoursesView', 'ProfileView']">
          <component :is="Component" />
        </KeepAlive>
      </RouterView>
    </main>
    <Tabbar v-if="showTabbar" v-model="active" @change="onTabChange" fixed placeholder active-color="var(--edu-primary)" inactive-color="var(--edu-text-secondary)">
      <TabbarItem name="home" icon="home-o">{{ t('nav.home') }}</TabbarItem>
      <TabbarItem name="courses" icon="apps-o">{{ t('nav.courses') }}</TabbarItem>
      <TabbarItem name="my-courses" icon="records-o">{{ t('nav.myCourses') }}</TabbarItem>
      <TabbarItem name="profile" icon="user-o">{{ t('nav.profile') }}</TabbarItem>
    </Tabbar>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  background: var(--edu-bg);
}
.app--with-tabbar {
  padding-bottom: calc(56px + env(safe-area-inset-bottom));
}
.page-main {
  min-height: 100vh;
  padding-top: 0;
}
</style>
