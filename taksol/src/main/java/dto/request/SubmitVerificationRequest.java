package dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubmitVerificationRequest {

    @NotBlank
    @Size(max = 50)
    public String documentType;

    @NotBlank
    @Size(max = 255)
    public String documentPath;
}
