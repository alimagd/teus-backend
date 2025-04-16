package pt.teus.backend.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.teus.backend.dto.mappers.UserMapper;
import pt.teus.backend.dto.request.user.RegistrationRequestDto;
import pt.teus.backend.dto.request.user.UserUpdateRequestDto;
import pt.teus.backend.dto.response.user.UserResponseDto;
import pt.teus.backend.entity.user.UpgradeRequest;
import pt.teus.backend.entity.user.UpgradeStatus;
import pt.teus.backend.entity.user.UserInfo;
import pt.teus.backend.entity.user.UserRole;
import pt.teus.backend.exception.ResourceNotFoundException;
import pt.teus.backend.repository.UpgradeRequestRepository;
import pt.teus.backend.repository.UserInfoRepository;
import pt.teus.backend.security.UserInfoDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepository userRepository;
    @Autowired
    @Lazy
    private PasswordEncoder encoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UpgradeRequestRepository upgradeRequestRepository;
    private static final BigDecimal MIN_UPGRADE_FEE = new BigDecimal("10.00");

    Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        logger.info("User found: {}", userInfo);
        logger.info("Stored hashed password: {}", userInfo.getPassword());
        return new UserInfoDetails(userInfo);
    }

    @Override
    public void requestUpgrade(@NotNull @Email String email, @NotNull BigDecimal amountPaid) {
        // Fetch user by email
        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Ensure the user has not already requested an upgrade
        if (upgradeRequestRepository.existsByUserEmail(email)) {
            throw new IllegalStateException("An upgrade request is already pending for this user.");
        }

        // Ensure the amount paid meets the minimum upgrade fee
        if (amountPaid.compareTo(MIN_UPGRADE_FEE) < 0) {
            throw new IllegalArgumentException("Minimum upgrade fee is $10.00.");
        }

        // Create and save the upgrade request in the database
        UpgradeRequest upgradeRequest = new UpgradeRequest(user, amountPaid, UpgradeStatus.PENDING);
        upgradeRequestRepository.save(upgradeRequest);

        // Log the action (you could also store this in a log file)
        logger.info("Upgrade request submitted for user: {}", email);
    }

    @Override
    public void approveUpgrade(String email) {
        // Find the pending upgrade request for the user
        UpgradeRequest upgradeRequest = upgradeRequestRepository.findByUserEmailAndStatus(email, UpgradeStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("No pending upgrade request found for this user."));

        // Fetch the user associated with the upgrade request
        UserInfo user = upgradeRequest.getUser();

        // Update the user's role to OWNER (or any other role)
        user.setUserRole(UserRole.USER);  // Update the role based on your requirements
        userRepository.save(user);

        // Mark the upgrade request as approved
        upgradeRequest.setStatus(UpgradeStatus.APPROVED);
        upgradeRequestRepository.save(upgradeRequest);

        // Log the action (you could also store this in a log file)
        logger.info("User {} has been upgraded to OWNER.", email);
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getUserRole().toString(),
                user.getCreatedAt(),
                user.isPremium()
        ));

    }

    @Override
    public boolean userExistsByUsername(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserResponseDto getUserByUsername(String email) {
        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + email));
        return userMapper.toDto(user);
    }

    private void validateUserRequest(RegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (!Boolean.TRUE.equals(requestDto.acceptedTerms())) { // Ensure it's not null and is true
            throw new IllegalArgumentException("You must accept the terms and conditions to register.");
        }
    }
    @Override
    public UserResponseDto registerUser(RegistrationRequestDto requestDto) {
        validateUserRequest(requestDto);

        UserInfo user = userMapper.toEntity(requestDto, encoder);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        return userMapper.toDto(user);
    }

@Override
    public void deleteUserByAdmin(Long userId, String adminEmail) {
        UserInfo admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!admin.getUserRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("Only admins can delete user accounts");
        }

        UserInfo userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(userToDelete);
    }

    @Override
    public String getRole(String email) {
        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with username: " + email));
        return user.getUserRole().toString();
    }

    @Override
    public UserResponseDto updateUserDetails(String email, UserUpdateRequestDto requestDto) {

        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + email));

        user.setFullName(requestDto.fullName());
        user.setPhone(requestDto.phone());


        UserInfo updated = userRepository.save(user);
        logger.info("User with email {} updated their details: FullName={}, Phone={}",
                email, requestDto.fullName(), requestDto.phone());

        return userMapper.toDto(updated);
    }

    @Override
    public void upgradeUser(String email) {
        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + email));

        user.setPremium(true);
        userRepository.save(user);
    }

    @Override
    public boolean isAdmin(String email) {
        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + email));
        return user.getUserRole() == UserRole.ADMIN;
    }

    @Override
    public boolean isPremiumUser(String email) {
        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + email));
        return user.isPremium();
    }

}
