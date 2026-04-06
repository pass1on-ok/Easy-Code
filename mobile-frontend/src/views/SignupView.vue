<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { NavBar, Form, Field, CellGroup, Button, showToast, showSuccessToast, showFailToast } from 'vant'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'

const router = useRouter()
const { t } = useI18n()
const authStore = useAuthStore()

const username = ref('')
const email = ref('')
const password = ref('')
const password2 = ref('')
const loading = ref(false)

async function onSubmit() {
  if (password.value !== password2.value) {
    showToast(t('signup.passwordMismatch'))
    return
  }
  loading.value = true
  const ok = await authStore.signup({
    username: username.value,
    email: email.value,
    password: password.value,
    password2: password2.value,
  })
  loading.value = false
  if (ok) {
    showSuccessToast(t('signup.success'))
    router.replace('/')
  } else {
    showFailToast(authStore.error || t('signup.fail'))
  }
}
</script>

<template>
  <div class="signup">
    <NavBar :title="t('signup.title')" left-arrow @click-left="router.back()" fixed placeholder />
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
            v-model="email"
            name="email"
            type="email"
            :label="t('signup.email')"
            :placeholder="t('signup.emailPlaceholder')"
            :rules="[{ required: true, message: t('signup.emailPlaceholder') }]"
          />
          <Field
            v-model="password"
            type="password"
            name="password"
            :label="t('login.password')"
            :placeholder="t('login.passwordPlaceholder')"
            :rules="[{ required: true, message: t('login.passwordPlaceholder') }]"
          />
          <Field
            v-model="password2"
            type="password"
            name="password2"
            :label="t('signup.passwordConfirm')"
            :placeholder="t('signup.passwordConfirmPlaceholder')"
            :rules="[{ required: true, message: t('signup.passwordConfirmPlaceholder') }]"
          />
        </CellGroup>
        <div class="btn-wrap">
          <Button round block type="primary" native-type="submit" :loading="loading">
            {{ t('signup.submit') }}
          </Button>
        </div>
      </Form>
    </div>
  </div>
</template>

<style scoped>
.signup {
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
