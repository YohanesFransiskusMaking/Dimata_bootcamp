package dto.request;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequest {
    @NotBlank
    public String refreshToken;

    @NotBlank
    public String deviceId;

}
