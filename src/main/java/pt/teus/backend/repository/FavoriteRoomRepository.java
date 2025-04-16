package pt.teus.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.teus.backend.entity.room.FavoriteRoom;
import pt.teus.backend.entity.room.Room;
import pt.teus.backend.entity.user.UserInfo;

import java.util.List;
import java.util.Optional;

public interface FavoriteRoomRepository extends JpaRepository<FavoriteRoom, Long> {
    Optional<FavoriteRoom> findByUserAndRoom(UserInfo user, Room room);
    List<FavoriteRoom> findByUser(UserInfo user);
    void deleteByUserAndRoom(UserInfo user, Room room);
}
