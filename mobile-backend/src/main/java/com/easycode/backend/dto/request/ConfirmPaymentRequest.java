package com.easycode.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmPaymentRequest {

    @NotBlank(message = "session_id is required")
    private String session_id;

    @NotNull(message = "course_id is required")
    private Long course_id;
}
