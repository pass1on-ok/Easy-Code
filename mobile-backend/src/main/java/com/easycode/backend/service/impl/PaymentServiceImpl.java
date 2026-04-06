package com.easycode.backend.service.impl;

import com.easycode.backend.dto.request.ConfirmPaymentRequest;
import com.easycode.backend.dto.response.CheckoutResponse;
import com.easycode.backend.dto.response.CourseVO;
import com.easycode.backend.entity.Course;
import com.easycode.backend.entity.User;
import com.easycode.backend.entity.UserCourse;
import com.easycode.backend.entity.UserPayment;
import com.easycode.backend.exception.BadRequestException;
import com.easycode.backend.exception.NotFoundException;
import com.easycode.backend.repository.CourseRepository;
import com.easycode.backend.repository.UserCourseRepository;
import com.easycode.backend.repository.UserPaymentRepository;
import com.easycode.backend.repository.UserRepository;
import com.easycode.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserPaymentRepository userPaymentRepository;
    private final UserCourseRepository userCourseRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${payment.mode:mock}")
    private String paymentMode;

    @Override
    @Transactional
    public CheckoutResponse createCheckoutSession(String slug, String username) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Course not found: " + slug));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (userCourseRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new BadRequestException("You are already enrolled in this course");
        }

        int finalPrice = course.getFinalPrice();
        if (finalPrice <= 0) {
            throw new BadRequestException("This is a free course — use the free enrollment endpoint");
        }

        // Generate a unique session ID for this payment attempt
        String sessionId = UUID.randomUUID().toString();

        // Persist a pending payment record
        UserPayment payment = UserPayment.builder()
                .user(user)
                .course(course)
                .stripeSessionId(sessionId)
                .amount(finalPrice)
                .paymentBool(false)
                .build();
        userPaymentRepository.save(payment);

        log.info("Mock checkout session created: sessionId={}, user={}, course={}, amount={}KZT",
                sessionId, username, slug, finalPrice);

        String paymentUrl = frontendUrl + "/payment-success?session_id=" + sessionId + "&course_id=" + course.getId();

        return CheckoutResponse.builder()
                .sessionId(sessionId)
                .url(paymentUrl)
                .build();
    }

    @Override
    @Transactional
    public CourseVO confirmPayment(ConfirmPaymentRequest request, String username) {
        UserPayment payment = userPaymentRepository.findByStripeSessionId(request.getSession_id())
                .orElseThrow(() -> new NotFoundException("Payment session not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!payment.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Payment does not belong to current user");
        }

        if (payment.isPaymentBool()) {
            // Already confirmed — just return the course
            return CourseVO.from(payment.getCourse());
        }

        // Mock: mark payment as successful immediately
        payment.setPaymentBool(true);
        payment.setStripePaymentIntent("mock-txn-" + UUID.randomUUID().toString().substring(0, 8));
        userPaymentRepository.save(payment);

        // Enroll user in the course
        Course course = payment.getCourse();
        if (!userCourseRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            UserCourse enrollment = UserCourse.builder()
                    .user(user)
                    .course(course)
                    .completed(false)
                    .build();
            userCourseRepository.save(enrollment);
        }

        log.info("Mock payment confirmed: user={}, course={}", username, course.getSlug());

        return CourseVO.from(course);
    }

    @Override
    public String getPaymentMode() {
        return paymentMode;
    }
}