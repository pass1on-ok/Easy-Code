<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NavBar, Form, Field, CellGroup, Button, showToast, showSuccessToast, showFailToast } from 'vant'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'

const route = useRoute()
const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const loading = ref(false)

onMounted(() => {
  if (route.query.session_expired === '1') {
    showToast(t('login.sessionExpired'))
  }
})

async function onSubmit() {
  if (!username.value || !password.value) {
    showToast(t('login.required'))
    return
  }
  loading.value = true
  const ok = await authStore.login({ username: username.value, password: password.value })
  loading.value = false
  if (ok) {
    showSuccessToast(t('login.success'))
    const redirect = (route.query.redirect as string) || '/'
    router.replace(redirect)
  } else {
    showFailToast(authStore.error || t('login.fail'))
  }
}
</script>

<template>
  <div class="login">
    <NavBar :title="t('login.title')" left-arrow @click-left="router.back()" fixed placeholder />
    <div class="content">
      <Form @submit="onSubmit">
        <CellGroup inset>
          <Field
            v-model="username"
            name="username"
            :label="t('login.username')"
            :placeholder="t('login.usernamePlaceholder')"
            :rules="[{ required: true, message: t('login.usernamePlaceholder') }]"
          />
          <Field
            v-model="password"
            type="password"
            name="password"
            :label="t('login.password')"
            :placeholder="t('login.passwordPlaceholder')"
            :rules="[{ required: true, message: t('login.passwordPlaceholder') }]"
          />
        </CellGroup>
        <div class="btn-wrap">
          <Button round block type="primary" native-type="submit" :loading="loading">
            {{ t('login.submit') }}
          </Button>
        </div>
      </Form>
    </div>
  </div>
</template>

<style scoped>
.login {
  min-height: 100vh;
  background: var(--edu-bg);
}
.content {
  padding: 20px;
}
.btn-wrap {
  margin-top: 24px;
  padding: 0 16px;
}
.btn-wrap :deep(.van-button--primary) {
  background: var(--edu-primary);
  border-color: var(--edu-primary);
}
</style>
