package com.eg.blpsreports.dto;

import java.time.Instant;

public record ErrorResponse(
        String message,
        Instant time
) {
}
