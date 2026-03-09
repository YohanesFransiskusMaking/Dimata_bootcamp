package dto.response;

import java.math.BigDecimal;

public class WalletResponse {

    public BigDecimal balance;
    public String message;

    public WalletResponse(BigDecimal balance, String message) {
        this.balance = balance;
        this.message = message;
    }
}
