package bookstore.repository;

import bookstore.model.Order;
import bookstore.model.OrderItem;
import bookstore.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserOrderByOrderDateDesc(User user);

    @Query(
            "SELECT DISTINCT i"
                    + " FROM Order o"
                    + " JOIN o.orderItems i"
                    + " WHERE o.id = :orderId AND i.id = :itemId"
    )

    Optional<OrderItem> findByIdAndOrderItemsId(@Param("itemId") Long itemId,
            @Param("orderId") Long orderId);
}
