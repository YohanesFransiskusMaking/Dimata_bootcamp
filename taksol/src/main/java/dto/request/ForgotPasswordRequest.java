package dto.request;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {
    @NotBlank
    public String email;
}
