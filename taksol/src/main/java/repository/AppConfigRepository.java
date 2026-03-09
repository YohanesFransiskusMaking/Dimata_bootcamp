package repository;

import entity.AppConfig;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppConfigRepository implements PanacheRepository<AppConfig> {
    
}