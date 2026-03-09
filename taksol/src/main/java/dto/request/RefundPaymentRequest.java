package dto.request;

import java.math.BigDecimal;

public class RefundPaymentRequest {

    private String reason;
    private BigDecimal amount; // nullable untuk full refund

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
