package entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"order_id", "reviewer_id"}
    )
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public AppUser getReviewer() {
        return reviewer;
    }

    public void setReviewer(AppUser reviewer) {
        this.reviewer = reviewer;
    }

    public AppUser getReviewee() {
        return reviewee;
    }

    public void setReviewee(AppUser reviewee) {
        this.reviewee = reviewee;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private AppUser reviewer;

    @ManyToOne
    @JoinColumn(name = "reviewee_id", nullable = false)
    private AppUser reviewee;

    private int rating;

    @Column(length = 500)
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}