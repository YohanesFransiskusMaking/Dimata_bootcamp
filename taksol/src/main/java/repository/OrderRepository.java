package repository;

import java.util.List;

import entity.Order;
import entity.OrderStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public List<Order> findAvailableOrders() {
        return find("status = ?1 and assignedDriver is null", OrderStatus.PENDING)
                .list();
    }

}
