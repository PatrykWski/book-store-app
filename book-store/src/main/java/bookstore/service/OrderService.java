package bookstore.service;

import bookstore.dto.order.OrderResponseDto;
import bookstore.dto.order.PlacingOrderRequestDto;
import bookstore.dto.order.UpdateOrderStatusDto;
import bookstore.dto.orderitem.OrderItemResponseDto;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderService {
    OrderResponseDto placeOrder(String email, PlacingOrderRequestDto requestDto);

    List<OrderResponseDto> viewHistory(String email);

    Set<OrderItemResponseDto> getOrderItems(
            String email, Long orderId) throws AccessDeniedException;

    Optional<OrderItemResponseDto> getOrderItemById(String email, Long bookId);

    OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusDto statusDto);
}
