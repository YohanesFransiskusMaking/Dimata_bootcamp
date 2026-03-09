package resource;

import dto.request.AssignRoleRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.AppUserService;

@Path("/super-admin/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("SUPER_ADMIN")
public class SuperAdminUserResource {

    @Inject
    AppUserService userService;

    @POST
    @Path("/{userId}/assign-role")
    @Transactional
    public Response assignRole(@PathParam("userId") Long userId,
                               @Valid AssignRoleRequest request) {

        userService.assignRole(userId, request.getRole());

        return Response.ok().build();
    }
}