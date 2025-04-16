package pt.teus.backend.dto.response.room;

import pt.teus.backend.dto.request.room.LocationData;
import pt.teus.backend.entity.enums.BedSize;
import pt.teus.backend.entity.enums.PayPeriod;
import pt.teus.backend.entity.enums.RoomPrivacyType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RoomResponseDto(

        Long id,
        String title,
        String uniqueSlug,
        String area,
        BigDecimal prepayment,
        BigDecimal rentalFee,
        PayPeriod payPeriod,
        String description,
        LocationData location,
        LocalDate availableFrom,
        Boolean isSmokingForbidden,
        Boolean isPetForbidden,
        Boolean isFurnished,
        Boolean isOnlyForWomen,
        Boolean ownerLivesHere,
        Boolean hasCleaningService,
        Boolean hasWifi,
        Integer maxOccupancy,
        BedSize bedSize,
        RoomPrivacyType roomPrivacyType,
        Boolean hasAirConditioner,
        List<String> photoUrls,
        String mapUrl,
        String ownerContact // Include only for authenticated and upgraded users.
) {
}
