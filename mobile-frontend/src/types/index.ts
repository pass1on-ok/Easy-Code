export interface User {
  id: number
  username: string
  email: string
  first_name?: string
  last_name?: string
  is_teacher?: boolean
  bio?: string
  avatar?: string
}

export interface Course {
  id: number
  name: string
  description: string
  price: number
  discount?: number
  slug: string
  thumbnail?: string | null
  length?: number | null
}

export interface Video {
  id: number
  title: string
  serial_number: number
  is_preview?: boolean
  video_url?: string
  description?: string | null
}

export interface CourseMaterial {
  id: number
  title: string
  file: string | null
  description?: string
}

export interface CourseDetail extends Course {
  videos: Video[]
  materials: CourseMaterial[]
}

export interface PurchasedCourse extends Course {
  enrollment_date?: string
  completed?: boolean
  grade?: number | null
}

export interface QuizQuestion {
  id: number
  prompt: string
  options: string[]
}

export interface Quiz {
  video_id: number
  video_title?: string | null
  questions: QuizQuestion[]
  attempted?: boolean
  passed?: boolean
}

export interface QuizSubmitRequest {
  answers: Record<number, number>
}

export interface QuestionResult {
  question_id: number
  prompt: string
  options: string[]
  user_answer: number | null
  correct_answer: number
  correct: boolean
}

export interface QuizResult {
  video_id: number
  score: number
  total: number
  passed: boolean
  results: QuestionResult[]
}

export interface CourseProgress {
  slug: string
  total_lessons: number
  completed_lessons: number
  tests_total: number
  tests_completed: number
  tests_passed: number
}
