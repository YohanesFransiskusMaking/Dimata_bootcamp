package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import entity.AppUser;
import entity.Role;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenUtil {

    public String generateAccessToken(AppUser user) {

        Set<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());

        return Jwt.issuer("taksol-app")
                .upn(user.getEmail())
                .subject(user.getId().toString())
                .claim("jti", UUID.randomUUID().toString())
                .claim("email", user.getEmail())
                .groups(roleNames)
                .expiresIn(Duration.ofMinutes(15))
                .sign();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID();
    }

    public String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token");
        }
    }

}
