package resource;

import java.net.URI;
import java.util.List;

import dto.request.CreateJenisKendaraanRequest;
import dto.response.JenisKendaraanResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import service.JenisKendaraanService;
import jakarta.ws.rs.core.MediaType;

@Path("/jenis_kendaraan")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"OPS_ADMIN", "SUPER_ADMIN"})
public class JenisKendaraanResource {

    @Inject
    JenisKendaraanService service;

    @POST
    public Response create(@Valid CreateJenisKendaraanRequest request,
            @Context UriInfo uriInfo) {

        JenisKendaraanResponse response = service.create(request);

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(response.id.toString())
                .build();

        return Response.ok(uri).entity(response).build();
    }

    @GET
    @RolesAllowed({"DRIVER", "SUPER_ADMIN", "OPS_ADMIN", "CUSTOMER"})
    public List<JenisKendaraanResponse> list() {
        return service.findAll();
    }
}
