package bookstore.repository;

import bookstore.model.Order;
import bookstore.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);

    Order getOrderById(Long id);

    Order getOrderByUser(User user);
}
