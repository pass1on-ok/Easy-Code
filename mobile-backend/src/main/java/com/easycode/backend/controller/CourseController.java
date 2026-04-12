package com.easycode.backend.controller;

import com.easycode.backend.common.Result;
import com.easycode.backend.dto.response.CourseDetailVO;
import com.easycode.backend.dto.response.CourseVO;
import com.easycode.backend.dto.response.PurchasedCourseVO;
import com.easycode.backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Course endpoints:
 *   GET  /api/courses/           — public course list
 *   GET  /api/course/{slug}/     — public course detail (enrollment-aware)
 *   GET  /api/purchased-courses/ — authenticated user's enrolled courses
 *   POST /check-out/{slug}/      — enroll in a free course
 */
@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * GET /api/courses/
     * Returns all published courses (public).
     */
    @GetMapping("/api/courses/")
    public ResponseEntity<List<CourseVO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * GET /api/course/{slug}/
     * Returns full course detail. If authenticated, marks enrolled videos/materials.
     */
    @GetMapping("/api/course/{slug}/")
    public ResponseEntity<CourseDetailVO> getCourse(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = (userDetails != null) ? userDetails.getUsername() : null;
        return ResponseEntity.ok(courseService.getCourseBySlug(slug, username));
    }

    /**
     * GET /api/purchased-courses/
     * Returns courses the authenticated user is enrolled in.
     */
    @GetMapping("/api/purchased-courses/")
    public ResponseEntity<List<PurchasedCourseVO>> getPurchasedCourses(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(courseService.getPurchasedCourses(userDetails.getUsername()));
    }

    /**
     * POST /check-out/{slug}/
     * Enroll in a free course (price = 0). Paid courses must go through Stripe.
     */
    @PostMapping("/check-out/{slug}/")
    public ResponseEntity<Map<String, String>> enrollFreeCourse(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        courseService.enrollFreeCourse(slug, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Successfully enrolled in the course"));
    }
}
