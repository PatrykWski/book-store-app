package bookstore.service;

import bookstore.dto.order.OrderResponseDto;
import bookstore.dto.order.PlacingOrderRequestDto;
import bookstore.dto.order.UpdateOrderStatusDto;
import bookstore.dto.orderitem.OrderItemResponseDto;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;

public interface OrderService {
    OrderResponseDto placeOrder(String email, PlacingOrderRequestDto requestDto);

    List<OrderResponseDto> viewHistory(String email);

    Set<OrderItemResponseDto> getOrderItems(
            String email, Long orderId) throws AccessDeniedException;

    OrderItemResponseDto getOrderItemById(String email, Long itemId, Long orderId);

    OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusDto statusDto);
}
