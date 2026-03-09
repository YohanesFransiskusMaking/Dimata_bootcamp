package resource;

import java.net.URI;

import dto.request.CreateOrderRequest;
import dto.request.EstimateOrderRequest;
import dto.request.UpdateOrderStatusRequest;
import dto.response.EstimateOrderResponse;
import dto.response.OrderResponse;
import entity.AppUser;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import service.AppUserService;
import service.OrderService;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.HttpHeaders;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class OrderResource {

    @Inject
    OrderService orderService;

    @Inject
    AppUserService userService;

    @Context
    HttpHeaders headers;

    @POST
    @RolesAllowed("CUSTOMER")
    public Response create(CreateOrderRequest request,
            @Context UriInfo uriInfo) {

        AppUser user = userService.getCurrentUser();
        OrderResponse response = orderService.createOrder(request, user);

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(response.id.toString())
                .build();

        return Response.created(uri).entity(response).build();
    }

    @POST
    @Path("/{id}/accept")
    @RolesAllowed("DRIVER")
    public Response acceptOrder(@PathParam("id") Long id) {

        OrderResponse response = orderService.acceptOrder(id);

        return Response.ok(response).build();
    }

    @PUT
    @Path("/{id}/status")
    @RolesAllowed({ "DRIVER", "CUSTOMER", "ADMIN" })
    public Response updateStatus(@PathParam("id") Long id,
            UpdateOrderStatusRequest request) {

        OrderResponse response = orderService.updateStatus(id, request);

        return Response.ok(response).build();
    }

    @GET
    @Path("/available")
    @RolesAllowed("DRIVER")
    public Response getAvailableOrders() {
        return Response.ok(orderService.getAvailableOrders()).build();
    }

    @GET
    @Path("/driver/my-active")
    @RolesAllowed("DRIVER")
    public Response getMyActiveOrder() {
        return Response.ok(orderService.getMyActiveOrder()).build();
    }

    @GET
    @Path("/customer")
    @RolesAllowed("CUSTOMER")
    public Response getCustomerOrders() {
        return Response.ok(orderService.getCustomerOrders()).build();
    }

    @GET
    @Path("/customer/my-active")
    @RolesAllowed("CUSTOMER")
    public Response getCustomerActiveOrder() {
        return Response.ok(orderService.getCustomerActiveOrder()).build();
    }

    @POST
    @Path("/estimate")
    public EstimateOrderResponse estimateOrder(EstimateOrderRequest request) {
        return orderService.estimateOrder(request);
    }

    @GET
    @Path("/driver/history")
    @RolesAllowed("DRIVER")
    public Response getDriverHistory() {
        return Response.ok(orderService.getDriverOrders()).build();
    }

    @GET
    @Path("/{id}/detail")
    @RolesAllowed({ "DRIVER", "CUSTOMER" })
    public Response getOrderDetail(@PathParam("id") Long id) {

        return Response.ok(orderService.getOrderDetail(id)).build();

    }
}