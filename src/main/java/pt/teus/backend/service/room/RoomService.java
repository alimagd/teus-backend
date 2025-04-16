package pt.teus.backend.service.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.teus.backend.dto.mappers.RoomMapper;
import pt.teus.backend.dto.request.room.LocationData;
import pt.teus.backend.dto.request.room.RoomCreationRequestDto;
import pt.teus.backend.dto.request.room.RoomUpdatingRequest;
import pt.teus.backend.dto.response.room.BasicRoomResponseDto;
import pt.teus.backend.dto.response.room.RoomResponseDto;
import pt.teus.backend.entity.enums.PayPeriod;
import pt.teus.backend.entity.room.Room;
import pt.teus.backend.entity.user.UserInfo;
import pt.teus.backend.entity.user.UserRole;
import pt.teus.backend.exception.ResourceNotFoundException;
import pt.teus.backend.repository.RoomRepository;
import pt.teus.backend.repository.UserInfoRepository;
import pt.teus.backend.security.UserInfoDetails;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@EnableCaching
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserInfoRepository userInfoRepository; // To fetch the owner.
    private final RoomMapper roomMapper;
    private final GeocodingService geocodingService;

    public RoomService(RoomRepository roomRepository, UserInfoRepository userInfoRepository,
                       RoomMapper roomMapper, GeocodingService geocodingService) {
        this.roomRepository = roomRepository;
        this.userInfoRepository = userInfoRepository;
        this.roomMapper = roomMapper;
        this.geocodingService = geocodingService;
    }


    private static final String UPLOAD_DIR = "src/main/resources/static/upload/"; // dir for photos

    private String generateUniqueSlug() {
        // Fetch the max slug from the database
        String maxSlug = roomRepository.findMaxUniqueSlug();
        int nextId = 11111111; // Start from '11111111' if no rooms exist

        if (maxSlug != null) {
            // Extract the numeric part of the slug
            String numericPart = maxSlug.replace("ROOM_", "");
            nextId = Integer.parseInt(numericPart) + 1;
        }

        // Ensure the slug does not exceed '99999999'
        if (nextId > 99999999) {
            throw new IllegalStateException("Maximum number of rooms reached: ROOM_99999999");
        }

        // Format the next slug as 'ROOM_XXXXXXXX'
        return String.format("ROOM_%08d", nextId);
    }
    private List<String> saveImages(List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // Ensure upload directory exists
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    boolean isMkDir = uploadDir.mkdirs();
                    if (!isMkDir) {
                        throw new RuntimeException("Unable to create directory " + UPLOAD_DIR);
                    }
                }

                // Generate a unique filename
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);

                // Save file
                Files.write(filePath, file.getBytes());

                // Store the image URL
                imageUrls.add("/static/upload/" + fileName); // Can be changed to S3 later
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }
        return imageUrls;
    }


    public BasicRoomResponseDto createRoom(RoomCreationRequestDto requestDto, String username, List<MultipartFile> photos) {

        // Fetch owner
        UserInfo owner = userInfoRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        // Map DTO to Entity
        Room room = new Room();
        LocationData location = requestDto.location();
        room.setCountry(location.getCountry());
        room.setCity(location.getCity());
        room.setNeighborhood(location.getNeighborhood());
        room.setPostalCode(location.getPostalCode());

        // Fetch map URL or set a default
        String mapUrl = geocodingService.getLocationMapImageUrl(
                location.getNeighborhood(), location.getCity(), location.getCountry()
        );
        room.setLocationMapImageUrl(mapUrl != null && !mapUrl.isBlank() ? mapUrl : "http://localhost:8080/src/main/resources/static/upload/map1.jpg");

        room.setOwner(owner);
        room.setTitle(requestDto.title());
        room.setUniqueSlug(generateUniqueSlug());
        room.setArea(requestDto.area());
        room.setPrepayment(requestDto.prepayment());
        room.setRentalFee(requestDto.rentalFee());
        room.setPayPeriod(requestDto.payPeriod());
        room.setSmokingForbidden(requestDto.isSmokingForbidden());
        room.setPetForbidden(requestDto.isPetForbidden());
        room.setFurnished(requestDto.isFurnished());
        room.setOnlyForWomen(requestDto.isOnlyForWomen());
        room.setOwnerLivesHere(requestDto.ownerLivesHere());
        room.setHasCleaningService(requestDto.hasCleaningService());
        room.setHasWifi(requestDto.hasWifi());
        room.setMaxOccupancy(requestDto.maxOccupancy());
        room.setBedSize(requestDto.bedSize());
        room.setPrivacyType(requestDto.roomPrivacyType());
        room.setHasAirConditioner(requestDto.hasAirConditioner());
        room.setDescription(requestDto.description());
        room.setAvailableFrom(requestDto.availableFrom());
        room.setCreatedAt(LocalDateTime.now());

        // upload photos
        if (photos.size() > 6) {
            throw new IllegalArgumentException("You can upload a maximum of 6 photos.");
        }
        List<String> imageUrls = saveImages(photos);
        room.setPhotos(imageUrls);

        // Save to DB
        Room savedRoom = roomRepository.save(room);

        // Map to Response DTO
        return roomMapper.toBasicDto(savedRoom);
    }

    public BasicRoomResponseDto updateRoom(Long id, RoomUpdatingRequest requestDto, String ownerEmail, List<String> photoUrls) {
        // Step 1: Fetch the room by ID
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));

        // Step 2: Validate that the landlord owns the room
        if (!existingRoom.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not allowed to update this room.");
        }

        // Step 3: Update common fields of the room
        if (requestDto.title() != null) existingRoom.setTitle(requestDto.title());
        if (requestDto.area() != null) existingRoom.setArea(requestDto.area());
        if (requestDto.prepayment() != null) existingRoom.setPrepayment(requestDto.prepayment());
        if (requestDto.rentalFee() != null) existingRoom.setRentalFee(requestDto.rentalFee());
        if (requestDto.payPeriod() != null) existingRoom.setPayPeriod(requestDto.payPeriod());
        if (requestDto.description() != null) existingRoom.setDescription(requestDto.description());
        if (requestDto.availableFrom() != null) existingRoom.setAvailableFrom(requestDto.availableFrom());
        if (requestDto.isSmokingForbidden() != null) existingRoom.setSmokingForbidden(requestDto.isSmokingForbidden());
        if (requestDto.isPetForbidden() != null) existingRoom.setPetForbidden(requestDto.isPetForbidden());
        if (requestDto.isFurnished() != null) existingRoom.setFurnished(requestDto.isFurnished());
        if (requestDto.isOnlyForWomen() != null) existingRoom.setOnlyForWomen(requestDto.isOnlyForWomen());
        if (requestDto.hasCleaningService() != null) existingRoom.setHasCleaningService(requestDto.hasCleaningService());
        if (requestDto.hasWifi() != null) existingRoom.setHasWifi(requestDto.hasWifi());
        if (requestDto.maxOccupancy() != null) existingRoom.setMaxOccupancy(requestDto.maxOccupancy());
        if (requestDto.bedSize() != null) existingRoom.setBedSize(requestDto.bedSize());
        if (requestDto.roomPrivacyType() != null) existingRoom.setPrivacyType(requestDto.roomPrivacyType());
        if (requestDto.hasAirConditioner() != null) existingRoom.setHasAirConditioner(requestDto.hasAirConditioner());

        // Step 4: Update room photos
        if (photoUrls != null && !photoUrls.isEmpty()) {
            if (photoUrls.size() > 6) {
                throw new IllegalArgumentException("Maximum 6 photos are allowed.");
            }
            existingRoom.setPhotos(photoUrls);
        }

        // Step 5: Update location using LocationData DTO
        boolean locationChanged = false;
        if (requestDto.location() != null) {
            if (requestDto.location().getNeighborhood() != null && !requestDto.location().getNeighborhood().equals(existingRoom.getNeighborhood())) {
                existingRoom.setNeighborhood(requestDto.location().getNeighborhood());
                locationChanged = true;
            }
            if (requestDto.location().getCity() != null && !requestDto.location().getCity().equals(existingRoom.getCity())) {
                existingRoom.setCity(requestDto.location().getCity());
                locationChanged = true;
            }
            if (requestDto.location().getCountry() != null && !requestDto.location().getCountry().equals(existingRoom.getCountry())) {
                existingRoom.setCountry(requestDto.location().getCountry());
                locationChanged = true;
            }
            if (requestDto.location().getPostalCode() != null && !requestDto.location().getPostalCode().equals(existingRoom.getPostalCode())) {
                existingRoom.setPostalCode(requestDto.location().getPostalCode());
                locationChanged = true;
            }
        }

        // Step 6: Regenerate map URL if the location has changed
        if (locationChanged) {
            String newMapUrl = geocodingService.getLocationMapImageUrl(
                    existingRoom.getNeighborhood(), existingRoom.getCity(), existingRoom.getCountry()
            );
            existingRoom.setLocationMapImageUrl(newMapUrl);
        }

        // Step 7: Save the updated room
        Room updatedRoom = roomRepository.save(existingRoom);

        // Step 8: Return the updated room as a response DTO
        return roomMapper.toBasicDto(updatedRoom);
    }

    @Cacheable(value = "roomDetails", key = "#roomId")
    public RoomResponseDto getRoomDetails(Long roomId, UserDetails userDetails) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        boolean includeContact = false;
        if (userDetails instanceof UserInfoDetails userInfoDetails) {
            UserRole role = userInfoDetails.getRole();
            includeContact = (role == UserRole.USER || role == UserRole.ADMIN);
        }

        return roomMapper.toResponseDto(room, includeContact);
    }



    //     Publicly accessible room list
    public Page<BasicRoomResponseDto> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable).map(room -> new BasicRoomResponseDto(
                room.getId(),
                room.getTitle(),
                room.getUniqueSlug(),
                room.getArea().toString(),
                room.getCountry() + "," + room.getCity() + "," + room.getNeighborhood(),
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
                room.getPhotos().getFirst()
        ));
    }

    Logger logger = LoggerFactory.getLogger(RoomService.class);

    public Page<BasicRoomResponseDto> getAllRoomsByCityAndDate(
            String city,
            LocalDate availableFrom,
            Pageable pageable
    ) {
        try {
            if (availableFrom == null) {
                availableFrom = LocalDate.now();
            }

            // Log the parameters
            logger.info("Calling repository with filters - city: {}, availableFrom: {}",
                     city, availableFrom);

            Page<Room> rooms = roomRepository.findAllByCityAndAvailableFrom( city, availableFrom, pageable);

            // Log the results count
            logger.info("Repository returned {} rooms", rooms.getTotalElements());

            return rooms.map(roomMapper::toBasicDto);

        } catch (Exception ex) {
            logger.error("Error occurred while filtering rooms: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error fetching rooms. Please try again later.", ex);
        }
    }

    // getting
    public Page<BasicRoomResponseDto> getAllAvailableRooms(Pageable pageable, LocalDate availableFrom) {
        if (availableFrom == null) {
            availableFrom = LocalDate.now();
        }
        return roomRepository.findByAvailableFrom(availableFrom, pageable)
                .map(room -> new BasicRoomResponseDto(
                        room.getId(),
                        room.getTitle(),
                        room.getUniqueSlug(),
                        room.getArea().toString(),
                        room.getCountry() + "," + room.getCity() + "," + room.getNeighborhood(),
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
                        room.getPhotos().getFirst()
                ));
    }


    public Page<BasicRoomResponseDto> getRoomsByOwner(String ownerEmail, Pageable pageable) {
        UserInfo owner = userInfoRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        return roomRepository.findByOwner(owner, pageable)
                .map(room -> new BasicRoomResponseDto(
                        room.getId(),
                        room.getTitle(),
                        room.getUniqueSlug(),
                        room.getArea().toString(),
                        room.getCountry() + "," + room.getCity() + "," + room.getNeighborhood(),
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
                        room.getPhotos().getFirst()
                ));
    }

    @Cacheable(value = {"roomsByOwner","roomDetails"},key = "#roomId")
    public void deleteRoom(Long roomId, String requesterEmail) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        UserInfo requester = userInfoRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Admin can delete any room, owner can delete only their own room
        if (!requester.getUserRole().equals(UserRole.ADMIN) && !room.getOwner().getEmail().equals(requesterEmail)) {
            throw new AccessDeniedException("You are not allowed to delete this room");
        }

        roomRepository.delete(room);
    }


    public void deleteMyRooms(String ownerEmail) {
        // Find the landlord
        UserInfo owner = userInfoRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Landlord not found"));

        // Delete all rooms owned by the landlord
        List<Room> rooms = roomRepository.findByOwnerId(owner.getId());
        roomRepository.deleteAll(rooms);
    }

    public void deleteAllRoomsOfUser(Long userId, String adminEmail) {
        UserInfo admin = userInfoRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!admin.getUserRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("Only admins can delete all rooms of a user");
        }

        List<Room> rooms = roomRepository.findByOwnerId(userId);
        roomRepository.deleteAll(rooms);
    }


    public BasicRoomResponseDto addPhoto(Long roomId, String newPhoto, String ownerEmail) {
        // 1. Retrieve the Room by roomId
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 2. Verify that the ownerEmail corresponds to the room's owner
        UserInfo owner = userInfoRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!room.getOwner().equals(owner)) {
            throw new RuntimeException("User does not own the room");
        }
        List<String> photos = room.getPhotos();
        photos.add(newPhoto);
        room.setPhotos(photos);
        roomRepository.save(room); // Save the room if necessary (due to cascading)

        // 6. Return the updated room data (map it to BasicRoomResponseDto)
        return roomMapper.toBasicDto(room);
    }

    public BasicRoomResponseDto removePhoto(Long roomId, String photo, String ownerEmail) {
        // Fetch the room
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        // Ensure the landlord owns this room
        if (!room.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not allowed to replace photos for this room");
        }

        // Replace the photos
        room.getPhotos().remove(photo);
        // Save the updated room
        Room updatedRoom = roomRepository.save(room);
        return new BasicRoomResponseDto(
                updatedRoom.getId(),
                updatedRoom.getTitle(),
                updatedRoom.getUniqueSlug(),
                updatedRoom.getArea().toString(),
                room.getCountry() + "," + room.getCity() + "," + room.getNeighborhood(),
                updatedRoom.getRentalFee(),
                updatedRoom.getAvailableFrom(),
                updatedRoom.getSmokingForbidden(),
                updatedRoom.getPetForbidden(),
                updatedRoom.getFurnished(),
                updatedRoom.getOnlyForWomen(),
                updatedRoom.getOwnerLivesHere(),
                updatedRoom.getHasCleaningService(),
                updatedRoom.getHasWifi(),
                updatedRoom.getMaxOccupancy(),
                updatedRoom.getBedSize(),
                updatedRoom.getPrivacyType(),
                updatedRoom.getHasAirConditioner(),
                updatedRoom.getPhotos().getFirst()
        );
    }
    public void deletePhotos(List<String> photoUrls) {
        if (photoUrls != null) {
            for (String url : photoUrls) {
                Path path = Paths.get(UPLOAD_DIR + url.substring(url.lastIndexOf("/") + 1));
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete file: " + path, e);
                }
            }
        }
    }



    public List<String> getPhotos(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        return room.getPhotos();
    }

    // Address search
    public Page<BasicRoomResponseDto> findRooms(
            String country,
            String city,
            String neighborhood,
            BigDecimal maxRentalFee,
            PayPeriod payPeriod,
            Pageable pageable) {
        return roomRepository.searchRooms(
                country, city, neighborhood, maxRentalFee, payPeriod, pageable).map(
                room -> new BasicRoomResponseDto(
                        room.id(),
                        room.title(),
                        room.uniqueSlug(),
                        room.area(),
                        room.location(),
                        room.rentalFee(),
                        room.availableFrom(),
                        room.isSmokingForbidden(),
                        room.isPetForbidden(),
                        room.isFurnished(),
                        room.isOnlyForWomen(),
                        room.ownerLivesHere(),
                        room.hasCleaningService(),
                        room.hasWifi(),
                        room.maxOccupancy(),
                        room.bedSize(),
                        room.roomPrivacyType(),
                        room.hasAirConditioner(),
                        room.photo()
                ));
    }

}


