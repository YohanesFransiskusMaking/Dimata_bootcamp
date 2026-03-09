package repository;

import entity.DriverProfile;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DriverProfileRepository implements PanacheRepository<DriverProfile> {
}
