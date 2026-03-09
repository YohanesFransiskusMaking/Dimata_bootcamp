package resource;

import dto.request.TopUpRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import org.eclipse.microprofile.jwt.JsonWebToken;
import service.WalletService;

@Path("/wallet")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WalletResource {

    @Inject
    WalletService walletService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/topup/midtrans")
    @RolesAllowed({ "CUSTOMER", "DRIVER" })
    public Response createMidtransTopup(@Valid TopUpRequest request) throws Exception {

        Long userId = Long.parseLong(jwt.getSubject());

        String snapToken = walletService.createMidtransTransaction(userId, request.getAmount());

        return Response.ok(Map.of("snapToken", snapToken)).build();
    }

    @GET
    @RolesAllowed({ "CUSTOMER", "DRIVER" })
    public Response getWallet() {

        Long userId = Long.parseLong(jwt.getSubject());

        return Response.ok(walletService.getWallet(userId)).build();

    }

    @GET
    @Path("/transactions")
    @RolesAllowed({ "CUSTOMER", "DRIVER" })
    public Response getTransactions() {

        Long userId = Long.parseLong(jwt.getSubject());

        return Response.ok(walletService.getTransactions(userId)).build();
    }

}
