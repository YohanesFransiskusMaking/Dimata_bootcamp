package service;

import java.math.BigDecimal;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import integration.google.GoogleMapsClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class GoogleMapsService {

    @Inject
    @RestClient
    GoogleMapsClient client;

    @ConfigProperty(name = "google.maps.api.key")
    String apiKey;

    public BigDecimal calculateDistanceKm(
            double originLat,
            double originLng,
            double destLat,
            double destLng) {

        String origins = originLat + "," + originLng;
        String destinations = destLat + "," + destLng;

        String response = client.getDistance(origins, destinations, apiKey);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            int meters = root
                    .path("rows")
                    .get(0)
                    .path("elements")
                    .get(0)
                    .path("distance")
                    .path("value")
                    .asInt();

            return BigDecimal.valueOf(meters)
                    .divide(BigDecimal.valueOf(1000));

        } catch (Exception e) {
            throw new WebApplicationException("Gagal parsing response Google Maps", 500);
        }
    }

    public BigDecimal calculateFare(BigDecimal distanceKm) {
        BigDecimal pricePerKm = BigDecimal.valueOf(3000);
        return distanceKm.multiply(pricePerKm);
    }

    public BigDecimal calculateFareFromCoordinates(
            double originLat,
            double originLng,
            double destLat,
            double destLng) {

        BigDecimal distance = calculateDistanceKm(originLat, originLng, destLat, destLng);
        return calculateFare(distance);
    }
}