<script setup lang="ts">
import { ref, nextTick, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useLocaleStore } from '@/stores/locale'
import { useI18n } from '@/i18n'
import { getBotReply } from '@/services/chatbot'

defineOptions({ name: 'AIChatView' })

const router = useRouter()
const localeStore = useLocaleStore()
const { t } = useI18n()

const inputText = ref('')
const messages = ref<{ role: 'ai' | 'user'; text: string; time: string }[]>([])
const chatListRef = ref<HTMLElement | null>(null)

function getTimeStr() {
  const locale = localeStore.current === 'kz' ? 'kk-KZ' : localeStore.current === 'py' ? 'ru-RU' : 'en-US'
  return new Date().toLocaleTimeString(locale, { hour: '2-digit', minute: '2-digit' })
}

function initGreeting() {
  if (messages.value.length === 0) {
    messages.value.push({ role: 'ai', text: t('ai.greeting'), time: getTimeStr() })
  } else {
    const first = messages.value[0]
    if (first) first.text = t('ai.greeting')
  }
}

onMounted(initGreeting)
watch(() => localeStore.current, initGreeting)

function send() {
  const text = inputText.value.trim()
  if (!text) return
  messages.value.push({
    role: 'user',
    text,
    time: getTimeStr(),
  })
  inputText.value = ''
  nextTick(() => {
    chatListRef.value?.scrollTo({ top: chatListRef.value.scrollHeight, behavior: 'smooth' })
  })
  setTimeout(() => {
    const lang = localeStore.current === 'kz' ? 'kz' : localeStore.current === 'py' ? 'py' : 'en'
    messages.value.push({
      role: 'ai',
      text: getBotReply(text, lang),
      time: getTimeStr(),
    })
    nextTick(() => {
      chatListRef.value?.scrollTo({ top: chatListRef.value!.scrollHeight, behavior: 'smooth' })
    })
  }, 800)
}
</script>

<template>
  <div class="ai-chat">
    <header class="ai-chat__header">
      <button type="button" class="ai-chat__back" :aria-label="t('detail.back')" @click="router.back()">
        <span class="ai-chat__back-icon">←</span>
      </button>
      <span class="ai-chat__star">★</span>
      <div class="ai-chat__title-wrap">
        <h1 class="ai-chat__title">{{ t('ai.title') }}</h1>
        <p class="ai-chat__subtitle">{{ t('ai.subtitle') }}</p>
      </div>
    </header>
    <div ref="chatListRef" class="ai-chat__list">
      <div
        v-for="(msg, i) in messages"
        :key="i"
        class="msg"
        :class="msg.role === 'ai' ? 'msg--ai' : 'msg--user'"
      >
        <p class="msg__text">{{ msg.text }}</p>
        <span class="msg__time">{{ msg.time }}</span>
      </div>
    </div>
    <div class="ai-chat__input-wrap">
      <input
        v-model="inputText"
        type="text"
        class="ai-chat__input"
        :placeholder="t('ai.placeholder')"
        @keydown.enter.prevent="send()"
      />
      <button type="button" class="ai-chat__send" :aria-label="t('ai.send')" @click="send()">
        <span class="ai-chat__send-icon">➤</span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.ai-chat {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--edu-bg);
}
.ai-chat__header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: linear-gradient(135deg, var(--header-from) 0%, var(--header-to) 100%);
  color: #fff;
  flex-shrink: 0;
}
.ai-chat__back {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 10px;
  color: #fff;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.ai-chat__back-icon {
  font-size: 20px;
  line-height: 1;
}
.ai-chat__star {
  font-size: 20px;
  opacity: 0.95;
}
.ai-chat__title-wrap {
  flex: 1;
  min-width: 0;
}
.ai-chat__title {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 2px 0;
}
.ai-chat__subtitle {
  font-size: 13px;
  opacity: 0.9;
  margin: 0;
}
.ai-chat__list {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.msg {
  max-width: 85%;
  padding: 12px 16px;
  border-radius: 16px;
  position: relative;
}
.msg--ai {
  align-self: flex-start;
  background: var(--edu-border);
  color: var(--edu-text);
}
.msg--user {
  align-self: flex-end;
  background: var(--edu-primary);
  color: #fff;
}
.msg__text {
  font-size: 15px;
  line-height: 1.5;
  margin: 0 0 6px 0;
}
.msg__time {
  font-size: 11px;
  opacity: 0.75;
}
.ai-chat__input-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  background: var(--edu-bg-card);
  border-top: 1px solid var(--edu-border);
  flex-shrink: 0;
}
.ai-chat__input {
  flex: 1;
  padding: 12px 16px;
  font-size: 15px;
  border: 1px solid var(--edu-border);
  border-radius: 24px;
  background: var(--edu-bg);
  color: var(--edu-text);
  outline: none;
}
.ai-chat__input::placeholder {
  color: var(--edu-text-secondary);
}
.ai-chat__send {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--edu-primary);
  color: #fff;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.ai-chat__send-icon {
  font-size: 20px;
  line-height: 1;
}
</style>
