package pt.teus.backend.entity.room;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.CreationTimestamp;
import pt.teus.backend.entity.enums.BedSize;
import pt.teus.backend.entity.enums.PayPeriod;
import pt.teus.backend.entity.enums.RoomCategory;
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

    // The user who owns (rents out) this room
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserInfo owner;

    @Column(nullable = false)
    private String title; // e.g. "Private Room in T2 Apartment"

    @Column(nullable = false, unique = true, length = 50, updatable = false)
    private String slug; // for SEO-friendly URLs

    @Column(name = "area_sqm", nullable = false, precision = 6, scale = 2)
    private BigDecimal area; // m² (square meters)

    @Column(name = "prepayment_amount", precision = 10, scale = 2)
    private BigDecimal prepayment; // Optional prepayment

    @Column(name = "rental_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalFee; // Rent in EUR

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayPeriod payPeriod; // DAILY, WEEKLY, MONTHLY, etc.

    @Column(nullable = false)
    private Boolean smokingNotAllowed;

    @Column(nullable = false)
    private Boolean petsNotAllowed;

    @Column(nullable = false)
    private Boolean furnished;

    @Column
    private Boolean womenOnly;

    @Column
    private Boolean ownerLivesInProperty;

    @Column
    private Boolean cleaningServiceIncluded;

    @Column
    private Boolean wifiAvailable;

    @Column(length = 1000)
    private String description;

    @Column
    private String neighborhood; // e.g. Saldanha

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String postalCode;

    @Column
    private String mapImageUrl; // URL to a map image

    @Column(nullable = false)
    private LocalDate availableFrom;

    @Column
    private LocalDate availableTo;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "room_photos", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BedSize bedSize; // SINGLE, DOUBLE, QUEEN, KING

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomPrivacyType privacyType; // PRIVATE, SHARED

    @Column(nullable = false)
    private Integer maxOccupants; // Max people allowed

    @Column
    private Boolean airConditioner;

    @Column
    private Boolean heating;

    @Column
    private Boolean kitchenAvailable;

    @Column
    private Boolean privateBathroom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomCategory category;
    // APARTMENT, HOTEL, MOTEL, STUDIO, DORMITORY, VILLAGE, etc.

    @Column
    private String apartmentType;
    // Optional: T0, T1, T2... only relevant if category is APARTMENT or VILLAGE

    @Column
    private Integer starRating;
    // Optional: 2, 3, 4, 5 — relevant if category is HOTEL or MOTEL


    public Room() {
    }

    public Room(Long id, UserInfo owner, String title, String slug, BigDecimal area, BigDecimal prepayment, BigDecimal rentalFee, PayPeriod payPeriod, Boolean smokingNotAllowed, Boolean petsNotAllowed, Boolean furnished, Boolean womenOnly, Boolean ownerLivesInProperty, Boolean cleaningServiceIncluded, Boolean wifiAvailable, String description, String neighborhood, String city, String country, String postalCode, String mapImageUrl, LocalDate availableFrom, LocalDate availableTo, LocalDateTime createdAt, List<String> photoUrls, BedSize bedSize, RoomPrivacyType privacyType, Integer maxOccupants, Boolean airConditioner, Boolean heating, Boolean kitchenAvailable, Boolean privateBathroom, RoomCategory category, String apartmentType, Integer starRating) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.slug = slug;
        this.area = area;
        this.prepayment = prepayment;
        this.rentalFee = rentalFee;
        this.payPeriod = payPeriod;
        this.smokingNotAllowed = smokingNotAllowed;
        this.petsNotAllowed = petsNotAllowed;
        this.furnished = furnished;
        this.womenOnly = womenOnly;
        this.ownerLivesInProperty = ownerLivesInProperty;
        this.cleaningServiceIncluded = cleaningServiceIncluded;
        this.wifiAvailable = wifiAvailable;
        this.description = description;
        this.neighborhood = neighborhood;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.mapImageUrl = mapImageUrl;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.createdAt = createdAt;
        this.photoUrls = photoUrls;
        this.bedSize = bedSize;
        this.privacyType = privacyType;
        this.maxOccupants = maxOccupants;
        this.airConditioner = airConditioner;
        this.heating = heating;
        this.kitchenAvailable = kitchenAvailable;
        this.privateBathroom = privateBathroom;
        this.category = category;
        this.apartmentType = apartmentType;
        this.starRating = starRating;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public Boolean getSmokingNotAllowed() {
        return smokingNotAllowed;
    }

    public void setSmokingNotAllowed(Boolean smokingNotAllowed) {
        this.smokingNotAllowed = smokingNotAllowed;
    }

    public Boolean getPetsNotAllowed() {
        return petsNotAllowed;
    }

    public void setPetsNotAllowed(Boolean petsNotAllowed) {
        this.petsNotAllowed = petsNotAllowed;
    }

    public Boolean getFurnished() {
        return furnished;
    }

    public void setFurnished(Boolean furnished) {
        this.furnished = furnished;
    }

    public Boolean getWomenOnly() {
        return womenOnly;
    }

    public void setWomenOnly(Boolean womenOnly) {
        this.womenOnly = womenOnly;
    }

    public Boolean getOwnerLivesInProperty() {
        return ownerLivesInProperty;
    }

    public void setOwnerLivesInProperty(Boolean ownerLivesInProperty) {
        this.ownerLivesInProperty = ownerLivesInProperty;
    }

    public Boolean getCleaningServiceIncluded() {
        return cleaningServiceIncluded;
    }

    public void setCleaningServiceIncluded(Boolean cleaningServiceIncluded) {
        this.cleaningServiceIncluded = cleaningServiceIncluded;
    }

    public Boolean getWifiAvailable() {
        return wifiAvailable;
    }

    public void setWifiAvailable(Boolean wifiAvailable) {
        this.wifiAvailable = wifiAvailable;
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

    public String getMapImageUrl() {
        return mapImageUrl;
    }

    public void setMapImageUrl(String mapImageUrl) {
        this.mapImageUrl = mapImageUrl;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalDate getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(LocalDate availableTo) {
        this.availableTo = availableTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
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

    public Integer getMaxOccupants() {
        return maxOccupants;
    }

    public void setMaxOccupants(Integer maxOccupants) {
        this.maxOccupants = maxOccupants;
    }

    public Boolean getAirConditioner() {
        return airConditioner;
    }

    public void setAirConditioner(Boolean airConditioner) {
        this.airConditioner = airConditioner;
    }

    public Boolean getHeating() {
        return heating;
    }

    public void setHeating(Boolean heating) {
        this.heating = heating;
    }

    public Boolean getKitchenAvailable() {
        return kitchenAvailable;
    }

    public void setKitchenAvailable(Boolean kitchenAvailable) {
        this.kitchenAvailable = kitchenAvailable;
    }

    public Boolean getPrivateBathroom() {
        return privateBathroom;
    }

    public void setPrivateBathroom(Boolean privateBathroom) {
        this.privateBathroom = privateBathroom;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public void setCategory(RoomCategory category) {
        this.category = category;
    }

    public String getApartmentType() {
        return apartmentType;
    }

    public void setApartmentType(String apartmentType) {
        this.apartmentType = apartmentType;
    }

    public Integer getStarRating() {
        return starRating;
    }

    public void setStarRating(Integer starRating) {
        this.starRating = starRating;
    }
}
