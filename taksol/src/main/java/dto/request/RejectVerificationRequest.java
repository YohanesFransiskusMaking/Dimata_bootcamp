package dto.request;

import jakarta.validation.constraints.NotBlank;

public class RejectVerificationRequest {

    @NotBlank
    public String reason;
}
