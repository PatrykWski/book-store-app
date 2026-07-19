package bookstore.service;

import bookstore.dto.cartItem.AddBookRequestDto;
import bookstore.dto.shoppingCart.ShoppingCartResponseDto;

public interface ShoppingCartService {

    ShoppingCartResponseDto addABookToACart(String email, AddBookRequestDto addBookRequestDto);
    ShoppingCartResponseDto showACart(String email);
    ShoppingCartResponseDto deleteABookFromTheCart(String email, Long id);
}
