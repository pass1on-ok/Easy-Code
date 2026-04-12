package com.easycode.backend.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record QuizResultVO(
        Long videoId,
        Integer score,
        Integer total,
        Boolean passed,
        List<QuestionResult> results
) {
    @Builder
    public record QuestionResult(
            Long questionId,
            String prompt,
            List<String> options,
            Integer userAnswer,
            Integer correctAnswer,
            Boolean correct
    ) {}
}
