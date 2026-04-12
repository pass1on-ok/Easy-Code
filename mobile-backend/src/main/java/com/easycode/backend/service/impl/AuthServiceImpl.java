package com.easycode.backend.service.impl;

import com.easycode.backend.dto.request.LoginRequest;
import com.easycode.backend.dto.request.SignupRequest;
import com.easycode.backend.dto.request.TokenRefreshRequest;
import com.easycode.backend.dto.response.AuthResponse;
import com.easycode.backend.dto.response.UserVO;
import com.easycode.backend.entity.Profile;
import com.easycode.backend.entity.RefreshToken;
import com.easycode.backend.entity.User;
import com.easycode.backend.exception.BadRequestException;
import com.easycode.backend.exception.UnauthorizedException;
import com.easycode.backend.repository.ProfileRepository;
import com.easycode.backend.repository.RefreshTokenRepository;
import com.easycode.backend.repository.UserRepository;
import com.easycode.backend.security.JwtService;
import com.easycode.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username or password");
        }

        User user = userRepository.findByUsernameWithProfile(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getPassword2())) {
            throw new BadRequestException("Passwords do not match");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(true)
                .build();
        user = userRepository.save(user);

        // Create default profile (STUDENT)
        Profile profile = Profile.builder()
                .user(user)
                .role("STUDENT")
                .build();
        profileRepository.save(profile);
        user.setProfile(profile);

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefresh();

        // Validate the token is in DB and not revoked
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid token type");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsernameWithProfile(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Revoke the old token and issue a new pair (rotation)
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        String newAccessToken  = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        saveRefreshToken(user, newRefreshToken);

        return AuthResponse.builder()
                .access(newAccessToken)
                .refresh(newRefreshToken)
                .build();
    }

    // ---- Helpers ----

    private AuthResponse buildAuthResponse(User user) {
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .access(accessToken)
                .refresh(refreshToken)
                .user(UserVO.from(user))
                .build();
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(token)
                .revoked(false)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build();
        refreshTokenRepository.save(rt);
    }
}
