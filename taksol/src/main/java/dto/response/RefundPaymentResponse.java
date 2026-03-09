package dto.response;

import java.math.BigDecimal;

public class RefundPaymentResponse {

    private Long paymentId;
    private BigDecimal originalAmount;
    private BigDecimal refundedAmount;
    private String refundStatus;
    private String message;
    
    public Long getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }
    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }
    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }
    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }
    public String getRefundStatus() {
        return refundStatus;
    }
    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    
}
