package org.example.dto;

import jakarta.validation.constraints.NotNull;

public record ErrorResponseDto(
        @NotNull(message = "errorMessage must not be null")
        String errorMessage) {
}
