import api from './api'

export const paymentService = {
  async createCheckoutSession(courseSlug: string) {
    const response = await api.post(`/api/create-checkout/${courseSlug}/`)
    return response.data
  },

  async confirmPayment(sessionId: string, courseId: number) {
    const response = await api.post('/api/confirm-payment/', {
      session_id: sessionId,
      course_id: courseId
    })
    return response.data
  },

  async getStripePublicKey() {
    const response = await api.get('/api/stripe-key/')
    return response.data
  }
}
