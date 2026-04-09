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
