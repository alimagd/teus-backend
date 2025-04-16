package pt.teus.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.teus.backend.dto.response.room.BasicRoomResponseDto;
import pt.teus.backend.entity.enums.PayPeriod;
import pt.teus.backend.entity.room.Room;
import pt.teus.backend.entity.user.UserInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Page<Room> findByOwner(UserInfo owner, Pageable pageable);

    List<Room> findByOwnerId(Long id);


    @Query("SELECT p FROM Room p WHERE p.owner.email = :ownerEmail")
    Page<Room> findByOwnerEmail(@Param("ownerEmail") String ownerEmail, Pageable pageable);


    @Query("""
                SELECT r FROM Room r
                WHERE (:address IS NULL OR LOWER(r.city) LIKE CONCAT('%', LOWER(:city), '%'))
                AND (:availableFrom IS NULL OR r.availableFrom <= :availableFrom)
            """)
    Page<Room> findAllByCityAndAvailableFrom(
            @Param("city") String city,
            @Param("availableFrom") LocalDate availableFrom,
            Pageable pageable
    );

@Query("SELECT r FROM Room r " +
       "WHERE (:country IS NULL OR LOWER(r.country) LIKE LOWER(CONCAT('%', :country, '%'))) " +
       "AND (:city IS NULL OR LOWER(r.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
       "AND (:neighborhood IS NULL OR LOWER(r.neighborhood) LIKE LOWER(CONCAT('%', :neighborhood, '%'))) " +
       "AND (:maxPrice IS NULL OR r.rentalFee <= :maxPrice) " +
       "AND (:payPeriod IS NULL OR r.payPeriod = :payPeriod)")
Page<BasicRoomResponseDto> searchRooms(
        @Param("country") String country,
        @Param("city") String city,
        @Param("neighborhood") String neighborhood,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("payPeriod") PayPeriod payPeriod,
        Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.availableFrom >= :availableFrom ")
    Page<Room> findByAvailableFrom(LocalDate availableFrom, Pageable pageable);

//    boolean existsByUniqueSlug(String uniqueSlug);

    @Query("SELECT MAX(r.uniqueSlug) FROM Room r")
    String findMaxUniqueSlug();

//    Page<Room> findByCountryContainingIgnoreCase(@Param("country") String country, Pageable pageable);
//
//    Page<Room> findByCityContainingIgnoreCase(String city, Pageable pageable);

}

