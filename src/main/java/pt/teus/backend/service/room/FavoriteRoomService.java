package pt.teus.backend.service.room;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.teus.backend.entity.room.FavoriteRoom;
import pt.teus.backend.entity.room.Room;
import pt.teus.backend.entity.user.UserInfo;
import pt.teus.backend.exception.ResourceNotFoundException;
import pt.teus.backend.repository.FavoriteRoomRepository;
import pt.teus.backend.repository.RoomRepository;
import pt.teus.backend.repository.UserInfoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteRoomService {

    private final FavoriteRoomRepository favoriteRoomRepository;
    private final RoomRepository roomRepository;
    private final UserInfoRepository userInfoRepository;

    public FavoriteRoomService(FavoriteRoomRepository favoriteRoomRepository, RoomRepository roomRepository, UserInfoRepository userInfoRepository) {
        this.favoriteRoomRepository = favoriteRoomRepository;
        this.roomRepository = roomRepository;
        this.userInfoRepository = userInfoRepository;
    }

    @Transactional
    public void addRoomToFavorites(Long roomId, String userEmail) {
        UserInfo user = userInfoRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        favoriteRoomRepository.findByUserAndRoom(user, room)
                .ifPresent(fav -> {
                    throw new IllegalArgumentException("Room is already in favorites");
                });

        FavoriteRoom favorite = new FavoriteRoom();
        favorite.setUser(user);
        favorite.setRoom(room);
        favoriteRoomRepository.save(favorite);
    }

    @Transactional
    public void removeRoomFromFavorites(Long roomId, String userEmail) {
        UserInfo user = userInfoRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        favoriteRoomRepository.deleteByUserAndRoom(user, room);
    }

    public List<Long> getUserFavoriteRoomIds(String userEmail) {
        UserInfo user = userInfoRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return favoriteRoomRepository.findByUser(user).stream()
                .map(favorite -> favorite.getRoom().getId())
                .collect(Collectors.toList());
    }
}
