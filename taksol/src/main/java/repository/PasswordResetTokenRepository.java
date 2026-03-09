package repository;

import entity.PasswordResetToken;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordResetTokenRepository implements PanacheRepository<PasswordResetToken> {

    public PasswordResetToken findByTokenHash(String hash) {
        return find("tokenHash", hash).firstResult();
    }
}
