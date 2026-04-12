package com.easycode.backend.service;

import com.easycode.backend.dto.request.UpdateProfileRequest;
import com.easycode.backend.dto.response.UserVO;

public interface UserService {

    UserVO getCurrentUser(String username);

    UserVO updateProfile(String username, UpdateProfileRequest request);
}
