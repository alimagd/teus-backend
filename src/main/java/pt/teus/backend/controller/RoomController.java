package pt.teus.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.teus.backend.dto.request.room.RoomCreationRequestDto;
import pt.teus.backend.dto.request.room.RoomUpdatingRequest;
import pt.teus.backend.dto.response.room.BasicRoomResponseDto;
import pt.teus.backend.dto.response.room.RoomResponseDto;
import pt.teus.backend.entity.enums.PayPeriod;
import pt.teus.backend.service.room.FavoriteRoomService;
import pt.teus.backend.service.room.RoomService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private FavoriteRoomService favoriteRoomService;

    Logger logger = LoggerFactory.getLogger(RoomController.class);


    @GetMapping
    public ResponseEntity<Page<BasicRoomResponseDto>> getAllRooms(Pageable pageable) {
        return ResponseEntity.ok(roomService.getAllRooms(pageable));
    }
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDto> roomDetails(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {

        RoomResponseDto roomResponse = roomService.getRoomDetails(roomId, userDetails);
        return ResponseEntity.ok(roomResponse);
    }


    //
//    / /    @Operation(summary = "Create a new room", description = "Allows landlords to create a room listing.")
//    / /    @ApiResponses(value = {
//    / /            @ApiResponse(responseCode = "201", description = "Room created successfully"),
//    / /            @ApiResponse(responseCode = "403", description = "Access denied"),
//    / /            @ApiResponse(responseCode = "400", description = "Validation error")
//    / /    }) // swagger

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BasicRoomResponseDto> createRoom(
            @RequestPart("room") @Valid RoomCreationRequestDto requestDto,
            @RequestPart("photos") List<MultipartFile> photos,
            @AuthenticationPrincipal UserDetails userDetails){

        String userEmail = userDetails.getUsername(); // Assuming username is the email

        logger.info("requestDto : {}", requestDto.toString());

        BasicRoomResponseDto createdRoom = roomService.createRoom(requestDto, userEmail, photos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }



    @GetMapping("/available")
    public ResponseEntity<Page<BasicRoomResponseDto>> getAllAvailableRoomsFrom(
            Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate availableFrom) {
        return ResponseEntity.ok(roomService.getAllAvailableRooms(pageable, availableFrom));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BasicRoomResponseDto>> searchRooms(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String neighborhood,
            @RequestParam(required = false) BigDecimal maxRentalFee,
            @RequestParam(required = false) PayPeriod payPeriod,
            Pageable pageable
    ) {
        return ResponseEntity.ok(roomService.findRooms(
                country, city, neighborhood, maxRentalFee, payPeriod, pageable
        ));
    }

    //    GET http://localhost:9090/api/v1/rooms/filtered?availableFrom=2025-01-15&minPrice=500&maxPrice=2000&location=Lisbon&title=APARTMENT
    //    GET http://localhost:9090/api/v1/rooms/filtered?availableFrom=2025-01-15&city=Lisbon
    @GetMapping("/filteredByCity") // for searching by users
    public ResponseEntity<Page<BasicRoomResponseDto>> getFilteredRooms(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate availableFrom,
            Pageable pageable
    ) {
        // logger to test
        logger.info("AvailableFrom: {},  Location: {}",
                availableFrom, city);

        Page<BasicRoomResponseDto> rooms = roomService.getAllRoomsByCityAndDate(
                city, availableFrom, pageable);

        logger.info("Rooms fetched: {}", rooms.getContent());

        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/myRooms")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BasicRoomResponseDto>> getMyRooms(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    ) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(roomService.getRoomsByOwner(userEmail, pageable));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteRoom(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        roomService.deleteRoom(id, email);  // email can be the room-owner-email or the admin-email
        return ResponseEntity.ok("Room deleted successfully.");
    }

    @DeleteMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteMyRooms(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        roomService.deleteMyRooms(userEmail);
        return ResponseEntity.ok("All your rooms deleted successfully.");
    }

    @DeleteMapping("/admin/user/{userId}/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllRoomsOfUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails adminDetails) {
        String adminEmail = adminDetails.getUsername();
        roomService.deleteAllRoomsOfUser(userId, adminEmail);
        return ResponseEntity.ok("All rooms for the user deleted successfully.");
    }


    @PatchMapping("/user/{id}/photos/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BasicRoomResponseDto> addPhoto(
            @PathVariable Long id,
            @RequestParam String newPhoto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        BasicRoomResponseDto updatedRoom = roomService.addPhoto(id, newPhoto, userEmail);
        return ResponseEntity.ok(updatedRoom);
    }

    @GetMapping("/{id}/photos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getPhotosOfRoom(
            @PathVariable Long id) {
        List<String> photos = roomService.getPhotos(id);
        return ResponseEntity.ok(photos);
    }


    @PatchMapping("/user/{id}/photos/remove")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BasicRoomResponseDto> removePhoto(
            @PathVariable Long id,
            @RequestParam String photoToRemove,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        BasicRoomResponseDto updatedRoom = roomService.removePhoto(id, photoToRemove, userEmail);
        return ResponseEntity.ok(updatedRoom);
    }


    @PutMapping("/user/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BasicRoomResponseDto> updateRoom(
            @PathVariable Long id,
            @RequestPart("room") RoomUpdatingRequest requestDto,
            @RequestPart(value = "photos", required = false) List<String> photos,
            @AuthenticationPrincipal UserDetails userDetails) {

        String ownerEmail = userDetails.getUsername();
        BasicRoomResponseDto updatedRoom = roomService.updateRoom(id, requestDto, ownerEmail, photos);
        return ResponseEntity.ok(updatedRoom);
    }


    @PostMapping("/{roomId}/favorite")
    public ResponseEntity<String> addRoomToFavorites(@PathVariable Long roomId, @AuthenticationPrincipal UserDetails userDetails) {
        favoriteRoomService.addRoomToFavorites(roomId, userDetails.getUsername());
        return ResponseEntity.ok("Room added to favorites");
    }

    @DeleteMapping("/{roomId}/favorite")
    public ResponseEntity<String> removeRoomFromFavorites(@PathVariable Long roomId, @AuthenticationPrincipal UserDetails userDetails) {
        favoriteRoomService.removeRoomFromFavorites(roomId, userDetails.getUsername());
        return ResponseEntity.ok("Room removed from favorites");
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Long>> getUserFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteRoomService.getUserFavoriteRoomIds(userDetails.getUsername()));
    }



}