package resource;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import security.SecurityUtil;
import service.ReviewService;
import dto.request.ReviewRequest;
import dto.response.PaginatedReviews;
import dto.response.ReviewResponse;

@Path("/reviews")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReviewResource {

    @Inject
    ReviewService reviewService;

    @Inject
    SecurityUtil securityUtil;

    @POST
    @RolesAllowed({ "CUSTOMER", "DRIVER" })
    public Response createReview(ReviewRequest request) {

        Long reviewerId = securityUtil.getCurrentUserId();

        ReviewResponse response = reviewService.createReview(
                reviewerId,
                request.getOrderId(),
                request.getRating(),
                request.getComment());

        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Path("/users/{userId}")
    @PermitAll
    public Response getReviewsByUser(
            @PathParam("userId") Long userId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        PaginatedReviews response = reviewService.getReviewsByUserPaginated(userId, page, size);

        return Response.ok(response).build();
    }
}