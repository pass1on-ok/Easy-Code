package com.easycode.backend.service.impl;

import com.easycode.backend.dto.response.CourseDetailVO;
import com.easycode.backend.dto.response.CourseVO;
import com.easycode.backend.dto.response.PurchasedCourseVO;
import com.easycode.backend.entity.Course;
import com.easycode.backend.entity.User;
import com.easycode.backend.entity.UserCourse;
import com.easycode.backend.exception.BadRequestException;
import com.easycode.backend.exception.NotFoundException;
import com.easycode.backend.repository.CourseRepository;
import com.easycode.backend.repository.UserCourseRepository;
import com.easycode.backend.repository.UserRepository;
import com.easycode.backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseVO> getAllCourses() {
        return courseRepository.findByIsPublishedTrue()
                .stream()
                .map(CourseVO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailVO getCourseBySlug(String slug, String username) {
        Course course = courseRepository.findBySlugWithDetails(slug)
                .orElseThrow(() -> new NotFoundException("Course not found: " + slug));

        boolean enrolled = false;
        if (username != null) {
            userRepository.findByUsername(username).ifPresent(user ->
                    userCourseRepository.existsByUserIdAndCourseId(user.getId(), course.getId()));
            // Re-evaluate after potential null
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                enrolled = userCourseRepository.existsByUserIdAndCourseId(user.getId(), course.getId());
            }
        }

        return CourseDetailVO.from(course, enrolled);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchasedCourseVO> getPurchasedCourses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return userCourseRepository.findByUserIdWithCourse(user.getId())
                .stream()
                .map(PurchasedCourseVO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void enrollFreeCourse(String slug, String username) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Course not found: " + slug));

        if (course.getPrice() != null && course.getFinalPrice() > 0) {
            throw new BadRequestException("This course requires payment. Please use the checkout flow.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (userCourseRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new BadRequestException("You are already enrolled in this course");
        }

        UserCourse enrollment = UserCourse.builder()
                .user(user)
                .course(course)
                .completed(false)
                .build();
        userCourseRepository.save(enrollment);
    }
}
