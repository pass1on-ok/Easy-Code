package com.easycode.backend.controller;

import com.easycode.backend.dto.request.ConfirmPaymentRequest;
import com.easycode.backend.dto.response.CheckoutResponse;
import com.easycode.backend.dto.response.CourseVO;
import com.easycode.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Payment endpoints:
 *   POST /api/create-checkout/{slug}/  — create checkout session
 *   POST /api/confirm-payment/         — confirm payment and enroll user
 *   GET  /api/payment-config/          — return payment configuration
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /api/create-checkout/{slug}/
     * Creates a Stripe Checkout Session and returns the redirect URL.
     */
    @PostMapping("/create-checkout/{slug}/")
    public ResponseEntity<CheckoutResponse> createCheckout(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CheckoutResponse response = paymentService.createCheckoutSession(slug, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/confirm-payment/
     * Verifies Stripe payment and grants course access to the user.
     */
    @PostMapping("/confirm-payment/")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @Valid @RequestBody ConfirmPaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CourseVO course = paymentService.confirmPayment(request, userDetails.getUsername());
        return ResponseEntity.ok(Map.of(
                "message", "Payment confirmed. You are now enrolled!",
                "course", course
        ));
    }

    /**
     * GET /api/payment-config/
     * Returns payment mode (mock/live) for the frontend.
     */
    @GetMapping("/payment-config/")
    public ResponseEntity<Map<String, String>> getPaymentConfig() {
        return ResponseEntity.ok(Map.of("mode", paymentService.getPaymentMode()));
    }
}
