package com.ram.linkly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateUrlRequest(
        @NotBlank
        @Size(max = 2048)
        @Pattern(regexp = "^https?://.+", message = "must start with http:// or https://")
        String url,
        @Pattern(regexp = "^[a-zA-Z0-9_-]{3,32}$",
                message = "alias must be 3-32 letters, numbers, '-' or '_'")
        String alias,
        Instant expiresAt
) {
}
