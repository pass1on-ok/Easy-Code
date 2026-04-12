package com.easycode.backend.repository;

import com.easycode.backend.entity.UserPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPaymentRepository extends JpaRepository<UserPayment, Long> {

    Optional<UserPayment> findByStripeSessionId(String stripeSessionId);

    boolean existsByUserIdAndCourseIdAndPaymentBoolTrue(Long userId, Long courseId);
}
