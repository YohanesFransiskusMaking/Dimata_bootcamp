package dto.response;

import java.util.List;

public class PaginatedReviews {

    public List<ReviewResponse> content;
    public int page;
    public int size;
    public long total;

    public PaginatedReviews(List<ReviewResponse> content,
                            int page,
                            int size,
                            long total) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.total = total;
    }
}