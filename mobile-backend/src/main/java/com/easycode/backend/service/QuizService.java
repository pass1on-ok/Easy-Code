package com.easycode.backend.service;

import com.easycode.backend.dto.request.QuizSubmitRequest;
import com.easycode.backend.dto.response.CourseProgressVO;
import com.easycode.backend.dto.response.QuizResultVO;
import com.easycode.backend.dto.response.QuizVO;

public interface QuizService {

    QuizVO getQuiz(Long videoId, String username);

    QuizResultVO submitQuiz(Long videoId, String username, QuizSubmitRequest request);

    CourseProgressVO getCourseProgress(String slug, String username);
}

