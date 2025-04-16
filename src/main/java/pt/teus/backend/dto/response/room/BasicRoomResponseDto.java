package pt.teus.backend.dto.response.room;


import pt.teus.backend.entity.enums.BedSize;
import pt.teus.backend.entity.enums.RoomPrivacyType;

import java.math.BigDecimal;
import java.time.LocalDate;

// Basic information for public users
public record BasicRoomResponseDto(
        Long id,
        String title,
        String uniqueSlug,
        String area,
        String location,
        BigDecimal rentalFee,
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
        String photo
// The first photo can be used as the thumbnail.The frontend can use photos.get(0) from the response to display the thumbnail.
) {}