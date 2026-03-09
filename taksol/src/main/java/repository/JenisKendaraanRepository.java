package repository;

import java.util.Optional;

import entity.JenisKendaraan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JenisKendaraanRepository implements PanacheRepository<JenisKendaraan> {

    public Optional<JenisKendaraan> findActiveByNama(String nama) {
        return find("namaJenis = ?1 and deletedAt is null", nama)
                .firstResultOptional();
    }
}

