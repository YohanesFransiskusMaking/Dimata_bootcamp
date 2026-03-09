package dto.response;

import java.time.LocalDateTime;

public class UserResponse {
    public Long id;
    public String nama;
    public String email;
    public String noHp;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Boolean isEmailVerified;
}
