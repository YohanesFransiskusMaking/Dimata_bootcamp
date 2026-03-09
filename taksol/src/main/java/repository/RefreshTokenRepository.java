package repository;

import java.time.LocalDateTime;
import java.util.Optional;

import entity.RefreshToken;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RefreshTokenRepository implements PanacheRepository<RefreshToken> {

    public void revokeAllByUser(Long userId) {
        update("revoked = true where user.id = ?1", userId);
    }

    public Optional<RefreshToken> findValidToken(String tokenHash) {
        return find("tokenHash = ?1 and revoked = false and expiresAt > ?2",
                tokenHash, LocalDateTime.now())
                .firstResultOptional();
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
    }

    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return find("tokenHash = ?1", tokenHash)
                .firstResultOptional();
    }

}
