import api from './api'
import type { CourseProgress, Quiz, QuizResult, QuizSubmitRequest } from '@/types'

export async function getQuiz(videoId: number): Promise<Quiz> {
  const { data } = await api.get<Quiz>(`/api/videos/${videoId}/quiz`)
  return data
}

export async function submitQuiz(videoId: number, payload: QuizSubmitRequest): Promise<QuizResult> {
  const { data } = await api.post<QuizResult>(`/api/videos/${videoId}/quiz/submit`, payload)
  return data
}

export async function getCourseProgress(slug: string): Promise<CourseProgress> {
  const { data } = await api.get<CourseProgress>(`/api/course/${slug}/progress`)
  return data
}

