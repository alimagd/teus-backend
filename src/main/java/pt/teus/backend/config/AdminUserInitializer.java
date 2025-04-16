package pt.teus.backend.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.teus.backend.dto.request.user.RegistrationRequestDto;
import pt.teus.backend.dto.response.user.UserResponseDto;
import pt.teus.backend.entity.user.UserRole;
import pt.teus.backend.service.UserInfoService;

@Configuration
public class AdminUserInitializer {


    @Autowired
    private UserInfoService userInfoService;

    @Bean
    public CommandLineRunner createAdminUser() {
        return args_ -> {
            if (!userInfoService.userExistsByUsername("super@teus.pt")) {
                RegistrationRequestDto adminUserRequest = new RegistrationRequestDto(
                        "super@teus.pt",
                        "915210728",
                        "super",
                        "Super Admin",
                        UserRole.ADMIN,
                        Boolean.TRUE
                );
                // Set profile in the UserInfoService or directly
                UserResponseDto userResponseDto = userInfoService.registerUser(adminUserRequest);
                userInfoService.upgradeUser(userResponseDto.email());

                System.out.println("Admin user created successfully.");
            } else {
                System.out.println("Admin user already exists.");
            }
        };
    }
}

