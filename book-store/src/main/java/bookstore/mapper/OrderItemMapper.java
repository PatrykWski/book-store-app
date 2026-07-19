package bookstore.mapper;

import bookstore.dto.order.OrderResponseDto;
import bookstore.model.CartItem;
import bookstore.model.Order;
import bookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "price", source = "book.price")
    OrderItem toOrderItem(CartItem cartItem);

    OrderResponseDto toDto(Order order);
}
