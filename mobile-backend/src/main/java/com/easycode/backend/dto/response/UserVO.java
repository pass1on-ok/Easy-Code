package com.easycode.backend.dto.response;

import com.easycode.backend.entity.User;
import lombok.Builder;
import lombok.Data;

/**
 * Public-facing user view object.
 * Maps to the shape the frontend expects from /user/api/me/
 */
@Data
@Builder
public class UserVO {

    private Long id;
    private String username;
    private String email;
    private String firstName;   // JSON: first_name
    private String lastName;    // JSON: last_name
    private Boolean isTeacher;
    private String role;
    private String avatar;
    private String bio;

    public static UserVO from(User user) {
        String role = (user.getProfile() != null) ? user.getProfile().getRole() : "STUDENT";
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isTeacher("TEACHER".equals(role))
                .role(role)
                .avatar(user.getProfile() != null ? user.getProfile().getAvatar() : null)
                .bio(user.getProfile() != null ? user.getProfile().getBio() : null)
                .build();
    }
}
