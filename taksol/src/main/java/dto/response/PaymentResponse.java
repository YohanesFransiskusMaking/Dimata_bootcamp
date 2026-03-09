package dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import entity.PaymentMethod;
import entity.PaymentStatus;

public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal jumlah;
    private PaymentMethod metode;
    private PaymentStatus status;
    private LocalDateTime waktuPembayaran;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public BigDecimal getJumlah() {
        return jumlah;
    }
    public void setJumlah(BigDecimal jumlah) {
        this.jumlah = jumlah;
    }
    public PaymentStatus getStatus() {
        return status;
    }
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    public PaymentMethod getMetode() {
        return metode;
    }
    public void setMetode(PaymentMethod metode) {
        this.metode = metode;
    }
  
    public LocalDateTime getWaktuPembayaran() {
        return waktuPembayaran;
    }
    public void setWaktuPembayaran(LocalDateTime waktuPembayaran) {
        this.waktuPembayaran = waktuPembayaran;
    }

    
}

