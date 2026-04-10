<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Course Certificate</h2>
        <button @click="close" class="close-btn">
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
          <p>Generating your certificate...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="error-message">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
          </svg>
          <p>{{ error }}</p>
          <button @click="close" class="btn-primary">Close</button>
        </div>

        <!-- Certificate Display -->
        <div v-else-if="certificate" class="certificate-container">
          <div class="certificate-info">
            <div class="congrats-section">
              <div class="trophy-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"></path>
                  <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"></path>
                  <path d="M4 22h16"></path>
                  <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"></path>
                  <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"></path>
                  <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"></path>
                </svg>
              </div>
              <h3>Congratulations!</h3>
              <p class="completion-message">
                You have successfully completed <strong>{{ certificate.certificate.course_name }}</strong>
              </p>
              <p class="completion-date">
                Completion Date: {{ certificate.certificate.completion_date }}
              </p>
            </div>

            <!-- PDF Preview -->
            <div class="pdf-preview">
              <iframe 
                :src="pdfDataUrl" 
                type="application/pdf"
                class="pdf-iframe"
              ></iframe>
            </div>

            <!-- Action Buttons -->
            <div class="action-buttons">
              <button @click="downloadCertificate" class="btn-download">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                  <polyline points="7 10 12 15 17 10"></polyline>
                  <line x1="12" y1="15" x2="12" y2="3"></line>
                </svg>
                Download Certificate
              </button>
              <button @click="close" class="btn-secondary">Close</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import testService, { type Certificate } from '../services/test';

const props = defineProps<{
  isOpen: boolean;
  courseSlug: string;
}>();

const emit = defineEmits<{
  close: [];
}>();

const loading = ref(false);
const error = ref('');
const certificate = ref<Certificate | null>(null);

const pdfDataUrl = computed(() => {
  if (!certificate.value) return '';
  return `data:application/pdf;base64,${certificate.value.pdf_base64}`;
});

watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    loadCertificate();
  }
});

const loadCertificate = async () => {
  loading.value = true;
  error.value = '';
  
  try {
    const data = await testService.generateCertificate(props.courseSlug);
    certificate.value = data;
  } catch (err: any) {
    error.value = err.response?.data?.error || 'Failed to generate certificate';
  } finally {
    loading.value = false;
  }
};

const downloadCertificate = () => {
  if (!certificate.value) return;
  
  const link = document.createElement('a');
  link.href = pdfDataUrl.value;
  link.download = `${certificate.value.certificate.course_name}_certificate.pdf`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

const close = () => {
  certificate.value = null;
  error.value = '';
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
  padding: 2rem;
  overflow-y: auto;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  color: #6b7280;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #374151;
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
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e5e7eb;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message {
  text-align: center;
  padding: 3rem;
}

.error-message svg {
  width: 60px;
  height: 60px;
  margin: 0 auto 1rem;
  stroke: #ef4444;
}

.error-message p {
  color: #ef4444;
  font-size: 1.1rem;
  margin-bottom: 1.5rem;
}

.certificate-container {
  max-width: 800px;
  margin: 0 auto;
}

.congrats-section {
  text-align: center;
  margin-bottom: 2rem;
}

.trophy-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.trophy-icon svg {
  width: 48px;
  height: 48px;
  stroke: #667eea;
  stroke-width: 2;
}

.congrats-section h3 {
  font-size: 2rem;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.completion-message {
  font-size: 1.1rem;
  color: #374151;
  margin-bottom: 0.5rem;
}

.completion-message strong {
  color: #1f2937;
  font-weight: 600;
}

.completion-date {
  color: #6b7280;
  font-size: 0.95rem;
}

.pdf-preview {
  width: 100%;
  height: 500px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #e5e7eb;
  margin-bottom: 2rem;
  background: #f9fafb;
}

.pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.btn-download,
.btn-secondary,
.btn-primary {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-download {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-download svg {
  width: 20px;
  height: 20px;
}

.btn-download:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
}

.btn-secondary:hover {
  background: #f3f4f6;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
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
  
  .congrats-section h3 {
    font-size: 1.5rem;
  }
  
  .pdf-preview {
    height: 400px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .btn-download,
  .btn-secondary {
    width: 100%;
  }
}
</style>
