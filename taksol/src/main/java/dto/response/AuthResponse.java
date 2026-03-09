package dto.response;

public class AuthResponse {
    public String accessToken;
    public String refreshToken;
    public long expiresIn;
    public String tokenType = "Bearer";
    public long refreshExpiresIn;


     public AuthResponse(String accessToken, String refreshToken, long expiresIn, long refreshExpiresIn, String tokenType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.refreshExpiresIn = refreshExpiresIn;
    }

}
