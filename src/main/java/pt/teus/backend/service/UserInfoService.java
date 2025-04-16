package pt.teus.backend.service;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import pt.teus.backend.dto.request.user.RegistrationRequestDto;
import pt.teus.backend.dto.request.user.UserUpdateRequestDto;
import pt.teus.backend.dto.response.user.UserResponseDto;

import java.math.BigDecimal;

public interface UserInfoService extends UserDetailsService {

    boolean userExistsByUsername(String username);

    UserResponseDto getUserByUsername(String email);

    UserResponseDto registerUser(RegistrationRequestDto requestDto);


void deleteUserByAdmin(Long userId, String adminEmail);

    String getRole(String email);

    UserResponseDto updateUserDetails(String email, UserUpdateRequestDto requestDto);
    void upgradeUser(String email);

    boolean isAdmin(String email);

    boolean isPremiumUser(String email);

    UserDetails loadUserByUsername(String userEmail);

    void requestUpgrade(@NotNull @Email String email, @NotNull BigDecimal bigDecimal);

    void approveUpgrade(String email);

    Page<UserResponseDto> getAllUsers(Pageable pageable);
}
