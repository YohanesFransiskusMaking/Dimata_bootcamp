package resource;

import java.util.List;

import dto.request.RejectRequest;
import dto.response.VerificationResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import security.SecurityUtil;
import service.VerificationService;

@Path("/admin/verification")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"OPS_ADMIN", "SUPER_ADMIN"})
public class VerificationAdminResource {

    @Inject
    VerificationService verificationService;

    @Inject
    SecurityUtil securityUtil; // ambil adminId dari JWT

    // Daftar semua verifikasi KYC
    @GET
    public List<VerificationResponse> allVerifications() {
        return verificationService.listAll()
                .list()
                .stream()
                .map(VerificationResponse::fromEntity)
                .toList();
    }

    // Approve KYC
    @PUT
    @Path("/{userId}/approve")
    public Response approve(@PathParam("userId") Long userId) {
        Long adminId = securityUtil.getCurrentUserId(); // ambil dari JWT
        verificationService.approveVerification(userId, adminId);
        return Response.ok().build();
    }

    // Reject KYC
    @PUT
    @Path("/{userId}/reject")
    public Response reject(@PathParam("userId") Long userId, RejectRequest request) {
        Long adminId = securityUtil.getCurrentUserId(); // ambil dari JWT
        verificationService.rejectVerification(userId, adminId, request.getReason());
        return Response.ok().build();
    }
}