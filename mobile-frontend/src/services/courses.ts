import api from './api'
import type { Course, CourseDetail, PurchasedCourse } from '@/types'

/** 课程列表 GET /api/courses/ */
export async function getCourses(): Promise<Course[]> {
  const { data } = await api.get<Course[]>('/api/courses/')
  return data
}

/** 课程详情 GET /api/course/<slug>/ */
export async function getCourseBySlug(slug: string): Promise<CourseDetail> {
  const { data } = await api.get<CourseDetail>(`/api/course/${slug}/`)
  return data
}

/** 已购课程 GET /api/purchased-courses/ 需登录 */
export async function getPurchasedCourses(): Promise<PurchasedCourse[]> {
  const { data } = await api.get<PurchasedCourse[]>('/api/purchased-courses/')
  return data
}

/** 报名课程 POST /check-out/<slug>/ 需登录 */
export async function enrollCourse(slug: string): Promise<{ message: string }> {
  const { data } = await api.post<{ message: string }>(`/check-out/${slug}/`)
  return data
}
