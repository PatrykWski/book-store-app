package bookstore.dto.shoppingcart;

import jakarta.validation.constraints.Positive;

public record UpdateShoppingCartQuantityDto(
                @Positive
                int quantity){
}
