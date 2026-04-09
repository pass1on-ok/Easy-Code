<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NavBar, RadioGroup, Radio, Button, Loading, Empty, showFailToast } from 'vant'
import { useI18n } from '@/i18n'
import { getQuiz, submitQuiz } from '@/services/quiz'
import type { Quiz as QuizType, QuizResult } from '@/types'

defineOptions({ name: 'QuizView' })
const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const videoId = computed(() => Number(route.params.videoId))
const quiz = ref<QuizType | null>(null)
const result = ref<QuizResult | null>(null)
const loading = ref(true)
const submitting = ref(false)

// answers[questionId] = selectedIndex
const answers = ref<Record<number, number>>({})

const canSubmit = computed(() => {
  if (!quiz.value) return false
  return quiz.value.questions.every((q) => typeof answers.value[q.id] === 'number')
})

const scoreText = computed(() => {
  if (!result.value) return ''
  return t('quiz.score')
    .replace('{score}', String(result.value.score))
    .replace('{total}', String(result.value.total))
})

onMounted(async () => {
  loading.value = true
  try {
    quiz.value = await getQuiz(videoId.value)
  } catch (e: any) {
    showFailToast(e.response?.data?.message || e.message || t('quiz.loadFail'))
    quiz.value = null
  } finally {
    loading.value = false
  }
})

async function onSubmit() {
  if (!quiz.value) return
  if (!canSubmit.value) {
    showFailToast(t('quiz.answerAll'))
    return
  }
  submitting.value = true
  try {
    result.value = await submitQuiz(videoId.value, { answers: answers.value })
  } catch (e: any) {
    showFailToast(e.response?.data?.message || e.message || t('quiz.submitFail'))
  } finally {
    submitting.value = false
  }
}

function onRetake() {
  result.value = null
  answers.value = {}
}
</script>

<template>
  <div class="quiz">
    <NavBar :title="t('quiz.title')" left-arrow @click-left="router.back()" fixed placeholder />
    <div class="content">

      <!-- Loading -->
      <Loading v-if="loading" type="spinner" vertical>{{ t('home.loading') }}</Loading>

      <!-- No quiz -->
      <Empty
        v-else-if="!quiz || quiz.questions.length === 0"
        :description="t('quiz.noQuiz')"
        image-size="80"
      />

      <!-- Results screen -->
      <template v-else-if="result">
        <!-- Score banner -->
        <div class="result-banner" :class="result.passed ? 'pass' : 'fail'">
          <div class="result-icon">{{ result.passed ? '✓' : '✗' }}</div>
          <div class="result-label">{{ result.passed ? t('quiz.passed') : t('quiz.notPassed') }}</div>
          <div class="result-score">{{ scoreText }}</div>
        </div>

        <!-- Per-question breakdown -->
        <div
          v-for="r in result.results"
          :key="r.question_id"
          class="card"
          :class="r.correct ? 'card--correct' : 'card--wrong'"
        >
          <div class="q-header">
            <span class="q-badge" :class="r.correct ? 'badge--correct' : 'badge--wrong'">
              {{ r.correct ? t('quiz.correct') : t('quiz.wrong') }}
            </span>
            <div class="q-title">{{ r.prompt }}</div>
          </div>

          <!-- Options list with highlights -->
          <div class="options-review">
            <div
              v-for="(opt, idx) in r.options"
              :key="idx"
              class="opt-row"
              :class="{
                'opt--user-correct': idx === r.user_answer && r.correct,
                'opt--user-wrong':   idx === r.user_answer && !r.correct,
                'opt--correct-ans':  !r.correct && idx === r.correct_answer,
              }"
            >
              <span class="opt-marker">
                <template v-if="idx === r.user_answer && r.correct">✓</template>
                <template v-else-if="idx === r.user_answer && !r.correct">✗</template>
                <template v-else-if="!r.correct && idx === r.correct_answer">✓</template>
                <template v-else>{{ String.fromCharCode(65 + idx) }}</template>
              </span>
              <span class="opt-text">{{ opt }}</span>
              <span v-if="idx === r.user_answer" class="opt-tag">{{ t('quiz.yourAnswer') }}</span>
              <span v-if="!r.correct && idx === r.correct_answer" class="opt-tag opt-tag--correct">{{ t('quiz.correctAnswer') }}</span>
            </div>
            <!-- Not answered -->
            <div v-if="r.user_answer === null || r.user_answer === undefined" class="no-answer">
              {{ t('quiz.notAnswered') }}
            </div>
          </div>
        </div>

        <!-- Action buttons -->
        <div class="action-row">
          <Button v-if="!result.passed" type="primary" block round @click="onRetake">
            {{ t('quiz.retake') }}
          </Button>
          <Button :type="result.passed ? 'primary' : 'default'" block round @click="router.back()">
            {{ t('quiz.backToCourse') }}
          </Button>
        </div>
      </template>

      <!-- Quiz questions form -->
      <template v-else>
        <p class="subtitle">{{ quiz.video_title || t('quiz.lesson') }}</p>

        <div class="card" v-for="q in quiz.questions" :key="q.id">
          <div class="q-title">{{ q.prompt }}</div>
          <RadioGroup v-model="answers[q.id]" class="options">
            <Radio v-for="(opt, idx) in q.options" :key="idx" :name="idx">
              {{ opt }}
            </Radio>
          </RadioGroup>
        </div>

        <Button
          type="primary"
          block
          round
          :loading="submitting"
          :disabled="!canSubmit"
          @click="onSubmit"
        >
          {{ t('quiz.submit') }}
        </Button>
      </template>
    </div>
  </div>
