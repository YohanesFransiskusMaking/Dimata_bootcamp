package resource;

import java.math.BigDecimal;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import service.GoogleMapsService;

@Path("/test-google")
public class TestGoogleResource {

    @Inject
    GoogleMapsService service;

    @GET
    public BigDecimal test(
            @QueryParam("oLat") double oLat,
            @QueryParam("oLng") double oLng,
            @QueryParam("dLat") double dLat,
            @QueryParam("dLng") double dLng) {
        return service.calculateFareFromCoordinates(oLat, oLng, dLat, dLng);
    }
}