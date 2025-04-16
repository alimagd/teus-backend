package pt.teus.backend.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}

