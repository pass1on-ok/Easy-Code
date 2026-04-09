package com.easycode.backend.controller;

import com.easycode.backend.entity.*;
import com.easycode.backend.repository.*;
import com.easycode.backend.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PaymentController — covers the frontend payment.ts endpoints:
 *   POST /api/create-checkout/{slug}/
 *   POST /api/confirm-payment/
 *   GET  /api/payment-config/
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired ProfileRepository profileRepository;
    @Autowired CourseRepository courseRepository;
    @Autowired UserPaymentRepository userPaymentRepository;
    @Autowired UserCourseRepository userCourseRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtService jwtService;

    private User testUser;
    private Course paidCourse;
    private String accessToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("payer1")
                .email("payer1@example.com")
                .password(passwordEncoder.encode("Pass123!"))
                .isActive(true)
                .build();
        testUser = userRepository.save(testUser);

        Profile profile = Profile.builder().user(testUser).role("STUDENT").build();
        profileRepository.save(profile);
        testUser.setProfile(profile);

        accessToken = jwtService.generateAccessToken(testUser);

        paidCourse = courseRepository.save(Course.builder()
                .name("Paid Spring Course")
                .slug("paid-spring")
                .description("Spring Boot in depth")
                .price(10000)
                .discount(0)
                .isPublished(true)
                .build());
    }

    // ── GET /api/payment-config/ ─────────────────────────────────────────────

    @Test
    void getPaymentConfig_public_returnsMockMode() throws Exception {
        mockMvc.perform(get("/api/payment-config/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("mock"));
    }

    // ── POST /api/create-checkout/{slug}/ ────────────────────────────────────

    @Test
    void createCheckout_paidCourse_returnsSessionIdAndUrl() throws Exception {
        mockMvc.perform(post("/api/create-checkout/paid-spring/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.session_id").isString())
                .andExpect(jsonPath("$.url").isString());
    }

    @Test
    void createCheckout_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/create-checkout/paid-spring/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCheckout_nonExistentCourse_returns404() throws Exception {
        mockMvc.perform(post("/api/create-checkout/no-course/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCheckout_freeCourse_returns400() throws Exception {
        courseRepository.save(Course.builder()
                .name("Free Course")
                .slug("free-course")
                .description("Free")
                .price(0)
                .discount(0)
                .isPublished(true)
                .build());

        mockMvc.perform(post("/api/create-checkout/free-course/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCheckout_alreadyEnrolled_returns400() throws Exception {
        // Enroll the user first
        userCourseRepository.save(UserCourse.builder()
                .user(testUser)
                .course(paidCourse)
                .build());

        mockMvc.perform(post("/api/create-checkout/paid-spring/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ── POST /api/confirm-payment/ ───────────────────────────────────────────

    @Test
    void confirmPayment_validSession_returnsMessageAndCourse() throws Exception {
        // Create checkout first to get a session ID
        String createResponse = mockMvc.perform(post("/api/create-checkout/paid-spring/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String sessionId = objectMapper.readTree(createResponse).get("session_id").asText();

        // Confirm the payment
        mockMvc.perform(post("/api/confirm-payment/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "session_id", sessionId,
                                "course_id",  paidCourse.getId()
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.course.slug").value("paid-spring"));
    }

    @Test
    void confirmPayment_invalidSession_returns404() throws Exception {
        mockMvc.perform(post("/api/confirm-payment/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "session_id", "fake-session-id",
                                "course_id",  paidCourse.getId()
                        ))))
                .andExpect(status().isNotFound());
    }

    @Test
    void confirmPayment_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/confirm-payment/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "session_id", "any-session",
                                "course_id",  1L
                        ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void confirmPayment_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/confirm-payment/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
