package com.easycode.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response body for login and signup.
 * Matches frontend expectation: { access, refresh, user? }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** Short-lived JWT access token */
    private String access;

    /** Long-lived JWT refresh token */
    private String refresh;

    /** Current user info embedded in the auth response */
    private UserVO user;
}
