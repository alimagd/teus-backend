package pt.teus.backend.dto.request.room;

import jakarta.validation.constraints.*;
import pt.teus.backend.entity.enums.BedSize;
import pt.teus.backend.entity.enums.PayPeriod;
import pt.teus.backend.entity.enums.RoomPrivacyType;
import pt.teus.backend.validation.NoPhoneNumber;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RoomCreationRequestDto(

        @NotNull(message = "Title is required")
        @Size(min = 1, max = 50, message = "Title max length is 50 and min is 1")
        String title,

        @DecimalMin(value = "5")
        @DecimalMax(value = "1000")
        BigDecimal area,

        @DecimalMin(value = "0.0", inclusive = false, message = "Prepayment must be greater than zero")
        BigDecimal prepayment,

        @DecimalMin(value = "0.0", inclusive = false, message = "Rental fee must be greater than zero")
        @NotNull(message = "rentalFee is required")
        BigDecimal rentalFee,

        @NotNull(message = "Time payment is required")
        PayPeriod payPeriod,

        @NotNull(message = "isSmokingForbidden is required")
        Boolean isSmokingForbidden,
        @NotNull(message = "isPetForbidden is required")
        Boolean isPetForbidden,
        @NotNull(message = "isFurnished is required")
        Boolean isFurnished,

        Boolean isOnlyForWomen,

        Boolean ownerLivesHere,

        Boolean hasCleaningService,

        Boolean hasWifi,
        @NotNull(message = "maxOccupancy is required")
        Integer maxOccupancy,

        BedSize bedSize,
        @NotNull(message = "roomPrivacyType is required")
        RoomPrivacyType roomPrivacyType,

        Boolean hasAirConditioner,

        @Size(max = 300, message = "Description can have a maximum of 300 characters")
        @NoPhoneNumber
        String description,

        @NotNull(message = "Location is required")
        LocationData location,

        @FutureOrPresent(message = "Available from date must be today or in the future")
        LocalDate availableFrom

) {}

