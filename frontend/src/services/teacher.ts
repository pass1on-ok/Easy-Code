import api from './api'
import type { Course } from '@/types'

export interface TeacherDashboard {
  course_count: number
  student_count: number
  revenue: number
  average_rating: number
}

export const teacherService = {
  async getTeacherDashboard(): Promise<TeacherDashboard> {
    const response = await api.get('/teacher/api/dashboard/')
    return response.data
  },

  async getTeacherCourses(): Promise<Course[]> {
    const response = await api.get('/teacher/api/courses/')
    return response.data
  },

  async createTeacherCourse(formData: FormData): Promise<Course> {
    const response = await api.post('/teacher/api/courses/create/', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    return response.data
  }
}
