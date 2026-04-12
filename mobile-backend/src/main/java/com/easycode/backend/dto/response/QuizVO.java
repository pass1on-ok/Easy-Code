package com.easycode.backend.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record QuizVO(
        Long videoId,
        String videoTitle,
        List<QuizQuestionVO> questions,
        Boolean attempted,
        Boolean passed
) {
}

