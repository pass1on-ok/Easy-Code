package com.easycode.backend.controller;

import com.easycode.backend.dto.request.QuizSubmitRequest;
import com.easycode.backend.dto.response.CourseProgressVO;
import com.easycode.backend.dto.response.QuizResultVO;
import com.easycode.backend.dto.response.QuizVO;
import com.easycode.backend.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Quiz endpoints:
 *   GET  /api/videos/{videoId}/quiz
 *   POST /api/videos/{videoId}/quiz/submit
 *   GET  /api/course/{slug}/progress
 */
@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/api/videos/{videoId}/quiz")
    public ResponseEntity<QuizVO> getQuiz(
            @PathVariable Long videoId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(quizService.getQuiz(videoId, username));
    }

    @PostMapping("/api/videos/{videoId}/quiz/submit")
    public ResponseEntity<QuizResultVO> submitQuiz(
            @PathVariable Long videoId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuizSubmitRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(quizService.submitQuiz(videoId, userDetails.getUsername(), request));
    }

    @GetMapping("/api/course/{slug}/progress")
    public ResponseEntity<CourseProgressVO> getCourseProgress(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(quizService.getCourseProgress(slug, userDetails.getUsername()));
    }
}

