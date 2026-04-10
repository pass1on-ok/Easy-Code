<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>{{ video?.title }} - {{ t('test.title') }}</h2>
        <button @click="closeModal" class="close-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </div>

      <div class="modal-body">
        <!-- Loading State -->
        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <p>{{ t('test.loading') }}</p>
        </div>

        <!-- Previous Result Notice -->
        <div v-else-if="hasTaken && !showTest" class="previous-result">
          <div class="result-icon">
            <svg v-if="previousResult?.passed" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
              <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
          </div>
          <h3>{{ t('test.alreadyTaken') }}</h3>
          <div class="score-display">
            <p class="score">{{ previousResult?.score }} / {{ previousResult?.total_questions }}</p>
            <p class="percentage" :class="{ passed: previousResult?.passed }">
              {{ previousResult?.percentage }}%
            </p>
          </div>
          <p :class="previousResult?.passed ? 'status-passed' : 'status-failed'">
            {{ previousResult?.passed ? t('test.passed') : t('test.failed') }}
          </p>
          <button @click="retakeTest" class="retake-btn">{{ t('test.retake') }}</button>
        </div>

        <!-- Test Questions -->
        <div v-else-if="showTest && !showResults" class="test-container">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progressPercentage + '%' }"></div>
          </div>
          <p class="question-counter">{{ t('test.question') }} {{ currentQuestionIndex + 1 }} {{ t('test.of') }} {{ questions.length }}</p>

          <div v-if="currentQuestion" class="question-card">
            <h3 class="question-text">{{ currentQuestion.question_text }}</h3>
            <div class="options">
              <label 
                v-for="option in 4" 
                :key="option"
                class="option-label"
                :class="{ selected: userAnswers[String(currentQuestion?.id)] === option }"
              >
                <input
                  type="radio"
                  :name="'question-' + currentQuestion?.id"
                  :value="option"
                  :checked="userAnswers[String(currentQuestion?.id)] === option"
                  @change="(e) => handleAnswerChange(currentQuestion?.id || 0, (e.target as HTMLInputElement).valueAsNumber)"
                />
                <span class="option-text">{{ getOptionText(currentQuestion, option) }}</span>
              </label>
            </div>
          </div>

          <div class="navigation-buttons">
            <button 
              @click="previousQuestion" 
              :disabled="currentQuestionIndex === 0"
              class="nav-btn"
            >
              {{ t('test.previous') }}
            </button>
            <button 
              v-if="currentQuestionIndex < questions.length - 1"
              @click="nextQuestion" 
              class="nav-btn primary"
            >
              {{ t('test.next') }}
            </button>
            <button 
              v-else
              @click="submitTest" 
              :disabled="!allQuestionsAnswered"
              class="nav-btn submit"
            >
              {{ t('test.submit') }}
            </button>
          </div>
        </div>

        <!-- Test Results -->
        <div v-else-if="showResults && testResult" class="results-container">
          <div class="result-icon large">
            <svg v-if="testResult.passed" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
              <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="15" y1="9" x2="9" y2="15"></line>
              <line x1="9" y1="9" x2="15" y2="15"></line>
            </svg>
          </div>
          <h3>{{ testResult.passed ? t('test.congratulations') : t('test.testFailed') }}</h3>
          <div class="score-display large">
            <p class="score">{{ testResult.score }} / {{ testResult.total_questions }}</p>
            <p class="percentage" :class="{ passed: testResult.passed }">
              {{ testResult.percentage }}%
            </p>
          </div>
          <p class="result-message">
            {{ testResult.passed ? t('test.youPassed') : t('test.tryAgain') }}
          </p>

          <!-- Detailed Results -->
          <div v-if="testResult.results_detail" class="detailed-results">
            <h4>{{ t('test.reviewAnswers') }}</h4>
            <div 
              v-for="(result, index) in testResult.results_detail" 
              :key="result.question_id"
              class="result-item"
              :class="{ correct: result.is_correct, incorrect: !result.is_correct }"
            >
              <div class="result-header">
                <span class="question-number">Q{{ index + 1 }}</span>
                <span class="result-status">
                  {{ result.is_correct ? '✓ ' + t('test.correct') : '✗ ' + t('test.incorrect') }}
                </span>
              </div>
              <p class="result-question">{{ result.question_text }}</p>
              <p class="result-answer">
                <strong>{{ t('test.yourAnswer') }}:</strong>
                {{ result.selected_text || (result.selected_option !== null ? `Option ${result.selected_option}` : 'N/A') }}
              </p>
              <p v-if="!result.is_correct" class="correct-answer">
                <strong>{{ t('test.correctAnswer') }}:</strong>
                {{ result.correct_text || (result.correct_option !== null ? `Option ${result.correct_option}` : 'N/A') }}
              </p>
            </div>
          </div>

          <button @click="closeModal" class="close-results-btn">{{ t('test.close') }}</button>
        </div>

        <!-- Error State -->
        <div v-if="error" class="error-message">
          <p>{{ error }}</p>
          <button @click="closeModal" class="close-btn">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import testService, { type Question, type TestResult } from '../services/test';
