<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useLocaleStore } from '@/stores/locale'
import { useThemeStore } from '@/stores/theme'
import { useI18n } from '@/i18n'
import type { LocaleCode } from '@/stores/locale'

defineOptions({ name: 'AppHeader' })

const router = useRouter()
const authStore = useAuthStore()
const localeStore = useLocaleStore()
const themeStore = useThemeStore()
const { t } = useI18n()

const showLangDropdown = ref(false)
const showUserMenu = ref(false)
const username = () => authStore.user?.username || ''

const localeLabels: Record<LocaleCode, string> = { en: 'EN', py: 'PY', kz: 'KZ' }
const currentLabel = () => localeLabels[localeStore.current]

function setLang(code: LocaleCode) {
  localeStore.setLocale(code)
  showLangDropdown.value = false
}

function toggleLangDropdown() {
  showLangDropdown.value = !showLangDropdown.value
}

function closeLangDropdown() {
  showLangDropdown.value = false
}

function closeUserMenu() {
  showUserMenu.value = false
}

function onClickOutside(e: MouseEvent) {
  const el = e.target as Node
  const langTrigger = document.querySelector('.lang-dropdown-trigger')
  const langPanel = document.querySelector('.lang-dropdown-panel')
  const userTrigger = document.querySelector('.user-menu-trigger')
  const userPanel = document.querySelector('.user-menu-panel')
  if (!el) return
  if (!langTrigger?.contains(el) && !langPanel?.contains(el)) closeLangDropdown()
  if (!userTrigger?.contains(el) && !userPanel?.contains(el)) closeUserMenu()
}

onMounted(() => {
  document.addEventListener('click', onClickOutside)
})
onUnmounted(() => {
  document.removeEventListener('click', onClickOutside)
})

function goProfile() {
  closeUserMenu()
  router.push({ name: 'profile' })
}

function logout() {
  closeUserMenu()
  authStore.logout()
  router.replace({ name: 'home' })
}
</script>

<template>
  <header class="app-header">
    <div class="app-header__brand" @click="router.push({ name: 'home' })">
      <span class="app-header__logo-icon">&lt;/&gt;</span>
      <span class="app-header__title"><span class="app-header__title-easy">Easy</span> <span class="app-header__title-code">Code</span></span>
    </div>
    <div class="app-header__actions">
      <button type="button" class="icon-btn" :aria-label="themeStore.isDark ? t('header.themeLight') : t('header.themeDark')" @click="themeStore.toggle()">
        <span class="icon-btn__icon">{{ themeStore.isDark ? '☀️' : '🌙' }}</span>
      </button>
      <div class="lang-wrap">
        <button
          type="button"
          class="lang-dropdown-trigger"
          :class="{ open: showLangDropdown }"
          @click.stop="toggleLangDropdown"
        >
          <span>{{ currentLabel() }}</span>
          <span class="lang-arrow">{{ showLangDropdown ? '▲' : '▼' }}</span>
        </button>
        <Transition name="lang-slide">
          <div v-show="showLangDropdown" class="lang-dropdown-panel">
            <button type="button" class="lang-option" :class="{ active: localeStore.current === 'en' }" @click="setLang('en')">EN</button>
            <button type="button" class="lang-option" :class="{ active: localeStore.current === 'py' }" @click="setLang('py')">PY</button>
            <button type="button" class="lang-option" :class="{ active: localeStore.current === 'kz' }" @click="setLang('kz')">KZ</button>
          </div>
        </Transition>
      </div>
      <template v-if="authStore.isAuthenticated">
        <div class="user-menu-wrap">
          <button
            type="button"
            class="user-menu-trigger"
            :class="{ open: showUserMenu }"
            @click.stop="showUserMenu = !showUserMenu"
          >
            <span class="user-menu-trigger__name">@{{ username() }}</span>
            <span class="user-menu-trigger__arrow">{{ showUserMenu ? '▲' : '▼' }}</span>
          </button>
          <Transition name="lang-slide">
            <div v-show="showUserMenu" class="user-menu-panel">
              <button type="button" class="user-menu-item" @click="goProfile">{{ t('header.profile') }}</button>
              <button type="button" class="user-menu-item user-menu-item--logout" @click="logout">{{ t('header.logout') }}</button>
            </div>
          </Transition>
        </div>
      </template>
      <template v-else>
        <button type="button" class="login-link" @click="router.push({ name: 'login' })">{{ t('header.login') }}</button>
      </template>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 24px;
  gap: 24px;
  background: linear-gradient(135deg, var(--header-from) 0%, var(--header-to) 100%);
  color: var(--header-text);
  min-height: 56px;
  flex-shrink: 0;
}
.app-header__brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.app-header__logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  font-size: 14px;
  font-weight: 700;
  font-family: ui-monospace, 'Cascadia Code', 'SF Mono', monospace;
  color: var(--header-from);
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12);
  letter-spacing: -0.04em;
}
.app-header__title {
  font-size: 17px;
  font-weight: 600;
  letter-spacing: -0.02em;
  line-height: 1.2;
}
.app-header__title-easy {
  opacity: 0.95;
  font-weight: 500;
}
.app-header__title-code {
  font-weight: 700;
  margin-left: 2px;
}
.app-header__actions {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;
}
.icon-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
  flex-shrink: 0;
}
.icon-btn__icon {
  font-size: 16px;
}
.lang-wrap {
  position: relative;
}
.lang-dropdown-trigger {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  font-size: 12px;
  font-weight: 600;
  color: var(--header-text);
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.lang-dropdown-trigger.open {
  background: rgba(255, 255, 255, 0.3);
}
.lang-arrow {
  font-size: 10px;
  opacity: 0.9;
}
.lang-dropdown-panel {
  position: absolute;
  top: calc(100% + 6px);
  right: 0;
  min-width: 100%;
  background: rgba(30, 27, 46, 0.98);
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  z-index: 100;
}
.lang-option {
  display: block;
  width: 100%;
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.9);
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.lang-option:hover,
.lang-option.active {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}
.lang-slide-enter-active,
.lang-slide-leave-active {
  transition: transform 0.2s ease, opacity 0.2s ease;
}
.lang-slide-enter-from,
.lang-slide-leave-to {
  transform: translateY(-8px);
  opacity: 0;
}
.lang-slide-enter-to,
.lang-slide-leave-from {
  transform: translateY(0);
  opacity: 1;
}
.user-menu-wrap {
  position: relative;
}
.user-menu-trigger {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  font-size: 12px;
  font-weight: 600;
  color: var(--header-text);
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.user-menu-trigger.open {
  background: rgba(255, 255, 255, 0.3);
}
.user-menu-trigger__name {
  overflow: hidden;
  text-overflow: ellipsis;
}
.user-menu-trigger__arrow {
  font-size: 9px;
  opacity: 0.9;
  flex-shrink: 0;
}
.user-menu-panel {
  position: absolute;
  top: calc(100% + 6px);
  right: 0;
  min-width: 120px;
  background: rgba(30, 27, 46, 0.98);
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  z-index: 100;
}
.user-menu-item {
  display: block;
  width: 100%;
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.9);
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.user-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}
.user-menu-item--logout {
  color: rgba(255, 200, 200, 0.95);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}
.login-link {
  font-size: 12px;
  font-weight: 600;
  color: var(--header-text);
  background: rgba(255, 255, 255, 0.25);
  border: none;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
</style>
