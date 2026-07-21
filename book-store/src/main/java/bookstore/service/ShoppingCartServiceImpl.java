package bookstore.service;

import bookstore.dto.cartitem.AddBookRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import bookstore.dto.shoppingcart.UpdateShoppingCartQuantityDto;
import bookstore.exception.CartItemAlreadyExistsException;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.ShoppingCartMapper;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.BookRepository;
import bookstore.repository.ShoppingCartRepository;
import bookstore.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional
    public ShoppingCartResponseDto addABookToACart(String email,
                                                   AddBookRequestDto addBookRequestDto) {
        User user = getUserByEmail(email);

        ShoppingCart shoppingCart = getShoppingCart(user);

        Book book = bookRepository.findById(addBookRequestDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Book with id: " + addBookRequestDto.getBookId() + " doesn't exist"));

        Optional<CartItem> existingItem = shoppingCart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().getId()
                        .equals(addBookRequestDto.getBookId()))
                .findFirst();

        if (existingItem.isPresent()) {
            throw new CartItemAlreadyExistsException("Book already in the cart,"
                    + " use update option to change details of the cart item");
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setQuantity(addBookRequestDto.getQuantity());
            cartItem.setShoppingCart(shoppingCart);
            shoppingCart.getCartItems().add(cartItem);
        }
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartResponseDto showACart(String email) {
        User user = getUserByEmail(email);

        ShoppingCart shoppingCart = getShoppingCart(user);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto deleteABookFromTheCart(String email, Long cartItemId) {
        User user = getUserByEmail(email);

        ShoppingCart shoppingCart = getShoppingCart(user);

        CartItem cartItem = getCartItem(shoppingCart, cartItemId);

        shoppingCart.getCartItems().remove(cartItem);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto updateABookInTheCart(
            String email, Long cartItemId,
            UpdateShoppingCartQuantityDto updateShoppingCartQuantityDto) {

        User user = getUserByEmail(email);

        ShoppingCart shoppingCart = getShoppingCart(user);

        CartItem cartItem = getCartItem(shoppingCart, cartItemId);

        cartItem.setQuantity(updateShoppingCartQuantityDto.quantity());

        return shoppingCartMapper.toDto(shoppingCart);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email: " + email + " doesn't exist"));
    }

    private CartItem getCartItem(ShoppingCart shoppingCart, Long cartItemId) {
        return shoppingCart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("CartItem with id: " + cartItemId
                                + " doesn't exist"));
    }

    private ShoppingCart getShoppingCart(User user) {
        return shoppingCartRepository.getShoppingCartByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart for user: "
                        + user + " doesn't exist"));
    }
}
