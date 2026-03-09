package security;

import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.security.Principal;

@ApplicationScoped
public class JwtUtil {

    @Inject
    JWTParser parser;

    public Long getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authHeader.substring(7); // hapus "Bearer "

        try {
            // decode token
            Principal principal = parser.parse(token);

            // subject di TokenUtil kamu adalah userId
            return Long.valueOf(principal.getName());
        } catch (ParseException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}