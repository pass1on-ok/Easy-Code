package com.easycode.backend.dto.response;

import com.easycode.backend.entity.QuizQuestion;
import lombok.Builder;

import java.util.List;

@Builder
public record QuizQuestionVO(
        Long id,
        String prompt,
        List<String> options
) {
    public static QuizQuestionVO from(QuizQuestion q) {
        return QuizQuestionVO.builder()
                .id(q.getId())
                .prompt(q.getPrompt())
                .options(q.getOptions())
                .build();
    }
}

