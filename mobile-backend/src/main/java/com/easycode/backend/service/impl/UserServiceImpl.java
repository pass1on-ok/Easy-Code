package com.easycode.backend.service.impl;

import com.easycode.backend.dto.request.UpdateProfileRequest;
import com.easycode.backend.dto.response.UserVO;
import com.easycode.backend.entity.Profile;
import com.easycode.backend.entity.User;
import com.easycode.backend.exception.BadRequestException;
import com.easycode.backend.exception.NotFoundException;
import com.easycode.backend.repository.ProfileRepository;
import com.easycode.backend.repository.UserRepository;
import com.easycode.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional(readOnly = true)
    public UserVO getCurrentUser(String username) {
        User user = userRepository.findByUsernameWithProfile(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return UserVO.from(user);
    }

    @Override
    @Transactional
    public UserVO updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsernameWithProfile(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Check email uniqueness if being changed
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName()  != null) user.setLastName(request.getLastName());
        userRepository.save(user);

        // Update profile extension
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Profile p = Profile.builder().user(user).role("STUDENT").build();
                    return profileRepository.save(p);
                });

        if (request.getBio()    != null) profile.setBio(request.getBio());
        if (request.getAvatar() != null) profile.setAvatar(request.getAvatar());
        profileRepository.save(profile);

        user.setProfile(profile);
        return UserVO.from(user);
    }
}
