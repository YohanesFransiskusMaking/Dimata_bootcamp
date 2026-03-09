package resource;

import org.eclipse.microprofile.jwt.JsonWebToken;

import dto.request.SubmitVerificationRequest;
import dto.response.VerificationResponse;
import entity.UserVerification;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import service.VerificationService;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/me/verification")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VerificationResource {

    @Inject
    VerificationService verificationService;

    @Inject
    JsonWebToken jwt;

    private Long getUserId() {
        return Long.parseLong(jwt.getSubject());
    }

    @POST
    @RolesAllowed("CUSTOMER")
    public Response submit(@Valid SubmitVerificationRequest request) {

        UserVerification verification =
                verificationService.submitVerification(
                        getUserId(),
                        request.documentType,
                        request.documentPath
                );

        return Response.status(Response.Status.OK)
                .entity(toResponse(verification))
                .build();
    }

    @GET
    @RolesAllowed("CUSTOMER")
    public VerificationResponse get() {

        UserVerification verification =
                verificationService.getMyVerification(getUserId());

        return toResponse(verification);
    }

    private VerificationResponse toResponse(UserVerification v) {
        VerificationResponse r = new VerificationResponse();
        r.userId = v.getUserId();
        r.status = v.getStatus();
        r.documentType = v.getDocumentType();
        r.documentPath = v.getDocumentPath();
        r.rejectedReason = v.getRejectedReason();
        r.verifiedAt = v.getVerifiedAt();
        r.verifiedBy = v.getVerifiedBy();
        r.createdAt = v.getCreatedAt();
        r.updatedAt = v.getUpdatedAt();
        return r;
    }
}