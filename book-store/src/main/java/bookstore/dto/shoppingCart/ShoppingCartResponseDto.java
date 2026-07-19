package bookstore.dto.shoppingCart;

import bookstore.model.CartItem;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingCartResponseDto {
    private Long id;
    private Long userId;
    private Set<CartItem> cartItems;
}
