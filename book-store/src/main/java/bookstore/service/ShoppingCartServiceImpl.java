package bookstore.service;

import bookstore.dto.cartItem.AddBookRequestDto;
import bookstore.dto.shoppingCart.ShoppingCartResponseDto;
import bookstore.exception.CartItemFoundException;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.ShoppingCartMapper;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.BookRepository;
import bookstore.repository.ShoppingCartRepository;
import bookstore.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User with email: " + email
                        + " doesn't exist"));

        ShoppingCart shoppingCart = getOrCreateShoppingCart(user);

        Book book = bookRepository.findById(addBookRequestDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Book with id: " + addBookRequestDto.getBookId() + " doesn't exist"));

        Optional<CartItem> existingItem = shoppingCart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().getId()
                        .equals(addBookRequestDto.getBookId()))
                .findFirst();

        if (existingItem.isPresent()) {
            throw new CartItemFoundException("Book already in the cart,"
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
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email: " + email + " doesn't exist"));
        ShoppingCart shoppingCart = getOrCreateShoppingCart(user);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto deleteABookFromTheCart(String email, Long id) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User with email: " + email
                        + " doesn't exist"));
        ShoppingCart shoppingCart = getOrCreateShoppingCart(user);
        Optional<CartItem> cartItem = shoppingCart.getCartItems().stream()
                .filter(cartItem1 -> cartItem1.getBook().getId().equals(id))
                .findFirst();
        if (cartItem.isPresent()) {
            shoppingCart.getCartItems().remove(cartItem);
            return shoppingCartMapper.toDto(shoppingCart);
        }
        throw new EntityNotFoundException("Book with id: " + id + " doesn't exist");
    }

    private ShoppingCart getOrCreateShoppingCart(User user) {
        return shoppingCartRepository.getShoppingCartByUserId(user.getId()).orElseGet(
                () -> {
                    ShoppingCart sC = new ShoppingCart();
                    sC.setUser(user);
                    return shoppingCartRepository.save(sC);
                });
    }
}
