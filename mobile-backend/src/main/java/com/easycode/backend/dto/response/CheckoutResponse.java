package com.easycode.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for POST /api/create-checkout/{slug}/
 * Frontend expects: { sessionId, url, publishableKey? }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

    private String sessionId;
    private String url;
    private String publishableKey;
}
