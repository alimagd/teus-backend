package pt.teus.backend.service.room;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pt.teus.backend.dto.request.room.LocationData;

import java.util.List;
import java.util.Map;

@Service
public class GeocodingService {
Logger logger = LoggerFactory.getLogger(GeocodingService.class);
    private static final String LOCATIONIQ_GEOCODING_URL = "https://us1.locationiq.com/v1/search.php";
    private static final String LOCATIONIQ_STATIC_MAP_URL = "https://maps.locationiq.com/v3/staticmap";
    private static final String LOCATIONIQ_API_KEY = "pk.9a1353f9c96bb01fc543c553f536f1de";  // Replace with your API key

    @RateLimiter(name = "geocodingServiceRateLimiter")
    public String getLocationMapImageUrl(String neighborhood, String city, String country) {
        String url = UriComponentsBuilder.fromUriString(LOCATIONIQ_GEOCODING_URL)
                .queryParam("key", LOCATIONIQ_API_KEY)
                .queryParam("q", neighborhood + "," + city + "," + country)
                .queryParam("format", "json")
                .toUriString();

        logger.info("Geocoding URL: {}", url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Map<String, Object>>> responseEntity = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> response = responseEntity.getBody();

        if (response != null && !response.isEmpty()) {
            Map<String, Object> firstResult = response.get(0);
            if (firstResult.containsKey("lat") && firstResult.containsKey("lon")) {
                String lat = firstResult.get("lat").toString();
                String lon = firstResult.get("lon").toString();

                // Generate static map URL using LocationIQ
                String staticMapUrl = UriComponentsBuilder.fromUriString(LOCATIONIQ_STATIC_MAP_URL)
                        .queryParam("key", LOCATIONIQ_API_KEY)
                        .queryParam("center", lat + "," + lon)
                        .queryParam("zoom", 15)
                        .queryParam("size", "800x300")
                        .queryParam("markers", lat + "," + lon)  // Marker at location
                        .queryParam("format", "png")
                        .toUriString();

                logger.info("Static Map URL: {}", staticMapUrl);
                return staticMapUrl;
            } else {
                logger.warn("Latitude or longitude not found in LocationIQ response.");
                return null;
            }
        }

        logger.warn("No results found for location: {}, {}, {}", neighborhood, city, country);
        return null;
    }

    @RateLimiter(name = "geocodingServiceRateLimiter")
    public LocationData getLocationFromPostalCode(String postalCode) throws Exception {
        String url = UriComponentsBuilder.fromUriString("https://us1.locationiq.com/v1/search.php")
                .queryParam("key", LOCATIONIQ_API_KEY)
                .queryParam("postalcode", postalCode)
                .queryParam("format", "json")
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode results = objectMapper.readTree(response);

        if (!results.isArray() || results.isEmpty()) {
            throw new RuntimeException("Invalid postal code!");
        }

        JsonNode firstResult = results.get(0);
        String[] addressParts = firstResult.path("display_name").asText().split(",");
        String country = addressParts[addressParts.length - 1].trim();  // Get last element (country)
        String city = firstResult.path("display_name").asText().split(",")[1]; // Adjust based on response format
        String neighborhood = firstResult.path("display_name").asText().split(",")[0]; // Adjust based on response format

        return new LocationData(country, city, neighborhood, postalCode);
    }
}