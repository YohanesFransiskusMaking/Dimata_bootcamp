package repository;

import entity.OrderStatusHistory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class OrderStatusHistoryRepository implements PanacheRepository<OrderStatusHistory> {

}
