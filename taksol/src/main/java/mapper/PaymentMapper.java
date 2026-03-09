package mapper;

import dto.response.PaymentResponse;
import entity.Payment;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {

        PaymentResponse res = new PaymentResponse();
        res.setId(payment.getId());
        res.setOrderId(payment.getOrder().getId());
        res.setJumlah(payment.getJumlah());
        res.setMetode(payment.getMetode());
        res.setStatus(payment.getPaymentStatus());
        res.setWaktuPembayaran(payment.getWaktuPembayaran());
        return res;
    }
}
