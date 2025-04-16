package pt.teus.backend.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import pt.teus.backend.entity.user.UserRole;

public record RegistrationRequestDto(
        @NotNull(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotNull(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phone number")
        String phone,

        @NotNull(message = "Password is required")
        String password,

        String fullName, // ✅ Optional, no @NotNull

        UserRole userRole, // ✅ Optional, no @NotNull (Backend should set a default if null)

        @NotNull(message = "You must accept the terms and conditions")
        Boolean acceptedTerms
) {
}
