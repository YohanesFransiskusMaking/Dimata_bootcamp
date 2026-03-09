package resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import service.WalletService;
import jakarta.ws.rs.core.MediaType;

@Path("/wallet/midtrans")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MidtransNotificationResource {

    @Inject
    WalletService walletService;

    @POST
    @Path("/notification")
    public Response handleNotification(String payload) {
        

        walletService.processMidtransNotification(payload);

        return Response.ok().build();
    }
}
