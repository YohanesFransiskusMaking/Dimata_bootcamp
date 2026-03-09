package dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoleRequest {

    @NotBlank
    @Size(max = 50)
    public String role;
}
