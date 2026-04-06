import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import HomeView from '@/views/HomeView.vue'
import NotificationsView from '@/views/NotificationsView.vue'
import ProfileView from '@/views/ProfileView.vue'
import CoursesView from '@/views/CoursesView.vue'
import CourseDetailView from '@/views/CourseDetailView.vue'
import MyCoursesView from '@/views/MyCoursesView.vue'
import LoginView from '@/views/LoginView.vue'
import SignupView from '@/views/SignupView.vue'
import PaymentSuccessView from '@/views/PaymentSuccessView.vue'
import PaymentCancelledView from '@/views/PaymentCancelledView.vue'
import CheckoutView from '@/views/CheckoutView.vue'
import TeacherView from '@/views/TeacherView.vue'
import AboutView from '@/views/AboutView.vue'
import AIChatView from '@/views/AIChatView.vue'
import EditProfileView from '@/views/EditProfileView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/notifications',
      name: 'notifications',
      component: NotificationsView,
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
    },
    {
      path: '/courses',
      name: 'courses',
      component: CoursesView,
    },
    {
      path: '/course',
      redirect: { name: 'courses' },
    },
    {
      path: '/course/:slug',
      name: 'course-detail',
      component: CourseDetailView,
    },
    {
      path: '/checkout/:slug',
      name: 'checkout',
      component: CheckoutView,
      meta: { requiresAuth: true },
    },
    {
      path: '/teacher',
      name: 'teacher',
      component: TeacherView,
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView,
    },
    {
      path: '/ai-teacher',
      name: 'ai-chat',
      component: AIChatView,
    },
    {
      path: '/profile/edit',
      name: 'edit-profile',
      component: EditProfileView,
      meta: { requiresAuth: true },
    },
    {
      path: '/my-courses',
      name: 'my-courses',
      component: MyCoursesView,
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresGuest: true },
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupView,
      meta: { requiresGuest: true },
    },
    {
      path: '/payment-success',
      name: 'payment-success',
      component: PaymentSuccessView,
    },
    {
      path: '/payment-cancelled',
      name: 'payment-cancelled',
      component: PaymentCancelledView,
    },
  ],
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some((r) => r.meta.requiresAuth)
  const requiresGuest = to.matched.some((r) => r.meta.requiresGuest)

  if (requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (requiresGuest && authStore.isAuthenticated) {
    return { name: 'home' }
  }
})

export default router
