import api from './api'

/** 创建 Stripe 结账会话 POST /api/create-checkout/<slug>/ 需登录 */
export async function createCheckoutSession(slug: string): Promise<{ sessionId: string; url: string; publishableKey?: string }> {
  const { data } = await api.post<{ sessionId: string; url: string; publishableKey?: string }>(`/api/create-checkout/${slug}/`)
  return data
}

/** 支付成功后确认 POST /api/confirm-payment/ 需登录 */
export async function confirmPayment(sessionId: string, courseId: number): Promise<{ message: string; course?: { id: number; name: string; slug: string } }> {
  const { data } = await api.post<{ message: string; course?: { id: number; name: string; slug: string } }>('/api/confirm-payment/', {
    session_id: sessionId,
    course_id: courseId,
  })
  return data
}

/** 支付模式 GET /api/payment-config/（mock / live） */
export async function getPaymentConfig(): Promise<{ mode: string }> {
  const { data } = await api.get<{ mode: string }>('/api/payment-config/')
  return data
}
