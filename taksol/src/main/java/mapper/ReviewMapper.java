package mapper;

import jakarta.enterprise.context.ApplicationScoped;
import entity.Review;
import dto.response.ReviewResponse;

@ApplicationScoped
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {

        ReviewResponse res = new ReviewResponse();
        res.id = review.getId();
        res.orderId = review.getOrder().getId();
        res.reviewerId = review.getReviewer().getId();
        res.revieweeId = review.getReviewee().getId();
        res.rating = review.getRating();
        res.comment = review.getComment();
        res.createdAt = review.getCreatedAt();

        return res;
    }
}