import api from './api';
import type { Video } from '../types';

interface VideoWithQuestions extends Video {
  questions?: Array<{
    id: number;
    question_text: string;
    option_1: string;
    option_2: string;
    option_3: string;
    option_4: string;
  }>;
}

const videosService = {
  /**
   * Get all videos for a course
   * GET /api/courses/{slug}/videos/
   */
  getCourseVideos: async (courseSlug: string) => {
    const response = await api.get<Video[]>(`/api/course/${courseSlug}/videos/`);
    return response.data;
  },

  /**
   * Get a specific video with questions
   * GET /api/course/{slug}/video/{serial_number}/
   */
  getVideoDetail: async (courseSlug: string, serialNumber: number) => {
    const response = await api.get<VideoWithQuestions>(
      `/api/course/${courseSlug}/video/${serialNumber}/`
    );
    return response.data;
  }
};

export default videosService;
