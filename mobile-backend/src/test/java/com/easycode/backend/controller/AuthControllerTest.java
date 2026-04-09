package com.easycode.backend.controller;

import com.easycode.backend.entity.Profile;
import com.easycode.backend.entity.User;
import com.easycode.backend.repository.ProfileRepository;
import com.easycode.backend.repository.RefreshTokenRepository;
import com.easycode.backend.repository.UserRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController — covers the frontend auth.ts endpoints:
 *   POST /api/token/         (login)
 *   POST /api/signup/        (register)
 *   POST /api/token/refresh/ (token rotation)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired ProfileRepository profileRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private static final String USERNAME = "testuser";
    private static final String EMAIL    = "test@example.com";
    private static final String PASSWORD = "Password123!";

    @BeforeEach
    void setUp() {
        // Create a test user with profile
        User user = User.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .isActive(true)
                .build();
        user = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(user)
                .role("STUDENT")
                .build();
        profileRepository.save(profile);
    }

    // ── POST /api/token/ (login) ─────────────────────────────────────────────

    @Test
    void login_validCredentials_returnsAccessAndRefreshTokens() throws Exception {
        mockMvc.perform(post("/api/token/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", USERNAME,
                                "password", PASSWORD
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access").isString())
                .andExpect(jsonPath("$.refresh").isString())
                .andExpect(jsonPath("$.user.username").value(USERNAME))
                .andExpect(jsonPath("$.user.email").value(EMAIL));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        mockMvc.perform(post("/api/token/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", USERNAME,
                                "password", "wrongpassword"
                        ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_unknownUser_returns401() throws Exception {
        mockMvc.perform(post("/api/token/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "nobody",
                                "password", PASSWORD
                        ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/token/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ── POST /api/signup/ (register) ─────────────────────────────────────────

    @Test
    void signup_newUser_returns201WithTokens() throws Exception {
        mockMvc.perform(post("/api/signup/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "newuser",
                                "email",    "newuser@example.com",
                                "password",  "Secure123!",
                                "password2", "Secure123!"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access").isString())
                .andExpect(jsonPath("$.refresh").isString())
                .andExpect(jsonPath("$.user.username").value("newuser"));
    }

    @Test
    void signup_passwordMismatch_returns400() throws Exception {
        mockMvc.perform(post("/api/signup/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username",  "anotheruser",
                                "email",     "another@example.com",
                                "password",  "Secure123!",
                                "password2", "Different!"
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signup_duplicateUsername_returns400() throws Exception {
        mockMvc.perform(post("/api/signup/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username",  USERNAME,   // already exists
                                "email",     "other@example.com",
                                "password",  "Secure123!",
                                "password2", "Secure123!"
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signup_duplicateEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/signup/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username",  "uniqueuser",
                                "email",     EMAIL,        // already exists
                                "password",  "Secure123!",
                                "password2", "Secure123!"
                        ))))
                .andExpect(status().isBadRequest());
    }

    // ── POST /api/token/refresh/ (token rotation) ────────────────────────────

    @Test
    void tokenRefresh_validRefreshToken_returnsNewTokens() throws Exception {
        // First login to get a refresh token
        MvcResult loginResult = mockMvc.perform(post("/api/token/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", USERNAME,
                                "password", PASSWORD
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        String body = loginResult.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(body).get("refresh").asText();

        // Use the refresh token
        mockMvc.perform(post("/api/token/refresh/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refresh", refreshToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access").isString())
                .andExpect(jsonPath("$.refresh").isString());
    }

    @Test
    void tokenRefresh_invalidToken_returns401() throws Exception {
        mockMvc.perform(post("/api/token/refresh/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refresh", "not.a.valid.token"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenRefresh_missingRefreshField_returns400() throws Exception {
        mockMvc.perform(post("/api/token/refresh/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
