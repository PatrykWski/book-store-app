package bookstore.service;

import bookstore.dto.order.OrderResponseDto;
import bookstore.dto.order.PlacingOrderRequestDto;
import bookstore.dto.order.UpdateOrderStatusDto;
import bookstore.dto.orderitem.OrderItemResponseDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.OrderItemMapper;
import bookstore.model.Order;
import bookstore.model.OrderItem;
import bookstore.model.ShoppingCart;
import bookstore.model.Status;
import bookstore.model.User;
import bookstore.repository.OrderItemRepository;
import bookstore.repository.OrderRepository;
import bookstore.repository.ShoppingCartRepository;
import bookstore.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(String email, PlacingOrderRequestDto requestDto) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email: " + email + " doesn't exist"));
        ShoppingCart shoppingCart = shoppingCartRepository.getShoppingCartByUserId(user.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Shopping cart doesn't exist"));

        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(orderItemMapper::toOrderItem)
                .collect(Collectors.toSet());

        BigDecimal finalPrice = countSum(orderItems);

        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.PENDING);
        order.setShippingAddress(requestDto.shippingAddress());
        order.setTotal(finalPrice);
        order.setOrderItems(new HashSet<>());

        Order savedOrder = orderRepository.save(order);

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder);
        }

        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        return orderItemMapper.toDto(savedOrder);
    }

    @Override
    public List<OrderResponseDto> viewHistory(String email) {
        return List.of();
    }

    @Override
    public Set<OrderItemResponseDto> getOrderItems(String email, Long orderId) {
        return Set.of();
    }

    @Override
    public Optional<OrderItemResponseDto> getOrderItemById(String email, Long bookId) {
        return Optional.empty();
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusDto statusDto) {
        return null;
    }

    private BigDecimal countSum(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
}