import type { Video } from '../types';
import { useLanguageStore } from '@/stores/language';

const languageStore = useLanguageStore();
const t = (key: string) => languageStore.t(key);

const props = defineProps<{
  isOpen: boolean;
  video: Video | null;
  courseSlug: string;
}>();

const emit = defineEmits<{
  close: [];
  testCompleted: [passed: boolean];
}>();

const loading = ref(false);
const error = ref('');
const questions = ref<Question[]>([]);
const currentQuestionIndex = ref(0);
const userAnswers = ref<Record<string, number>>({});
const showTest = ref(false);
const showResults = ref(false);
const testResult = ref<TestResult | null>(null);
const hasTaken = ref(false);
const previousResult = ref<any>(null);

const currentQuestion = computed(() => questions.value[currentQuestionIndex.value]);

const progressPercentage = computed(() => {
  if (questions.value.length === 0) return 0;
  return ((currentQuestionIndex.value + 1) / questions.value.length) * 100;
});

const allQuestionsAnswered = computed(() => {
  return questions.value.every(q => userAnswers.value[q.id] !== undefined);
});

watch(() => props.isOpen, (isOpen) => {
  if (isOpen && props.video) {
    loadTest();
  }
});

const loadTest = async () => {
  if (!props.video) return;
  
  loading.value = true;
  error.value = '';
  
  try {
    const response = await testService.getTestQuestions(props.courseSlug, props.video.serial_number);
    questions.value = response.questions || [];
    hasTaken.value = false;
    previousResult.value = null;
    
    if (questions.value.length === 0) {
      error.value = 'No questions available for this test';
    } else {
      showTest.value = true;
    }
  } catch (err: any) {
    // Handle 403 Forbidden error for access
    if (err.response?.status === 403) {
      error.value = "You don't have access to this course. Please purchase the course first.";
    } else if (err.response?.status === 401) {
      error.value = 'Please log in to take this test';
    } else {
      error.value = err.response?.data?.error || 'Failed to load test';
    }
  } finally {
    loading.value = false;
  }
};

const retakeTest = () => {
  showTest.value = true;
  hasTaken.value = false;
  resetTest();
};

const resetTest = () => {
  currentQuestionIndex.value = 0;
  userAnswers.value = {};
  showResults.value = false;
  testResult.value = null;
};

const handleAnswerChange = (questionId: number, value: number) => {
  userAnswers.value[questionId] = value;
  // Auto-save functionality can be added here
};

const getOptionText = (question: Question | null, option: number) => {
  const text = question ? (question as any)[`option_${option}`] : ''
  return text && text.trim().length > 0 ? text : `Option ${option}`
}

const nextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++;
  }
};

const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--;
  }
};

const submitTest = async () => {
  if (!props.video || !allQuestionsAnswered.value) return;
  
  loading.value = true;
  error.value = '';
  
  try {
    const result = await testService.submitTest(props.courseSlug, props.video.serial_number, userAnswers.value);
    testResult.value = result;
    showTest.value = false;
    showResults.value = true;
    // Calculate percentage
    const percentage = Math.round((result.score / result.total_questions) * 100);
    const passed = percentage >= 70;
    emit('testCompleted', passed);
  } catch (err: any) {
    error.value = err.response?.data?.error || 'Failed to submit test';
  } finally {
    loading.value = false;
  }
};

const closeModal = () => {
  resetTest();
  showTest.value = false;
  hasTaken.value = false;
  emit('close');
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  overflow-y: auto;
  padding: 2rem;
}

.modal-content {
  background: var(--color-card-bg);
  border-radius: 12px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px var(--color-card-shadow);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid var(--color-border);
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  background: var(--gradient-hero);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  color: var(--color-text-tertiary);
  transition: color 0.2s;
}

.close-btn:hover {
  color: var(--color-text-primary);
}

.close-btn svg {
  width: 24px;
  height: 24px;
}

