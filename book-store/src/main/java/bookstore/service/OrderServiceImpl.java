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
import java.nio.file.AccessDeniedException;
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

        return orderItemMapper.toOrderResponseDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> viewHistory(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email: " + email + " doesn't exist"));

        List<Order> orders = orderRepository.findAllByUser(user);

        return orders.stream()
                .map(orderItemMapper::toOrderResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<OrderItemResponseDto> getOrderItems(
            String email, Long orderId) throws AccessDeniedException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email: " + email + " doesn't exist"));

        Order order = orderRepository.getOrderById(orderId);

        if (order != null && order.getUser().equals(user)) {
            return order.getOrderItems().stream()
                    .map(orderItemMapper::toItemResponseDto)
                    .collect(Collectors.toSet());
        }
        throw new AccessDeniedException("You have no access to this method");
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponseDto getOrderItemById(String email, Long bookId) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email: " + email + " doesn't exist"));

        Order order = orderRepository.getOrderByUser(user);

        Optional<OrderItem> foundItem = order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getBook().getId().equals(bookId))
                .findFirst();

        if (foundItem.isPresent()) {
            return orderItemMapper.toItemResponseDto(foundItem.get());
        }

        throw new EntityNotFoundException("Book with id: " + bookId + " doesn't exist");
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusDto statusDto) {

        Order order = orderRepository.getOrderById(orderId);

        order.setStatus(statusDto.status());
        Order savedOrder = orderRepository.save(order);

        return orderItemMapper.toOrderResponseDto(savedOrder);
    }

    private BigDecimal countSum(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
}
