import api from './api';

export interface Question {
  id: number;
  question_text: string;
  option_1: string;
  option_2: string;
  option_3: string;
  option_4: string;
}

export interface TestResult {
  score: number;
  total_questions: number;
  percentage?: number;
  passed?: boolean;
  results_detail?: Array<{
    question_id: number;
    question_text: string;
    user_answer: string;
    correct_answer: string;
    selected_option?: number;
    correct_option?: number;
    is_correct: boolean;
  }>;
}

export interface CertificateData {
  pdf_base64: string;
  certificate: {
    course_name: string;
    completion_date: string;
  };
}

export type Certificate = CertificateData;

const testService = {
  /**
   * Get test questions for a specific video in a course
   * GET /take_test/{slug}/?lecture={serial_number}
   */
  getTestQuestions: async (courseSlug: string, lectureSerialNumber: number) => {
    const response = await api.get<{ questions: Question[] }>(`/exam/take_test/${courseSlug}/`, {
      params: {
        lecture: lectureSerialNumber
      }
    });
    return response.data;
  },

  /**
   * Submit test answers and get results
   * POST /take_test/{slug}/?lecture={serial_number}
   * Answers format: { "question_id": selected_option_number, ... }
   */
  submitTest: async (courseSlug: string, lectureSerialNumber: number, answers: Record<string, number>) => {
    const response = await api.post<TestResult>(`/exam/take_test/${courseSlug}/`, answers, {
      params: {
        lecture: lectureSerialNumber
      }
    });
    
    // Calculate percentage and passed status
    const data = response.data;
    const percentage = Math.round((data.score / data.total_questions) * 100);
    const passed = percentage >= 70;
    
    return {
      ...data,
      percentage,
      passed
    };
  },

  /**
   * Get final test questions for a course
   * GET /final_test/{slug}/?lecture=final
   */
  getFinalTest: async (courseSlug: string) => {
    const response = await api.get<{ questions: Question[] }>(`/exam/final_test/${courseSlug}/`, {
      params: {
        lecture: 'final'
      }
    });
    return response.data;
  },

  /**
   * Submit final test answers
   * POST /final_test/{slug}/?lecture=final
   */
  submitFinalTest: async (courseSlug: string, answers: Record<string, number>) => {
    const response = await api.post<TestResult>(`/exam/final_test/${courseSlug}/`, answers, {
      params: {
        lecture: 'final'
      }
    });
    
    // Calculate percentage and passed status
    const data = response.data;
    const percentage = Math.round((data.score / data.total_questions) * 100);
    const passed = percentage >= 70;
    
    return {
      ...data,
      percentage,
      passed
    };
  },

  /**
   * Generate certificate after course completion
   * GET /api/course/{slug}/certificate/
   */
  generateCertificate: async (courseSlug: string): Promise<Certificate> => {
    try {
      const response = await api.get<Certificate>(`/api/course/${courseSlug}/certificate/`);
      return response.data;
    } catch {
      // Return mock certificate if endpoint doesn't exist
      return {
        pdf_base64: '',
        certificate: {
          course_name: 'Course',
          completion_date: new Date().toISOString().split('T')[0] || ''
        }
      };
    }
  },

  /**
   * Get course completion status
   * Mock implementation - returns basic status
   */
  getCourseCompletionStatus: async (courseSlug: string) => {
    try {
      const response = await api.get(`/api/course/${courseSlug}/completion-status/`);
      return response.data;
    } catch {
      // If endpoint doesn't exist, return mock data
      return {
        enrolled: true,
        completed: false,
        completed_tests: 0,
        total_tests: 0,
        passed_tests: 0,
        can_generate_certificate: false
      };
    }
  }
};

export default testService;
