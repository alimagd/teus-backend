package pt.teus.backend.dto.mappers;

import org.springframework.stereotype.Component;
import pt.teus.backend.dto.request.room.LocationData;
import pt.teus.backend.dto.response.room.BasicRoomResponseDto;
import pt.teus.backend.dto.response.room.RoomResponseDto;
import pt.teus.backend.entity.room.Room;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoomMapper {


    public BasicRoomResponseDto toBasicDto(Room room) {
        if (room == null) {
            return null; // Handle null cases if needed
        }

        // Assuming Room has a getPhotos() method that returns a list of photo URLs
        List<String> photos = room.getPhotos() != null ? room.getPhotos() : List.of();
        String location = room.getCountry() + "," + room.getCity() + "," + room.getNeighborhood();

        return new BasicRoomResponseDto(
                room.getId(),
                room.getTitle(),
                room.getUniqueSlug(),
                room.getArea() != null ? room.getArea().stripTrailingZeros().toPlainString() : "0",
                location,
                room.getRentalFee(),
                room.getAvailableFrom(),
                room.getSmokingForbidden(),
                room.getPetForbidden(),
                room.getFurnished(),
                room.getOnlyForWomen(),
                room.getOwnerLivesHere(),
                room.getHasCleaningService(),
                room.getHasWifi(),
                room.getMaxOccupancy(),
                room.getBedSize(),
                room.getPrivacyType(),
                room.getHasAirConditioner(),
                photos.getFirst()
        );
    }

    public RoomResponseDto toResponseDto(Room room, boolean includeContact) {
        LocationData location = new LocationData(
                room.getCountry(), room.getCity(), room.getNeighborhood(), room.getPostalCode()
        );
        return new RoomResponseDto(
                room.getId(),
                room.getTitle(),
                room.getUniqueSlug(),
                room.getArea() != null ? room.getArea().stripTrailingZeros().toPlainString() : "0",
                room.getPrepayment(),
                room.getRentalFee(),
                room.getPayPeriod(),
                room.getDescription(),
                location,
                room.getAvailableFrom(),
                room.getSmokingForbidden(),
                room.getPetForbidden(),
                room.getFurnished(),
                room.getOnlyForWomen(),
                room.getOwnerLivesHere(),
                room.getHasCleaningService(),
                room.getHasWifi(),
                room.getMaxOccupancy(),
                room.getBedSize(),
                room.getPrivacyType(),
                room.getHasAirConditioner(),
                room.getPhotos() != null ? room.getPhotos() : new ArrayList<>(),
                room.getLocationMapImageUrl(),
                includeContact ? room.getOwner().getPhone() : "Contact!!" // Conditionally include contact
        );
    }
}
