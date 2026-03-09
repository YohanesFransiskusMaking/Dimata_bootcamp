package resource;

import java.util.List;

import dto.request.RejectRequest;
import dto.response.DriverAdminResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mapper.DriverProfileMapper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import service.AppUserService;

@Path("/admin/drivers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"OPS_ADMIN", "SUPER_ADMIN"})
public class AdminDriverResource {

    @Inject
    AppUserService userService;
    @Inject
    DriverProfileMapper driverMapper;

    @PUT
    @Path("/{userId}/approve")
    public Response approve(@PathParam("userId") Long userId) {

        userService.approveDriver(userId);

        return Response.ok().build();
    }

    @GET
    @Path("/pending")
    public List<DriverAdminResponse> pendingDrivers() {
        return userService.findPendingDrivers();
    }

    @PUT
    @Path("/{userId}/reject")
    public Response reject(@PathParam("userId") Long userId, RejectRequest request) {
        userService.rejectDriver(userId, request.getReason());
        return Response.ok().build();
    }

    @PUT
    @Path("/{userId}/suspend")
    public Response suspend(@PathParam("userId") Long userId) {
        userService.suspendDriver(userId);
        return Response.ok().build();
    }

    @PUT
    @Path("/{userId}/reactivate")
    public Response reactivate(@PathParam("userId") Long userId) {
        userService.reactivateDriver(userId);
        return Response.ok().build();
    }

    @GET
    public List<DriverAdminResponse> allDrivers() {
        return userService.findAllDrivers();
    }

}
