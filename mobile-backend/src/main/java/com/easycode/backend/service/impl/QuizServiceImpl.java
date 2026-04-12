package com.easycode.backend.service.impl;

import com.easycode.backend.dto.request.QuizSubmitRequest;
import com.easycode.backend.dto.response.*;
import com.easycode.backend.entity.*;
import com.easycode.backend.exception.NotFoundException;
import com.easycode.backend.repository.*;
import com.easycode.backend.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final VideoProgressRepository videoProgressRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    @Override
    @Transactional(readOnly = true)
    public QuizVO getQuiz(Long videoId, String username) {
        List<QuizQuestion> questions = quizQuestionRepository.findByVideoIdOrderByIdAsc(videoId);
        String videoTitle = questions.isEmpty() ? null : questions.get(0).getVideo().getTitle();

        Boolean attempted = false;
        Boolean passed = false;
        if (username != null) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                var attempt = quizAttemptRepository.findByUserIdAndVideoId(user.getId(), videoId);
                attempted = attempt.isPresent();
                passed = attempt.map(QuizAttempt::isPassed).orElse(false);
            }
        }

        return QuizVO.builder()
                .videoId(videoId)
                .videoTitle(videoTitle)
                .questions(questions.stream().map(QuizQuestionVO::from).toList())
                .attempted(attempted)
                .passed(passed)
                .build();
    }

    @Override
    @Transactional
    public QuizResultVO submitQuiz(Long videoId, String username, QuizSubmitRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<QuizQuestion> questions = quizQuestionRepository.findByVideoIdOrderByIdAsc(videoId);
        if (questions.isEmpty()) {
            throw new NotFoundException("Quiz not found for this lesson");
        }

        Map<Long, Integer> answers = request.answers();
        int total = questions.size();
        int score = 0;

        List<QuizResultVO.QuestionResult> results = new java.util.ArrayList<>();
        for (QuizQuestion q : questions) {
            Integer userAnswer = answers.get(q.getId());
            boolean correct = userAnswer != null && userAnswer.equals(q.getCorrectIndex());
            if (correct) score++;
            results.add(QuizResultVO.QuestionResult.builder()
                    .questionId(q.getId())
                    .prompt(q.getPrompt())
                    .options(q.getOptions())
                    .userAnswer(userAnswer)
                    .correctAnswer(q.getCorrectIndex())
                    .correct(correct)
                    .build());
        }
        boolean passed = score >= Math.max(1, (int) Math.ceil(total * 0.7));

        Video video = questions.get(0).getVideo();
        QuizAttempt attempt = quizAttemptRepository.findByUserIdAndVideoId(user.getId(), videoId)
                .orElseGet(() -> QuizAttempt.builder().user(user).video(video).build());
        attempt.setScore(score);
        attempt.setTotal(total);
        attempt.setPassed(passed);
        quizAttemptRepository.save(attempt);

        return QuizResultVO.builder()
                .videoId(videoId)
                .score(score)
                .total(total)
                .passed(passed)
                .results(results)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseProgressVO getCourseProgress(String slug, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Course course = courseRepository.findBySlugWithDetails(slug)
                .orElseThrow(() -> new NotFoundException("Course not found: " + slug));

        int totalLessons = course.getVideos() != null ? course.getVideos().size() : 0;

        // Completed lessons
        Set<Long> courseVideoIds = course.getVideos().stream().map(Video::getId).collect(Collectors.toSet());
        int completedLessons = (int) videoProgressRepository.findByUserId(user.getId()).stream()
                .filter(VideoProgress::isCompleted)
                .filter(vp -> vp.getVideo() != null && courseVideoIds.contains(vp.getVideo().getId()))
                .count();

        // Tests availability
        List<Long> quizVideoIds = quizQuestionRepository.findDistinctVideoIdsByCourseId(course.getId());
        int testsTotal = quizVideoIds.size();

        // Completed tests = attempts on those videos
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdAndCourseId(user.getId(), course.getId());
        Set<Long> attemptedVideoIds = attempts.stream().map(a -> a.getVideo().getId()).collect(Collectors.toSet());
        int testsCompleted = (int) quizVideoIds.stream().filter(attemptedVideoIds::contains).count();
        int testsPassed = (int) attempts.stream().filter(QuizAttempt::isPassed).count();

        return CourseProgressVO.builder()
                .slug(slug)
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .testsTotal(testsTotal)
                .testsCompleted(testsCompleted)
                .testsPassed(testsPassed)
                .build();
    }
}

