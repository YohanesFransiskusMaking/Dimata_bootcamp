package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import mapper.ReviewMapper;
import entity.Review;
import entity.AppUser;
import entity.Order;
import entity.OrderStatus;
import exception.ConflictException;
import repository.ReviewRepository;
import repository.AppUserRepository;
import repository.OrderRepository;

import java.util.List;

import dto.response.PaginatedReviews;
import dto.response.ReviewResponse;

@ApplicationScoped
public class ReviewService {

    @Inject
    AppUserRepository appUserRepository;
    @Inject
    ReviewRepository reviewRepository;
    @Inject
    ReviewMapper reviewMapper;
    @Inject
    OrderRepository orderRepository;

    @Transactional
    public ReviewResponse createReview(Long reviewerId,
            Long orderId,
            int rating,
            String comment) {

        Order order = orderRepository.findById(orderId);
        if (order == null)
            throw new NotFoundException("Order tidak ditemukan");

        if (order.getStatus() != OrderStatus.COMPLETED)
            throw new ConflictException("Order belum selesai");

        AppUser customer = order.getCustomer();
        AppUser driver = order.getAssignedDriver();

        if (driver == null)
            throw new ConflictException("Order belum memiliki driver");

        if (!reviewerId.equals(customer.getId()) &&
                !reviewerId.equals(driver.getId())) {
            throw new ForbiddenException("Tidak terlibat dalam order ini");
        }

        AppUser reviewee = reviewerId.equals(customer.getId()) ? driver : customer;

        if (rating < 1 || rating > 5)
            throw new BadRequestException("Rating harus 1–5");

        if (reviewRepository.existsByOrderIdAndReviewerId(orderId, reviewerId)) {
            throw new ConflictException("Anda sudah mereview order ini");
        }

        Review review = new Review();
        review.setOrder(order);
        review.setReviewer(
                customer.getId().equals(reviewerId) ? customer : driver);
        review.setReviewee(reviewee);
        review.setRating(rating);
        review.setComment(comment);

        reviewRepository.persist(review);

        updateUserRating(reviewee.getId());

        return reviewMapper.toResponse(review);
    }

    private void updateUserRating(Long userId) {

        Double avg = reviewRepository
                .getAverageRatingByUser(userId);

        Long total = reviewRepository
                .countByRevieweeId(userId);

        AppUser user = appUserRepository.findById(userId);

        user.setAverageRating(avg != null ? avg : 0.0);
        user.setTotalReviews(total.intValue());
    }

    public PaginatedReviews getReviewsByUserPaginated(Long userId,
            int page,
            int size) {

        if (page < 1)
            page = 1;
        if (size < 1)
            size = 10;

        long total = reviewRepository.countByRevieweeId(userId);

        List<Review> reviews = reviewRepository.findByRevieweeId(userId, page, size);

        List<ReviewResponse> content = reviews.stream()
                .map(reviewMapper::toResponse)
                .toList();

        return new PaginatedReviews(content, page, size, total);
    }
}