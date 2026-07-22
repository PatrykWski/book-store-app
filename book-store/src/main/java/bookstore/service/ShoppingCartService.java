package bookstore.service;

import bookstore.dto.cartitem.AddBookRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import bookstore.dto.shoppingcart.UpdateShoppingCartQuantityDto;

public interface ShoppingCartService {

    ShoppingCartResponseDto addABookToACart(String email, AddBookRequestDto addBookRequestDto);

    ShoppingCartResponseDto showACart(String email);

    ShoppingCartResponseDto deleteABookFromTheCart(String email, Long id);

    ShoppingCartResponseDto updateABookInTheCart(
            String email, Long id, UpdateShoppingCartQuantityDto updateShoppingCartQuantityDto);
}
