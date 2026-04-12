package com.easycode.backend.controller;

import com.easycode.backend.common.Result;
import com.easycode.backend.dto.request.LoginRequest;
import com.easycode.backend.dto.request.SignupRequest;
import com.easycode.backend.dto.request.TokenRefreshRequest;
import com.easycode.backend.dto.response.AuthResponse;
import com.easycode.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints matching the frontend's expected paths:
 *   POST /api/token/          — login
 *   POST /api/signup/         — register
 *   POST /api/token/refresh/  — token rotation
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/token/
     * Login and receive JWT access + refresh tokens.
     */
    @PostMapping("/api/token/")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/signup/
     * Register a new user account.
     */
    @PostMapping("/api/signup/")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/token/refresh/
     * Exchange a valid refresh token for a new access token (with token rotation).
     */
    @PostMapping("/api/token/refresh/")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
