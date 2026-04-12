export interface Course {
  id: number
  name: string
  description: string
  price: number
  discount?: number
  slug: string
  thumbnail?: string
  length?: number
}

export interface CourseDetail extends Course {
  prerequisites?: string[]
  tags?: string[]
  videos?: Video[]
  materials?: CourseMaterial[]
}

export interface Video {
  id: number
  title: string
  serial_number: number
  is_preview?: boolean
  video_url?: string
  description?: string
}

export interface CourseMaterial {
  id: number
  title: string
  file: string
  description?: string
}

export interface User {
  id: number
  username: string
  email: string
  first_name?: string
  last_name?: string
  is_teacher?: boolean
}
