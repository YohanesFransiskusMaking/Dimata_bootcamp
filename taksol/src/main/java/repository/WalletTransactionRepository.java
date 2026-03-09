package repository;

import entity.WalletTransaction;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

@ApplicationScoped
public class WalletTransactionRepository implements PanacheRepository<WalletTransaction> {

    public WalletTransaction findByMidtransOrderIdForUpdate(String orderId) {
    return find("midtransOrderId", orderId)
            .withLock(LockModeType.PESSIMISTIC_WRITE)
            .firstResult();
}
}
