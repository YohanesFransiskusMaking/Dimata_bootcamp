package resource;

import dto.request.RoleRequest;
import dto.response.RoleResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.RoleService;

import java.util.List;

@Path("admin/roles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("SUPER_ADMIN")
public class RoleResource {

    @Inject
    RoleService service;

    @POST
    public Response create(@Valid RoleRequest request) {
        RoleResponse response = service.create(request);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @GET
    public List<RoleResponse> findAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public RoleResponse findById(@PathParam("id") Integer id) {
        return service.findById(id);
    }

    @PUT
    @Path("/{id}")
    public RoleResponse update(@PathParam("id") Integer id,
                               @Valid RoleRequest request) {
        return service.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
