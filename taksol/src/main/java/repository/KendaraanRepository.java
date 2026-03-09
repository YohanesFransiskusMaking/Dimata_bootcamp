package repository;

import java.util.Optional;

import entity.AppUser;
import entity.Kendaraan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KendaraanRepository implements PanacheRepository<Kendaraan> {

    public Optional<Kendaraan> findByPlat(String plat) {
        return find("platNomor", plat).firstResultOptional();
    }

    public Optional<Kendaraan> findActiveByDriver(AppUser driver) {
        return find("driver = ?1 and status = 'ACTIVE'", driver)
                .firstResultOptional();
    }

    public Optional<Kendaraan> findByDriver(AppUser driver) {
        return find("driver", driver).firstResultOptional();
    }

}
