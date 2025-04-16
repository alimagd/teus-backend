package pt.teus.backend.entity.room;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.CreationTimestamp;
import pt.teus.backend.entity.enums.BedSize;
import pt.teus.backend.entity.enums.PayPeriod;
import pt.teus.backend.entity.enums.RoomPrivacyType;
import pt.teus.backend.entity.user.UserInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserInfo owner;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true, length = 50, updatable = false)
    private String uniqueSlug; // Should not be updated after creation

    @Column(nullable = false, precision = 6, scale = 2)
    @Min(5)
    @Max(1000)
    private BigDecimal area;

    @Column(precision = 10, scale = 2)
    private BigDecimal prepayment;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal rentalFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayPeriod payPeriod;

    @Column(nullable = false)
    private Boolean isSmokingForbidden;

    @Column(nullable = false)
    private Boolean isPetForbidden;

    @Column(nullable = false)
    private Boolean isFurnished;

    @Column
    private Boolean isOnlyForWomen;

    @Column
    private Boolean ownerLivesHere;

    @Column
    private Boolean hasCleaningService;

    @Column
    private Boolean hasWifi;

    @Column(length = 1000)
    private String description;

    @Column
    private String neighborhood;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String postalCode;

    @Column
    private String locationMapImageUrl;

    @Column(nullable = false)
    private LocalDate availableFrom;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ElementCollection
    private List<String> photos = new ArrayList<>();  // Store image URLs directly

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BedSize bedSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomPrivacyType privacyType;  // PRIVATE یا SHARED

    @Column(nullable = false)
    private Integer maxOccupancy;         // چند نفر ظرفیت دارد

    @Column
    private Boolean hasAirConditioner;



    public Room() {
    }

    public Room(Long id, UserInfo owner, String title, String uniqueSlug, BigDecimal area,
                BigDecimal prepayment, BigDecimal rentalFee, PayPeriod payPeriod, Boolean isSmokingForbidden,
                Boolean isPetForbidden, Boolean isFurnished, Boolean isOnlyForWomen, Boolean ownerLivesHere,
                Boolean hasCleaningService, Boolean hasWifi, Boolean hasAirConditioner, String description,
                String neighborhood, String city, String country, String postalCode, String locationMapImageUrl, LocalDate availableFrom, LocalDateTime createdAt, List<String> photos, BedSize bedSize, RoomPrivacyType privacyType, Integer maxOccupancy) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.uniqueSlug = uniqueSlug;
        this.area = area;
        this.prepayment = prepayment;
        this.rentalFee = rentalFee;
        this.payPeriod = payPeriod;
        this.isSmokingForbidden = isSmokingForbidden;
        this.isPetForbidden = isPetForbidden;
        this.isFurnished = isFurnished;
        this.isOnlyForWomen = isOnlyForWomen;
        this.ownerLivesHere = ownerLivesHere;
        this.hasCleaningService = hasCleaningService;
        this.hasWifi = hasWifi;
        this.description = description;
        this.neighborhood = neighborhood;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.locationMapImageUrl = locationMapImageUrl;
        this.availableFrom = availableFrom;
        this.createdAt = createdAt;
        this.photos = photos;
        this.hasAirConditioner = hasAirConditioner;
        this.bedSize = bedSize;
        this.privacyType = privacyType;
        this.maxOccupancy = maxOccupancy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getOwner() {
        return owner;
    }

    public void setOwner(UserInfo owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUniqueSlug() {
        return uniqueSlug;
    }

    public void setUniqueSlug(String uniqueSlug) {
        this.uniqueSlug = uniqueSlug;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getPrepayment() {
        return prepayment;
    }

    public void setPrepayment(BigDecimal prepayment) {
        this.prepayment = prepayment;
    }

    public BigDecimal getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(BigDecimal rentalFee) {
        this.rentalFee = rentalFee;
    }

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(PayPeriod payPeriod) {
        this.payPeriod = payPeriod;
    }

    public Boolean getSmokingForbidden() {
        return isSmokingForbidden;
    }

    public void setSmokingForbidden(Boolean smokingForbidden) {
        isSmokingForbidden = smokingForbidden;
    }

    public Boolean getPetForbidden() {
        return isPetForbidden;
    }

    public void setPetForbidden(Boolean petForbidden) {
        isPetForbidden = petForbidden;
    }

    public Boolean getFurnished() {
        return isFurnished;
    }

    public void setFurnished(Boolean furnished) {
        isFurnished = furnished;
    }

    public Boolean getOnlyForWomen() {
        return isOnlyForWomen;
    }

    public void setOnlyForWomen(Boolean onlyForWomen) {
        isOnlyForWomen = onlyForWomen;
    }

    public Boolean getOwnerLivesHere() {
        return ownerLivesHere;
    }

    public void setOwnerLivesHere(Boolean ownerLivesHere) {
        this.ownerLivesHere = ownerLivesHere;
    }

    public Boolean getHasCleaningService() {
        return hasCleaningService;
    }

    public void setHasCleaningService(Boolean hasCleaningService) {
        this.hasCleaningService = hasCleaningService;
    }

    public Boolean getHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(Boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getLocationMapImageUrl() {
        return locationMapImageUrl;
    }

    public void setLocationMapImageUrl(String locationMapImageUrl) {
        this.locationMapImageUrl = locationMapImageUrl;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public BedSize getBedSize() {
        return bedSize;
    }

    public void setBedSize(BedSize bedSize) {
        this.bedSize = bedSize;
    }

    public RoomPrivacyType getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(RoomPrivacyType privacyType) {
        this.privacyType = privacyType;
    }

    public Integer getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(Integer maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public Boolean getHasAirConditioner() {
        return hasAirConditioner;
    }

    public void setHasAirConditioner(Boolean hasAirConditioner) {
        this.hasAirConditioner = hasAirConditioner;
    }
}
