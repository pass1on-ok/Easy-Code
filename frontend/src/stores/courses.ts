import { defineStore } from 'pinia'
import { ref } from 'vue'
import { coursesService } from '@/services/courses'
import type { Course, CourseDetail } from '@/types'

export const useCoursesStore = defineStore('courses', () => {
  const courses = ref<Course[]>([])
  const currentCourse = ref<CourseDetail | null>(null)
  const purchasedCourses = ref<Course[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchCourses() {
    loading.value = true
    error.value = null
    try {
      courses.value = await coursesService.getCourses()
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch courses'
    } finally {
      loading.value = false
    }
  }

  async function fetchCourseBySlug(slug: string) {
    loading.value = true
    error.value = null
    try {
      currentCourse.value = await coursesService.getCourseBySlug(slug)
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch course'
    } finally {
      loading.value = false
    }
  }

  async function fetchPurchasedCourses() {
    loading.value = true
    error.value = null
    try {
      purchasedCourses.value = await coursesService.getPurchasedCourses()
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch purchased courses'
    } finally {
      loading.value = false
    }
  }

  async function enrollCourse(slug: string) {
    loading.value = true
    error.value = null
    try {
      await coursesService.enrollCourse(slug)
      await fetchPurchasedCourses()
      return true
    } catch (err: any) {
      error.value = err.message || 'Failed to enroll'
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    courses,
    currentCourse,
    purchasedCourses,
    loading,
    error,
    fetchCourses,
    fetchCourseBySlug,
    fetchPurchasedCourses,
    enrollCourse
  }
})
