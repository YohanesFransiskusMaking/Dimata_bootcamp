package repository;

import entity.UserVerification;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserVerificationRepository implements PanacheRepository<UserVerification> {
}