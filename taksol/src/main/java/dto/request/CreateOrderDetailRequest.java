package dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateOrderDetailRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private Long jenisKendaraanId;

    @NotNull
    private Long kendaraanId;

    @NotNull
    @Min(0)
    private BigDecimal jarakKm;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getJenisKendaraanId() {
        return jenisKendaraanId;
    }

    public void setJenisKendaraanId(Long jenisKendaraanId) {
        this.jenisKendaraanId = jenisKendaraanId;
    }

    public Long getKendaraanId() {
        return kendaraanId;
    }

    public void setKendaraanId(Long kendaraanId) {
        this.kendaraanId = kendaraanId;
    }

    public BigDecimal getJarakKm() {
        return jarakKm;
    }

    public void setJarakKm(BigDecimal jarakKm) {
        this.jarakKm = jarakKm;
    }

    
}
