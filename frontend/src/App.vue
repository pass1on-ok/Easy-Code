<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import { useAuthStore } from './stores/auth'

const authStore = useAuthStore()

onMounted(() => {
  authStore.initialize()
})
</script>

<template>
  <div class="app">
    <Header />
    <main class="main-content">
      <RouterView />
    </main>
    <Footer />
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
  font-size: 16px;
  -webkit-text-size-adjust: 100%;
  -ms-text-size-adjust: 100%;
  text-size-adjust: 100%;
  width: 100%;
  height: 100%;
}

body {
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
  overflow-x: hidden;
  margin: 0;
  padding: 0;
  width: 100%;
  min-height: 100vh;
  position: relative;
}

#app {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu,
    Cantarell, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  max-width: 100% !important;
  width: 100%;
  margin: 0 !important;
  padding: 0 !important;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

* {
  -webkit-tap-highlight-color: transparent;
}

@media (max-width: 640px) {
  html {
    font-size: 15px;
  }
}

@media (max-width: 380px) {
  html {
    font-size: 14px;
  }
}

.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
}
</style>
