package bookstore.dto.order;

import bookstore.dto.orderitem.OrderItemResponseDto;
import bookstore.model.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private Status status;
    private BigDecimal total;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private Set<OrderItemResponseDto> orderItems;
}
