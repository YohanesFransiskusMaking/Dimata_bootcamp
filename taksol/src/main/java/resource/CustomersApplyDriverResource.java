package resource;

import org.eclipse.microprofile.jwt.JsonWebToken;

import dto.request.DriverApplyRequest;
import dto.response.DriverApplyStatusResponse;
import entity.DriverProfile;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mapper.DriverProfileMapper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import service.AppUserService;

@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class CustomersApplyDriverResource {

    @Inject
    AppUserService userService;

    @Inject
    DriverProfileMapper driverMapper;
    
    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/apply")
    @RolesAllowed("CUSTOMER")
    public Response applyDriver(@Valid DriverApplyRequest request) {

        Long userId = Long.parseLong(jwt.getSubject());

        userService.applyDriver(userId, request);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/apply/status")
    @RolesAllowed("CUSTOMER")
    public Response getApplyStatus() {

        Long userId = Long.parseLong(jwt.getSubject());

        DriverProfile profile = userService.getMyDriverProfile(userId);
        DriverApplyStatusResponse response = driverMapper.toStatusResponse(profile);

        return Response.ok(response).build();
    }
}
