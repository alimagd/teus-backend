package pt.teus.backend.dto.mappers;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pt.teus.backend.dto.request.user.RegistrationRequestDto;
import pt.teus.backend.dto.response.user.UserResponseDto;
import pt.teus.backend.entity.user.UserInfo;

@Component
public class UserMapper {

    public UserResponseDto toDto(UserInfo user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getUserRole().toString(),
                user.getCreatedAt(),
                user.isPremium()
        );
    }

    public UserInfo toEntity(RegistrationRequestDto requestDto, PasswordEncoder passwordEncoder) {
        UserInfo user = new UserInfo();
        user.setEmail(requestDto.email());
        user.setPhone(requestDto.phone());
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setFullName(requestDto.fullName());
        user.setUserRole(requestDto.userRole());
        return user;
    }
}

