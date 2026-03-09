package resource;

import dto.request.AppConfigRequest;
import dto.response.AppConfigResponse;
import entity.AppConfig;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.AppConfigService;
import security.SecurityUtil;


@Path("/app-config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"FINANCE_ADMIN", "SUPER_ADMIN"})
public class AppConfigResource {

    @Inject
    AppConfigService service;

    @Inject
    SecurityUtil securityUtil;

    @GET
    @Path("/{key}")
    public Response getConfig(@PathParam("key") String key) {
        AppConfig config = service.getConfigByKey(key, true);
        return Response.ok(AppConfigResponse.fromEntity(config)).build();
    }

    // CREATE
    @POST
    public Response createConfig(AppConfigRequest request) {
        AppConfig created = service.createConfig(
                request.getKey(), request.getValue(), request.getDescription());
        return Response.status(Response.Status.OK)
                .entity(AppConfigResponse.fromEntity(created))
                .build();
    }

    // UPDATE
    @PUT
    @Path("/{key}")
    public Response updateConfig(@PathParam("key") String key, AppConfigRequest request) {
        AppConfig updated = service.updateConfig(
                key, request.getValue(), request.getDescription());
        return Response.ok(AppConfigResponse.fromEntity(updated)).build();
    }

    @DELETE
    @Path("/{key}")
    public Response deleteConfig(@PathParam("key") String key) {
        service.deleteConfig(key);
        return Response.noContent().build();
    }
}