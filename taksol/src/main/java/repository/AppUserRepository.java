package repository;

import java.util.Optional;

import entity.AppUser;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppUserRepository implements PanacheRepository<AppUser> {

    public boolean existsByEmail(String email) {
        return count("email = ?1", email) > 0;
    }

    public boolean existsByNoHp(String noHp) {
        return count("noHp = ?1", noHp) > 0;
    }

    public Optional<AppUser> findByEmail(String email) {
    return find("email", email).firstResultOptional();
}

}
