package com.easycode.backend.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record QuizSubmitRequest(
        @NotNull Map<Long, Integer> answers
) {
}

