package pt.teus.backend.dto.response.user;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String email,
        String fullName,
        String phone,
        String userRole,
        LocalDateTime createdAt,
        boolean isPremium
) {}
