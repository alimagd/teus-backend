package pt.teus.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pt.teus.backend.dto.request.user.RegistrationRequestDto;
import pt.teus.backend.dto.request.user.UserUpdateRequestDto;
import pt.teus.backend.dto.response.ErrorResponse;
import pt.teus.backend.dto.response.user.UserResponseDto;
import pt.teus.backend.exception.ResourceNotFoundException;
import pt.teus.backend.service.UserInfoService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserInfoService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegistrationRequestDto requestDto) {
        try {
            UserResponseDto createdUser = userService.registerUser(requestDto);
            System.out.println("SIIIK : createdUser.userRole() = " + createdUser.userRole());
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Endpoint for requesting an upgrade
    @PostMapping("/upgrade/request/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> requestUpgrade(@PathVariable String email, @RequestParam BigDecimal amountPaid) {
        try {
            userService.requestUpgrade(email, amountPaid);
            return ResponseEntity.ok("Upgrade request submitted successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Endpoint for admin to approve an upgrade
    @PostMapping("/upgrade/approve/{email}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> approveUpgrade(@PathVariable String email) {
        try {
            userService.approveUpgrade(email);
            return ResponseEntity.ok("User upgrade approved successfully.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/welcome")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> welcome(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponseDto userResponseDto = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok("Welcome, " + userResponseDto.fullName() + "!");
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated() and (hasAuthority('ADMIN') or #email.equals(authentication.principal.username))")
    public ResponseEntity<UserResponseDto> updateUserDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserUpdateRequestDto requestDto) {

        String email = userDetails.getUsername(); // Get the current authenticated user's email

        // Check if the request is for the logged-in user or if admin is updating another user
        if (email.equals(requestDto.email()) || userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            UserResponseDto updatedUser = userService.updateUserDetails(email, requestDto);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Admin or self-check failed
        }
    }

    @DeleteMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails adminDetails) {
        String adminEmail = adminDetails.getUsername();
        userService.deleteUserByAdmin(userId, adminEmail);
        return ResponseEntity.ok("User account deleted successfully.");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    )
        throws ResourceNotFoundException {
        
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

}
