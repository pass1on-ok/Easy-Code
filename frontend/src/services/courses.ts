import api from './api'
import type { Course, CourseDetail } from '@/types'

export const coursesService = {
  async getCourses(): Promise<Course[]> {
    const response = await api.get('/api/courses/')
    return response.data
  },

  async getCourseBySlug(slug: string): Promise<CourseDetail> {
    const response = await api.get(`/api/course/${slug}/`)
    return response.data
  },

  async enrollCourse(slug: string) {
    const response = await api.post(`/check-out/${slug}/`)
    return response.data
  },

  async getPurchasedCourses(): Promise<Course[]> {
    const response = await api.get('/api/purchased-courses/')
    return response.data
  }
}
