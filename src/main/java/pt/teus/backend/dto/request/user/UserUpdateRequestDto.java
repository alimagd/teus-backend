package pt.teus.backend.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDto(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Email is required ")
        String email
) {
}
