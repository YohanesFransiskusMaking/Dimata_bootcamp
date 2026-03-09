package security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class SecurityUtil {

    @Inject
    JsonWebToken jwt;

    public Long getCurrentUserId() {
        return Long.parseLong(jwt.getSubject());
    }
}
