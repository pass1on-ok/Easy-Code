package com.easycode.backend.controller;

import com.easycode.backend.entity.Profile;
import com.easycode.backend.entity.User;
import com.easycode.backend.repository.ProfileRepository;
import com.easycode.backend.repository.UserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController — covers the frontend auth.ts user profile endpoints:
 *   GET   /user/api/me/
 *   PATCH /user/api/me/
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired ProfileRepository profileRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtService jwtService;

    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("profile_user")
                .email("profile@example.com")
                .password(passwordEncoder.encode("Pass123!"))
                .firstName("Ada")
                .lastName("Lovelace")
                .isActive(true)
                .build();
        testUser = userRepository.save(testUser);

        Profile profile = Profile.builder().user(testUser).role("STUDENT").build();
        profileRepository.save(profile);
        testUser.setProfile(profile);

        accessToken = jwtService.generateAccessToken(testUser);
    }

    // ── GET /user/api/me/ ────────────────────────────────────────────────────

    @Test
    void getMe_authenticated_returnsUserWithSnakeCaseFields() throws Exception {
        mockMvc.perform(get("/user/api/me/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                // Jackson SNAKE_CASE naming strategy must produce these keys
                .andExpect(jsonPath("$.username").value("profile_user"))
                .andExpect(jsonPath("$.email").value("profile@example.com"))
                .andExpect(jsonPath("$.first_name").value("Ada"))
                .andExpect(jsonPath("$.last_name").value("Lovelace"))
                .andExpect(jsonPath("$.is_teacher").value(false))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void getMe_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/user/api/me/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMe_expiredToken_returns401() throws Exception {
        // An obviously invalid/expired token
        mockMvc.perform(get("/user/api/me/")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    // ── PATCH /user/api/me/ ──────────────────────────────────────────────────

    @Test
    void updateProfile_validFields_returnsUpdatedUser() throws Exception {
        mockMvc.perform(patch("/user/api/me/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "first_name", "Grace",
                                "last_name",  "Hopper"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("Grace"))
                .andExpect(jsonPath("$.last_name").value("Hopper"));
    }

    @Test
    void updateProfile_emailUpdate_returnsUpdatedEmail() throws Exception {
        mockMvc.perform(patch("/user/api/me/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "new@example.com"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void updateProfile_emptyBody_returnsCurrentUser() throws Exception {
        // Empty PATCH should be a no-op, not an error
        mockMvc.perform(patch("/user/api/me/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("profile_user"));
    }

    @Test
    void updateProfile_unauthenticated_returns401() throws Exception {
        mockMvc.perform(patch("/user/api/me/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("first_name", "Test"))))
                .andExpect(status().isUnauthorized());
    }
}
