package resource;

import java.util.List;

import dto.response.UserResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import security.SecurityUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import service.AppUserService;

@Path("/admin/users")
@RolesAllowed("SUPER_ADMIN")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminUserResource {

    @Inject
    AppUserService service;

    @Inject
    SecurityUtil securityUtil;

    @GET
    public List<UserResponse> findAll() {
        return service.findAllUsers();
    }

    @GET
    @Path("/{id}")
    public UserResponse findById(@PathParam("id") Long id) {
        return service.findUserById(id);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {

        Long currentAdminId = securityUtil.getCurrentUserId();

        service.deleteUserByAdmin(id, currentAdminId);

        return Response.ok().build();
    }

    

}
