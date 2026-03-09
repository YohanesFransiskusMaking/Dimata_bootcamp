package resource;

import org.eclipse.microprofile.jwt.JsonWebToken;

import dto.request.UserRequestUpdate;
import dto.response.UserResponse;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.AppUserService;

@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    AppUserService userService;
    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/me")
    @Authenticated
    public Response me() {

        Long userId = Long.parseLong(jwt.getSubject());

        UserResponse user = userService.findUserById(userId);

        return Response.ok(user).build();
    }

    @PUT
    @Path("/me")
    @Authenticated
    public Response updateMe(@Valid UserRequestUpdate request) {

        Long userId = Long.parseLong(jwt.getSubject());

        UserResponse response = userService.updateSelf(userId, request);

        return Response.ok(response).build();
    }

    @DELETE
    @Path("/me")
    @Authenticated
    public Response deleteMe() {

        Long userId = Long.parseLong(jwt.getSubject());

        userService.deleteSelf(userId);

        return Response.noContent().build();
    }

}
