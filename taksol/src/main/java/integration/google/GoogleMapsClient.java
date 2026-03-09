package integration.google;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/maps/api/distancematrix")
@RegisterRestClient(configKey = "google-maps")
public interface GoogleMapsClient {

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    String getDistance(
            @QueryParam("origins") String origins,
            @QueryParam("destinations") String destinations,
            @QueryParam("key") String apiKey
    );
}