package pt.teus.backend.dto.request.room;


public class LocationData {
    private String country;
    private String city;
    private String neighborhood;
    private String postalCode;

    public LocationData(String country, String city, String neighborhood, String postalCode) {
        this.country = country;
        this.city = city;
        this.neighborhood = neighborhood;
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
