package dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Email format tidak valid")
    public String email;

    @NotBlank(message = "Password tidak boleh kosong")
    public String password;

    @NotBlank
    public String deviceId;

}
