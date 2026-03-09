package repository;

import entity.OrderDetail;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderDetailRepository implements PanacheRepository<OrderDetail> {

}
