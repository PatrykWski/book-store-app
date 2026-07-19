package bookstore.mapper;

import bookstore.dto.shoppingCart.ShoppingCartResponseDto;
import bookstore.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);
}
