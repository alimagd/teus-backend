package pt.teus.backend.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpgradeRequestDto(
        @NotNull @Email String email,
        @NotNull Boolean acceptedTerms,  // Must accept T&C
        @NotNull BigDecimal amountPaid   // Payment amount
) {}
