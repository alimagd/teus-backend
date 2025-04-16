package pt.teus.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pt.teus.backend.entity.user.UserInfo;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}

