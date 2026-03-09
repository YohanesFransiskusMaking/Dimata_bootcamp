package repository;

import java.time.LocalDateTime;
import java.util.Optional;

import entity.EmailVerificationToken;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailVerificationTokenRepository 
        implements PanacheRepository<EmailVerificationToken> {

    public Optional<EmailVerificationToken> findValidToken(String tokenHash) {
        return find("tokenHash = ?1 and used = false and expiresAt > ?2",
                tokenHash, LocalDateTime.now())
                .firstResultOptional();
    }
}
