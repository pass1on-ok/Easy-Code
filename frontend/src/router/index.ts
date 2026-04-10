import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('../views/SignupView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/my-courses',
      name: 'my-courses',
      component: () => import('../views/MyCoursesView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
    },
    {
      path: '/teacher',
      name: 'teacher',
      component: () => import('../views/TeacherView.vue'),
    },
    {
      path: '/course/:slug',
      name: 'course-detail',
      component: () => import('../views/CourseDetailView.vue'),
    },
    {
      path: '/checkout/:slug',
      name: 'checkout',
      component: () => import('../views/CheckoutView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/payment-success',
      name: 'payment-success',
      component: () => import('../views/PaymentSuccessView.vue'),
    },
    {
      path: '/payment-cancelled',
      name: 'payment-cancelled',
      component: () => import('../views/PaymentCancelledView.vue'),
    },
  ],
})

// Navigation guards
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // Initialize auth store on first navigation
  if (!authStore.user && authStore.isAuthenticated) {
    await authStore.initialize()
  }
  
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)
  const requiresGuest = to.matched.some((record) => record.meta.requiresGuest)

  if (requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (requiresGuest && authStore.isAuthenticated) {
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router
