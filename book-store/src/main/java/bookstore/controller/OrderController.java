package bookstore.controller;

import bookstore.dto.order.OrderResponseDto;
import bookstore.dto.order.PlacingOrderRequestDto;
import bookstore.dto.order.UpdateOrderStatusDto;
import bookstore.dto.orderitem.OrderItemResponseDto;
import bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order management", description = "Endpoints for managing orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an order", description = "Create an order")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public OrderResponseDto placeOrder(@AuthenticationPrincipal String email,
                                       @RequestBody @Valid PlacingOrderRequestDto requestDto) {
        return orderService.placeOrder(email, requestDto);
    }

    @GetMapping
    @Operation(summary = "View history", description = "View history of the user")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<OrderResponseDto> viewHistory(@AuthenticationPrincipal String email) {
        return orderService.viewHistory(email);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all order items", description = "Get all order items")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Set<OrderItemResponseDto> getOrderItems(
            @AuthenticationPrincipal String email, @PathVariable Long orderId)
            throws AccessDeniedException {
        return orderService.getOrderItems(email, orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get an item", description = "Get an item by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public OrderItemResponseDto getOrderItemById(@AuthenticationPrincipal String email,
                                                 @PathVariable Long orderId,
                                                 @PathVariable Long itemId) {
        return orderService.findOrderItemByOrderIdAndItemId(email, orderId, itemId);
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an order", description = "Update an order by status using ID")
    public OrderResponseDto updateOrderStatus(@PathVariable Long orderId,
                                              @Valid @RequestBody UpdateOrderStatusDto statusDto) {
        return orderService.updateOrderStatus(orderId, statusDto);
    }
}
