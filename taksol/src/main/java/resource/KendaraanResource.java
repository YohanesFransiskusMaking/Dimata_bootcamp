package resource;
import java.util.List;
import dto.request.CreateKendaraanRequest;
import dto.request.UpdateKendaraanRequest;
import dto.request.UpdateStatusRequest;
import dto.response.KendaraanResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import service.KendaraanService;
import jakarta.ws.rs.core.MediaType;

@Path("/kendaraan")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class KendaraanResource {

    @Inject
    KendaraanService kendaraanService;

    // =============================
    // DRIVER ENDPOINT
    // =============================

    @POST
    @Path("/register")
    @RolesAllowed("DRIVER")
    public KendaraanResponse create(CreateKendaraanRequest request) {
        return kendaraanService.createKendaraan(request);
    }

    @GET
    @Path("/me")
    @RolesAllowed("DRIVER")
    public KendaraanResponse getMyVehicle() {
        return kendaraanService.getMyVehicle();
    }

    @PUT
    @Path("/me")
    @RolesAllowed("DRIVER")
    public KendaraanResponse updateMyVehicle(UpdateKendaraanRequest request) {
        return kendaraanService.updateMyVehicle(request);
    }

    @PUT
    @Path("/me/status")
    @RolesAllowed("DRIVER")
    public KendaraanResponse updateStatus(UpdateStatusRequest request) {
        return kendaraanService.updateStatus(request);
    }

    @DELETE
    @Path("/me")
    @RolesAllowed("DRIVER")
    public void deleteMyVehicle() {
        kendaraanService.deleteMyVehicle();
    }

    // =============================
    // ADMIN ENDPOINT
    // =============================

    @GET
    @RolesAllowed({"OPS_ADMIN", "SUPER_ADMIN"})
    public List<KendaraanResponse> getAll() {
        return kendaraanService.getAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"OPS_ADMIN", "SUPER_ADMIN"})
    public KendaraanResponse getById(@PathParam("id") Long id) {
        return kendaraanService.getById(id);
    }

    @PUT
    @Path("/{id}/status")
    @RolesAllowed({"OPS_ADMIN", "SUPER_ADMIN"})
    public KendaraanResponse updateStatusByAdmin(
            @PathParam("id") Long id,
            UpdateStatusRequest request) {
        return kendaraanService.updateStatusByAdmin(id, request);
    }
}



