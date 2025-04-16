package pt.teus.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.teus.backend.entity.user.UpgradeRequest;
import pt.teus.backend.entity.user.UpgradeStatus;

import java.util.Optional;

public interface UpgradeRequestRepository extends JpaRepository<UpgradeRequest, Long> {

    boolean existsByUserEmail(String email);
    Optional<UpgradeRequest> findByUserEmailAndStatus(String email, UpgradeStatus status);

}
