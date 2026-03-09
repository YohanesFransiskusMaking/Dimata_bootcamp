package repository;

import jakarta.enterprise.context.ApplicationScoped;
import entity.Review;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

@ApplicationScoped
public class ReviewRepository implements PanacheRepository<Review> {

    /**
     * Cek apakah reviewer sudah membuat review untuk order tertentu
     */
    public boolean existsByOrderIdAndReviewerId(Long orderId, Long reviewerId) {
        return count("order.id = ?1 and reviewer.id = ?2",
                orderId, reviewerId) > 0;
    }

    /**
     * Ambil review berdasarkan reviewee dengan pagination
     */
    public List<Review> findByRevieweeId(Long revieweeId,
                                         int page,
                                         int size) {

        return find("reviewee.id = ?1 ORDER BY createdAt DESC",
                revieweeId)
                .page(page - 1, size)
                .list();
    }

    /**
     * Hitung total review diterima user
     */
    public long countByRevieweeId(Long revieweeId) {
        return count("reviewee.id", revieweeId);
    }

    /**
     * Ambil rata-rata rating user
     */
    public Double getAverageRatingByUser(Long userId) {
        return getEntityManager()
                .createQuery(
                        "SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :userId",
                        Double.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

}
