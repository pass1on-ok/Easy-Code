package com.easycode.backend.service;

import com.easycode.backend.dto.request.LoginRequest;
import com.easycode.backend.dto.request.SignupRequest;
import com.easycode.backend.dto.request.TokenRefreshRequest;
import com.easycode.backend.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse signup(SignupRequest request);

    AuthResponse refreshToken(TokenRefreshRequest request);
}
