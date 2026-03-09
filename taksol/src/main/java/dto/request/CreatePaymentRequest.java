package dto.request;

import java.math.BigDecimal;

import entity.PaymentMethod;

public class CreatePaymentRequest {

    private Long orderId;
    private BigDecimal jumlah;
    private PaymentMethod metode;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public BigDecimal getJumlah() { return jumlah; }
    public void setJumlah(BigDecimal jumlah) { this.jumlah = jumlah; }

    public PaymentMethod getMetode() { return metode; }
    public void setMetode(PaymentMethod metode) { this.metode = metode; }
}

