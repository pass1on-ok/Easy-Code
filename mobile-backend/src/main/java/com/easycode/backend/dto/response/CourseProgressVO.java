package com.easycode.backend.dto.response;

import lombok.Builder;

@Builder
public record CourseProgressVO(
        String slug,
        Integer totalLessons,
        Integer completedLessons,
        Integer testsTotal,
        Integer testsCompleted,
        Integer testsPassed
) {
}

