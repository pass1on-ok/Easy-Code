package com.easycode.backend.service;

import com.easycode.backend.dto.request.ConfirmPaymentRequest;
import com.easycode.backend.dto.response.CheckoutResponse;
import com.easycode.backend.dto.response.CourseVO;

public interface PaymentService {

    CheckoutResponse createCheckoutSession(String slug, String username);

    CourseVO confirmPayment(ConfirmPaymentRequest request, String username);

    String getPaymentMode();
}