.modal-body {
  padding: 2rem;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: var(--color-text-secondary);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.previous-result {
  text-align: center;
  padding: 2rem;
}

.result-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 1rem;
}

.result-icon.large {
  width: 80px;
  height: 80px;
}

.result-icon svg {
  width: 100%;
  height: 100%;
  stroke: var(--color-primary);
}

.result-icon.large svg {
  stroke-width: 1.5;
}

.previous-result h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: var(--color-text-primary);
}

.score-display {
  margin: 1.5rem 0;
}

.score-display.large {
  margin: 2rem 0;
}

.score {
  font-size: 2rem;
  font-weight: bold;
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
}

.percentage {
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--color-error);
}

.percentage.passed {
  color: var(--color-success);
}

.status-passed {
  color: var(--color-success);
  font-weight: 600;
  font-size: 1.1rem;
  margin-bottom: 1rem;
}

.status-failed {
  color: var(--color-error);
  font-weight: 600;
  font-size: 1.1rem;
  margin-bottom: 1rem;
}

.retake-btn {
  background: var(--gradient-hero);
  color: white;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.retake-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.test-container {
  max-width: 600px;
  margin: 0 auto;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: var(--color-border);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 1rem;
}

.progress-fill {
  height: 100%;
  background: var(--gradient-hero);
  transition: width 0.3s ease;
}

.question-counter {
  text-align: center;
  color: var(--color-text-tertiary);
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

.question-card {
  background: var(--color-bg-secondary);
  padding: 2rem;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.question-text {
  font-size: 1.2rem;
  color: var(--color-text-primary);
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.option-label {
  display: flex;
  align-items: center;
  padding: 1rem;
  background: var(--color-card-bg);
  border: 2px solid var(--color-border);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.option-label:hover {
  border-color: var(--color-primary);
  background: var(--color-bg-secondary);
}

.option-label.selected {
  border-color: var(--color-primary);
  background: var(--gradient-card);
}

.option-label input[type="radio"] {
  margin-right: 1rem;
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.option-text {
  flex: 1;
  color: var(--color-text-primary);
  font-size: 1rem;
}

.navigation-buttons {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.nav-btn {
  flex: 1;
  padding: 0.75rem 1.5rem;
  border: 2px solid var(--color-primary);
  background: var(--color-card-bg);
  color: var(--color-primary);
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-btn:hover:not(:disabled) {
  background: var(--color-primary);
  color: white;
}

.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.nav-btn.primary, .nav-btn.submit {
  background: var(--gradient-hero);
  color: white;
  border: none;
}

.nav-btn.primary:hover, .nav-btn.submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.results-container {
  text-align: center;
  padding: 2rem;
}

.results-container h3 {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: var(--color-text-primary);
}

.result-message {
  font-size: 1.1rem;
  color: var(--color-text-secondary);
  margin-bottom: 2rem;
}

.detailed-results {
  text-align: left;
  margin-top: 2rem;
  max-height: 400px;
  overflow-y: auto;
}

.detailed-results h4 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
  color: var(--color-text-primary);
}

.result-item {
  background: var(--color-bg-secondary);
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  border-left: 4px solid var(--color-border);
}

.result-item.correct {
  border-left-color: var(--color-success);
}

.result-item.incorrect {
  border-left-color: var(--color-error);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.question-number {
  font-weight: 600;
  color: var(--color-text-tertiary);
}

.result-status {
  font-weight: 600;
  font-size: 0.9rem;
}

.result-item.correct .result-status {
  color: var(--color-success);
}

.result-item.incorrect .result-status {
  color: var(--color-error);
}

.result-question {
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.result-answer, .correct-answer {
  font-size: 0.9rem;
  color: var(--color-text-tertiary);
  margin: 0.25rem 0;
}

.correct-answer {
  color: var(--color-success);
}

.close-results-btn {
  background: var(--gradient-hero);
  color: white;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-top: 2rem;
}

.close-results-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.error-message {
  text-align: center;
  padding: 2rem;
  color: var(--color-error);
}

@media (max-width: 768px) {
  .modal-overlay {
    padding: 1rem;
  }
  
  .modal-content {
    max-height: 95vh;
  }
  
  .modal-header {
    padding: 1rem;
  }
  
  .modal-header h2 {
    font-size: 1.2rem;
  }
  
  .modal-body {
    padding: 1rem;
  }
  
  .question-card {
    padding: 1rem;
  }
  
  .navigation-buttons {
    flex-direction: column;
  }
}
</style>
