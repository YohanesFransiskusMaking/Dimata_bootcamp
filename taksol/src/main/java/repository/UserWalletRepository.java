package repository;

import entity.UserWallet;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

@ApplicationScoped
public class UserWalletRepository implements PanacheRepository<UserWallet> {
    public UserWallet findByIdForUpdate(Long id) {
    return findById(id, LockModeType.PESSIMISTIC_WRITE);
}
}
