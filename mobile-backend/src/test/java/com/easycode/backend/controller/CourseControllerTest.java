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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CourseController — covers the frontend courses.ts endpoints:
 *   GET  /api/courses/
 *   GET  /api/course/{slug}/
 *   GET  /api/purchased-courses/
 *   POST /check-out/{slug}/
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired ProfileRepository profileRepository;
    @Autowired CourseRepository courseRepository;
    @Autowired UserCourseRepository userCourseRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtService jwtService;

    private User testUser;
    private Course freeCourse;
    private Course paidCourse;
    private String accessToken;

    @BeforeEach
    void setUp() {
        // Create student
        testUser = User.builder()
                .username("student1")
                .email("student1@example.com")
                .password(passwordEncoder.encode("Pass123!"))
                .isActive(true)
                .build();
        testUser = userRepository.save(testUser);

        Profile profile = Profile.builder().user(testUser).role("STUDENT").build();
        profileRepository.save(profile);
        testUser.setProfile(profile);

        accessToken = jwtService.generateAccessToken(testUser);

        // Create a free published course
        freeCourse = courseRepository.save(Course.builder()
                .name("Free Java Course")
                .slug("free-java")
                .description("Learn Java for free")
                .price(0)
                .discount(0)
                .isPublished(true)
                .build());

        // Create a paid published course
        paidCourse = courseRepository.save(Course.builder()
                .name("Paid Python Course")
                .slug("paid-python")
                .description("Advanced Python")
                .price(5000)
                .discount(10)
                .isPublished(true)
                .build());
    }

    // ── GET /api/courses/ ────────────────────────────────────────────────────

    @Test
    void getCourses_public_returnsListWithNameAndSlug() throws Exception {
        mockMvc.perform(get("/api/courses/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].name", hasItems("Free Java Course", "Paid Python Course")))
                .andExpect(jsonPath("$[*].slug", hasItems("free-java", "paid-python")));
    }

    @Test
    void getCourses_noAuthRequired_returnsOk() throws Exception {
        // No Authorization header — should still work (public endpoint)
        mockMvc.perform(get("/api/courses/"))
                .andExpect(status().isOk());
    }

    // ── GET /api/course/{slug}/ ──────────────────────────────────────────────

    @Test
    void getCourse_existingSlug_returnsDetail() throws Exception {
        mockMvc.perform(get("/api/course/free-java/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("free-java"))
                .andExpect(jsonPath("$.name").value("Free Java Course"))
                .andExpect(jsonPath("$.is_enrolled").value(false));
    }

    @Test
    void getCourse_enrolledUser_isEnrolledTrue() throws Exception {
        // Enroll user manually
        userCourseRepository.save(UserCourse.builder()
                .user(testUser)
                .course(freeCourse)
                .build());

        mockMvc.perform(get("/api/course/free-java/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_enrolled").value(true));
    }

    @Test
    void getCourse_nonExistentSlug_returns404() throws Exception {
        mockMvc.perform(get("/api/course/does-not-exist/"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/purchased-courses/ ─────────────────────────────────────────

    @Test
    void getPurchasedCourses_withEnrollment_returnsCourse() throws Exception {
        userCourseRepository.save(UserCourse.builder()
                .user(testUser)
                .course(freeCourse)
                .build());

        mockMvc.perform(get("/api/purchased-courses/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].slug").value("free-java"));
    }

    @Test
    void getPurchasedCourses_noEnrollments_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/purchased-courses/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getPurchasedCourses_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/purchased-courses/"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /check-out/{slug}/ (free enroll) ───────────────────────────────

    @Test
    void enrollFreeCourse_validFreeSlug_returns200WithMessage() throws Exception {
        mockMvc.perform(post("/check-out/free-java/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void enrollFreeCourse_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/check-out/free-java/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enrollFreeCourse_nonExistentCourse_returns404() throws Exception {
        mockMvc.perform(post("/check-out/nonexistent/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
