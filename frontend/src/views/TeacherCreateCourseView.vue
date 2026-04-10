<template>
  <div class="teacher-form-page">
    <div class="container">
      <div class="form-header">
        <h1>{{ t('teacher.createCourse') }}</h1>
        <p>{{ t('teacher.createCourseDescription') }}</p>
      </div>

      <form @submit.prevent="submit" class="teacher-course-form">
        <div class="form-row">
          <label>{{ t('teacher.courseName') }}</label>
          <input v-model="form.name" type="text" required />
        </div>

        <div class="form-row">
          <label>{{ t('teacher.courseSlug') }}</label>
          <input v-model="form.slug" type="text" />
          <small>{{ t('teacher.slugHint') }}</small>
        </div>

        <div class="form-row">
          <label>{{ t('teacher.courseDescription') }}</label>
          <textarea v-model="form.description" rows="4" required></textarea>
        </div>

        <div class="form-row split">
          <div>
            <label>{{ t('teacher.price') }}</label>
            <input v-model.number="form.price" type="number" min="0" required />
          </div>
          <div>
            <label>{{ t('teacher.discount') }}</label>
            <input v-model.number="form.discount" type="number" min="0" max="100" />
          </div>
        </div>

        <div class="form-row split">
          <div>
            <label>{{ t('teacher.lengthHours') }}</label>
            <input v-model.number="form.length" type="number" min="0" required />
          </div>
          <div>
            <label>{{ t('teacher.productId') }}</label>
            <input v-model="form.product_id" type="text" required />
          </div>
        </div>

        <div class="form-row">
          <label>{{ t('teacher.thumbnail') }}</label>
          <input @change="handleFileChange($event, 'thumbnail')" type="file" accept="image/*" />
        </div>

        <div class="form-row">
          <label>{{ t('teacher.resourceFile') }}</label>
          <input @change="handleFileChange($event, 'resource')" type="file" />
        </div>

        <div class="form-row checkbox-row">
          <label>
            <input type="checkbox" v-model="form.active" />
            {{ t('teacher.activeCourse') }}
          </label>
        </div>

        <div class="form-actions">
          <button class="btn btn-primary" type="submit" :disabled="submitting">
            {{ submitting ? t('teacher.saving') : t('teacher.saveCourse') }}
          </button>
          <RouterLink to="/teacher" class="btn btn-outline">{{ t('teacher.cancel') }}</RouterLink>
        </div>

        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useLanguageStore } from '@/stores/language'
import { teacherService } from '@/services/teacher'

const languageStore = useLanguageStore()
const router = useRouter()
const submitting = ref(false)
const errorMessage = ref<string | null>(null)

const currentLanguage = computed(() => languageStore.currentLanguage)
const t = (key: string) => {
  const _ = currentLanguage.value
  return languageStore.t(key)
}

const form = reactive({
  name: '',
  slug: '',
  description: '',
  price: 0,
  discount: 0,
  active: false,
  length: 0,
  product_id: '',
  thumbnail: null as File | null,
  resource: null as File | null,
})

function handleFileChange(event: Event, field: 'thumbnail' | 'resource') {
  const input = event.target as HTMLInputElement
  if (input.files && input.files.length > 0) {
    form[field] = input.files[0]
  }
}

async function submit() {
  submitting.value = true
  errorMessage.value = null

  try {
    const data = new FormData()
    data.append('name', form.name)
    if (form.slug) data.append('slug', form.slug)
    data.append('description', form.description)
    data.append('price', String(form.price))
    data.append('discount', String(form.discount))
    data.append('active', String(form.active))
    data.append('length', String(form.length))
    data.append('product_id', form.product_id)
    if (form.thumbnail) data.append('thumbnail', form.thumbnail)
    if (form.resource) data.append('resource', form.resource)

    await teacherService.createTeacherCourse(data)
    router.push('/teacher')
  } catch (err: any) {
    errorMessage.value = err.response?.data?.error || err.response?.data?.message || t('teacher.createCourseFailed')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.teacher-form-page {
  padding: 3rem 0;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 1.5rem;
}

.form-header {
  text-align: center;
  margin-bottom: 2rem;
}

.teacher-course-form {
  background: var(--color-card-bg);
  border-radius: 24px;
  padding: 2rem;
  box-shadow: var(--color-card-shadow);
}

.form-row {
  margin-bottom: 1.5rem;
}

.form-row label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.75rem;
}

.form-row input,
.form-row textarea {
  width: 100%;
  padding: 0.95rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 14px;
  background: var(--color-bg);
  color: var(--color-text-primary);
}

.form-row textarea {
  min-height: 120px;
}

.split {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.checkbox-row {
  display: flex;
  align-items: center;
}

.form-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}

.error-message {
  margin-top: 1rem;
  color: var(--color-danger);
}
</style>
