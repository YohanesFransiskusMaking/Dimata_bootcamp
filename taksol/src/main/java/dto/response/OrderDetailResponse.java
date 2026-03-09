package dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDetailResponse {

    public Long id;
    public Long orderId;
    public Long jenisKendaraanId;
    public Long kendaraanId;
    public BigDecimal jarakKm;
    public BigDecimal tarifPerKm;
    public BigDecimal subtotal;
    public LocalDateTime createdAt;
}