</template>

<style scoped>
.quiz {
  min-height: 100vh;
  background: var(--edu-bg);
}
.content {
  padding: 16px 16px 32px;
}
.subtitle {
  margin: 8px 0 14px;
  color: var(--edu-text-secondary);
  font-size: 13px;
}

/* ── Quiz question cards ── */
.card {
  background: var(--edu-bg-card);
  border: 1px solid var(--edu-border);
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 12px;
  box-shadow: var(--edu-shadow);
}
.q-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--edu-text);
  margin-bottom: 10px;
}
.options :deep(.van-radio) {
  padding: 6px 0;
}

/* ── Result banner ── */
.result-banner {
  border-radius: 14px;
  padding: 20px 16px;
  margin-bottom: 16px;
  text-align: center;
  color: #fff;
}
.result-banner.pass {
  background: linear-gradient(135deg, #22c55e, #16a34a);
}
.result-banner.fail {
  background: linear-gradient(135deg, #f87171, #dc2626);
}
.result-icon {
  font-size: 40px;
  line-height: 1;
  margin-bottom: 6px;
}
.result-label {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}
.result-score {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

/* ── Result question cards ── */
.card--correct {
  border-color: #bbf7d0;
  background: var(--edu-bg-card);
}
.card--wrong {
  border-color: #fecaca;
  background: var(--edu-bg-card);
}
.q-header {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 12px;
}
.q-badge {
  flex-shrink: 0;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 20px;
  margin-top: 1px;
}
.badge--correct {
  background: #dcfce7;
  color: #166534;
}
.badge--wrong {
  background: #fee2e2;
  color: #991b1b;
}

/* ── Options review ── */
.options-review {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.opt-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 8px;
  background: var(--edu-bg);
  font-size: 13px;
  color: var(--edu-text);
  transition: background 0.15s;
}
.opt--user-correct {
  background: #dcfce7;
  color: #166534;
  font-weight: 600;
}
.opt--user-wrong {
  background: #fee2e2;
  color: #991b1b;
  font-weight: 600;
}
.opt--correct-ans {
  background: #dbeafe;
  color: #1e40af;
  font-weight: 600;
}
.opt-marker {
  flex-shrink: 0;
  width: 20px;
  text-align: center;
  font-weight: 700;
  font-size: 13px;
}
.opt-text {
  flex: 1;
}
.opt-tag {
  flex-shrink: 0;
  font-size: 10px;
  font-weight: 600;
  color: #991b1b;
  background: #fee2e2;
  padding: 1px 6px;
  border-radius: 10px;
}
.opt-tag--correct {
  color: #1e40af;
  background: #dbeafe;
}
.no-answer {
  font-size: 12px;
  color: var(--edu-text-secondary);
  font-style: italic;
  padding: 4px 0;
}

/* ── Action buttons ── */
.action-row {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}
</style>
