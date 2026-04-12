<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NavBar, Field, Button, showSuccessToast, showFailToast } from 'vant'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from '@/i18n'
import { authService } from '@/services/auth'

defineOptions({ name: 'EditProfileView' })
const { t } = useI18n()

const router = useRouter()
const authStore = useAuthStore()

const first_name = ref('')
const last_name = ref('')
const email = ref('')
const bio = ref('')
const loading = ref(false)

onMounted(() => {
  const u = authStore.user
  if (u) {
    first_name.value = u.first_name || ''
    last_name.value = u.last_name || ''
    email.value = u.email || ''
    bio.value = u.bio || ''
  }
})

async function submit() {
  loading.value = true
  try {
    await authService.updateProfile({
      first_name: first_name.value.trim(),
      last_name: last_name.value.trim(),
      email: email.value.trim(),
      bio: bio.value.trim() || undefined,
    })
    await authStore.fetchCurrentUser()
    showSuccessToast(t('edit.saveSuccess'))
    router.back()
  } catch (e: any) {
    const d = e.response?.data
    const msg = d?.message || d?.email?.[0] || d?.detail || e.message || t('edit.saveFail')
    showFailToast(msg)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="edit-profile">
    <NavBar
      :title="t('edit.title')"
      left-arrow
      @click-left="router.back()"
      fixed
      placeholder
    />
    <div class="edit-profile__form">
      <Field
        v-model="first_name"
        :label="t('edit.firstName')"
        :placeholder="t('edit.firstNamePlaceholder')"
        maxlength="150"
        show-word-limit
      />
      <Field
        v-model="last_name"
        :label="t('edit.lastName')"
        :placeholder="t('edit.lastNamePlaceholder')"
        maxlength="150"
        show-word-limit
      />
      <Field
        v-model="email"
        type="email"
        :label="t('edit.email')"
        :placeholder="t('edit.emailPlaceholder')"
      />
      <Field
        v-model="bio"
        type="textarea"
        :label="t('edit.bio')"
        :placeholder="t('edit.bioPlaceholder')"
        maxlength="500"
        show-word-limit
        rows="3"
        autosize
      />
      <Button
        type="primary"
        block
        round
        :loading="loading"
        class="edit-profile__submit"
        @click="submit"
      >
        {{ t('edit.save') }}
      </Button>
    </div>
  </div>
</template>

<style scoped>
.edit-profile {
  min-height: 100vh;
  background: var(--edu-bg);
}
.edit-profile__form {
  padding: 20px 16px;
}
.edit-profile__form :deep(.van-cell) {
  padding: 14px 16px;
}
.edit-profile__submit {
  margin-top: 24px;
}
.edit-profile__submit :deep(.van-button--primary) {
  background: var(--edu-primary);
  border-color: var(--edu-primary);
}
</style>
