package resource;

import java.net.URI;

import dto.request.CreatePaymentRequest;
import dto.request.RefundPaymentRequest;
import dto.response.PaymentResponse;
import dto.response.RefundPaymentResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.PaymentService;

@Path("/payments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PaymentResource {

    @Inject
    PaymentService paymentService;

    @POST
    @RolesAllowed("CUSTOMER")
    public Response create(CreatePaymentRequest req) {

        PaymentResponse response = paymentService.create(req);

        return Response
                .created(URI.create("/payments/" + response.getId()))
                .entity(response)
                .build();

    }

    @POST
    @RolesAllowed("CUSTOMER")
    @Path("/{paymentId}/refund")
    public Response refund(@PathParam("paymentId") Long paymentId,
            RefundPaymentRequest req) {

        RefundPaymentResponse response = paymentService.refund(paymentId, req);

        return Response.ok(response).build();
    }

    @PUT
    @Path("/{paymentId}/confirm-cash")
    @RolesAllowed("DRIVER")
    public Response confirmCash(@PathParam("paymentId") Long paymentId) {
        paymentService.confirmCash(paymentId);
        return Response.ok().build();
    }

    @GET
    @Path("/order/{orderId}")
    @RolesAllowed({ "DRIVER", "CUSTOMER" })
    public Response getPaymentByOrder(@PathParam("orderId") Long orderId) {

        return Response.ok(paymentService.getPaymentByOrder(orderId)).build();

    }

}
