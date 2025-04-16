package pt.teus.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.teus.backend.dto.request.user.LoginRequestDto;
import pt.teus.backend.dto.request.user.RefreshTokenRequestDto;
import pt.teus.backend.security.JwtService;
import pt.teus.backend.security.TokenBlacklistService;
import pt.teus.backend.security.UserInfoDetails;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateToken(userDetails, ((UserInfoDetails) userDetails).getRole());
            String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());

            Map<String, String> tokens = Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            );

            return ResponseEntity.ok(tokens);
        } catch (AuthenticationException e) {
            logger.warn("Invalid login attempt for username: {}", loginRequest.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
    }


    // Refresh token endpoint
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }

        String username = jwtService.extractUsername(refreshToken);
        String newAccessToken = jwtService.generateToken(username, null); // Pass role if required

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid logout request.");
        }
        String token = authHeader.substring(7);
        try {
            // Extract token expiration and blacklist the token
            Date expiration = jwtService.extractExpiration(token);
            tokenBlacklistService.addToBlacklist(
                    token, expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            return ResponseEntity.ok("Successfully logged out.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to log out: " + e.getMessage());
        }
    }
}

