package dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

    public Long id;
    public String lokasiJemput;
    public String lokasiTujuan;
    public Long assignedDriverId;
    public String status;
    public String statusPembayaran;
    public BigDecimal hargaTotal;
    public LocalDateTime createdAt;
}

