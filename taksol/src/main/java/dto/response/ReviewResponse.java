package dto.response;

import java.time.LocalDateTime;

public class ReviewResponse {
    public Long id;
    public Long orderId;
    public Long reviewerId;
    public Long revieweeId;
    public int rating;
    public String comment;
    public LocalDateTime createdAt;


}
