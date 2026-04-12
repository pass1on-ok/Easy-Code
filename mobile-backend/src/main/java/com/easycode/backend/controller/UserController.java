package com.easycode.backend.controller;

import com.easycode.backend.dto.request.UpdateProfileRequest;
import com.easycode.backend.dto.response.UserVO;
import com.easycode.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * User profile endpoints matching the frontend's expected paths:
 *   GET   /user/api/me/  — get current user
 *   PATCH /user/api/me/  — update profile
 */
@RestController
@RequestMapping("/user/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /user/api/me/
     * Returns the authenticated user's profile.
     */
    @GetMapping("/me/")
    public ResponseEntity<UserVO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserVO user = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    /**
     * PATCH /user/api/me/
     * Partially update the authenticated user's profile.
     */
    @PatchMapping("/me/")
    public ResponseEntity<UserVO> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserVO user = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(user);
    }
}
