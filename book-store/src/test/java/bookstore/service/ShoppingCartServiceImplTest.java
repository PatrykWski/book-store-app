package bookstore.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.dto.cartitem.AddBookRequestDto;
import bookstore.dto.shoppingcart.CartItemDto;
import bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import bookstore.dto.shoppingcart.UpdateShoppingCartQuantityDto;
import bookstore.exception.CartItemAlreadyExistsException;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.ShoppingCartMapper;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.Category;
import bookstore.model.Role;
import bookstore.model.RoleName;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.BookRepository;
import bookstore.repository.ShoppingCartRepository;
import bookstore.repository.UserRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceImplTest {
    private static final String CORRECT_TEST_EMAIL = "patryk@gmail.com";
    private static final String INCORRECT_TEST_EMAIL = "patrykgmail.com";
    private static final Long INCORRECT_CART_ITEM_ID = 5L;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    public void addABookToACart_ValidRequest_ReturnShoppingCartResponseDto() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        AddBookRequestDto addBookRequestDto = createValidAddBookRequestDto();
        Book book = createValidBook();
        CartItemDto cartItemDto = createValidCartItemDto();
        ShoppingCartResponseDto expected = createValidShoppingCartResponseDto(user, cartItemDto);

        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(addBookRequestDto.getBookId())).thenReturn(Optional.of(book));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        //when
        ShoppingCartResponseDto actual = shoppingCartService.addABookToACart(
                CORRECT_TEST_EMAIL, addBookRequestDto);

        //then
        Assertions.assertEquals(expected, actual);

    }

    @Test
    public void addABookToACart_InvalidEmail_ReturnsEntityNotFoundException() {
        //given
        AddBookRequestDto addBookRequestDto = createValidAddBookRequestDto();
        when(userRepository.findByEmail(INCORRECT_TEST_EMAIL)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addABookToACart(
                        INCORRECT_TEST_EMAIL, addBookRequestDto));
    }

    @Test
    public void addABookToACart_InvalidShoppingCart_ReturnsEntityNotFoundException() {
        //given
        User user = createValidUser();
        AddBookRequestDto addBookRequestDto = createValidAddBookRequestDto();
        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addABookToACart(
                        CORRECT_TEST_EMAIL, addBookRequestDto));
    }

    @Test
    public void addABookToACart_BookDoesNotExist_ReturnsEntityNotFoundException() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        AddBookRequestDto addBookRequestDto = createValidAddBookRequestDto();

        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(addBookRequestDto.getBookId())).thenReturn(Optional.empty());

        //when &then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addABookToACart(CORRECT_TEST_EMAIL, addBookRequestDto));
    }

    @Test
    public void addABookToACart_BookAlreadyInTheCart_ReturnsCartItemAlreadyExistsException() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        CartItem cartItem = createValidCartItem(shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
        AddBookRequestDto addBookRequestDto = createValidAddBookRequestDto();
        Book book = createValidBook();

        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(addBookRequestDto.getBookId())).thenReturn(Optional.of(book));

        //when &then
        Assertions.assertThrows(CartItemAlreadyExistsException.class,
                () -> shoppingCartService.addABookToACart(CORRECT_TEST_EMAIL, addBookRequestDto));
    }

    @Test
    public void showACart_ValidEmail_ReturnsShoppingCartResponseDto() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        CartItemDto cartItem = createValidCartItemDto();
        ShoppingCartResponseDto expected = createValidShoppingCartResponseDto(
                user, cartItem);
        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        //when
        ShoppingCartResponseDto actual = shoppingCartService.showACart(CORRECT_TEST_EMAIL);

        //then
        Assertions.assertEquals(expected, actual);

    }

    @Test
    public void showACart_InvalidEmail_ReturnsEntityNotFoundException() {
        //given
        when(userRepository.findByEmail(INCORRECT_TEST_EMAIL)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.showACart(INCORRECT_TEST_EMAIL));
    }

    @Test
    public void showACart_ShoppingCartDoesNotExist_ReturnsEntityNotFoundException() {
        //given
        User user = createValidUser();
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.showACart(CORRECT_TEST_EMAIL));
    }

    @Test
    public void deleteABookFromTheCart_ValidParams_ReturnsShoppingCartResponseDto() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        CartItem cartItem = createValidCartItem(shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
        CartItemDto cartItemDto = createValidCartItemDto();
        ShoppingCartResponseDto expected = createValidShoppingCartResponseDto(
                user, cartItemDto);
        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        //when
        ShoppingCartResponseDto actual = shoppingCartService
                .deleteABookFromTheCart(CORRECT_TEST_EMAIL, cartItem.getId());

        //then
        Assertions.assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    public void deleteABookFromTheCart_InvalidEmail_ReturnsEntityNotFoundException() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        CartItem cartItem = createValidCartItem(shoppingCart);

        when(userRepository.findByEmail(INCORRECT_TEST_EMAIL)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.deleteABookFromTheCart(
                        INCORRECT_TEST_EMAIL, cartItem.getId()));
    }

    @Test
    public void deleteABookFromTheCart_ShoppingCartDoesNotExist_ReturnsEntityNotFoundException() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        CartItem cartItem = createValidCartItem(shoppingCart);

        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService
                        .deleteABookFromTheCart(CORRECT_TEST_EMAIL, cartItem.getId()));
    }

    @Test
    public void deleteABookFromTheCart_CartItemDoesNotExist_ReturnsEntityNotFoundException() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);

        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.deleteABookFromTheCart(
                        CORRECT_TEST_EMAIL, INCORRECT_CART_ITEM_ID));
    }

    @Test
    public void updateABookInTheCart_ValidParams_ReturnsShoppingCartResponseDto() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        CartItem cartItem = createValidCartItem(shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
        CartItemDto cartItemDto = createUpdatedCartItemDto();
        ShoppingCartResponseDto expected = createValidShoppingCartResponseDto(user, cartItemDto);
        UpdateShoppingCartQuantityDto update = new UpdateShoppingCartQuantityDto(10);

        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        //when
        ShoppingCartResponseDto actual = shoppingCartService
                .updateABookInTheCart(CORRECT_TEST_EMAIL, cartItem.getId(), update);

        //then
        Assertions.assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    public void updateABookInTheCart_InvalidEmail_ReturnsEntityNotFoundException() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        CartItem cartItem = createValidCartItem(shoppingCart);
        UpdateShoppingCartQuantityDto update = new UpdateShoppingCartQuantityDto(10);
        when(userRepository.findByEmail(INCORRECT_TEST_EMAIL)).thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateABookInTheCart(
                        INCORRECT_TEST_EMAIL, cartItem.getId(), update));
    }

    @Test
    public void updateABookInTheCart_ShoppingCartDoesNotExist_ReturnsEntityNotFound() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        CartItem cartItem = createValidCartItem(shoppingCart);
        UpdateShoppingCartQuantityDto update = new UpdateShoppingCartQuantityDto(10);
        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.empty());

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateABookInTheCart(
                        CORRECT_TEST_EMAIL, cartItem.getId(), update));
    }

    @Test
    public void updateABookInTheCart_InvalidCartItemId_ReturnsEntityNotFoundException() {
        //given
        User user = createValidUser();
        ShoppingCart shoppingCart = createValidShoppingCart(user);
        UpdateShoppingCartQuantityDto update = new UpdateShoppingCartQuantityDto(10);
        when(userRepository.findByEmail(CORRECT_TEST_EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateABookInTheCart(
                        CORRECT_TEST_EMAIL, INCORRECT_CART_ITEM_ID, update));
    }

    private User createValidUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("patryk@gmail.com");
        user.setPassword("strongpassword");
        user.setFirstName("Patryk");
        user.setLastName("Kowalski");
        user.setShippingAddress("Noniewicza12/12");
        Set<Role> roles = new HashSet<>();
        roles.add(createValidRole());
        user.setRoles(roles);
        return user;
    }

    private ShoppingCart createValidShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCart.setUser(user);
        return shoppingCart;
    }

    private CartItem createValidCartItem(ShoppingCart shoppingCart) {
        Book book = createValidBook();
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(10);
        cartItem.setShoppingCart(shoppingCart);
        return cartItem;
    }

    private Role createValidRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.USER);
        return role;
    }

    private Book createValidBook() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Andrzej Sapkowski");
        book.setIsbn("978-1-4919-4600-2");
        book.setPrice(new BigDecimal(12));
        book.setTitle("Wiedźmin");
        book.setDescription("A book about monster killer");
        book.setCategories(createValidCategorySet());
        return book;
    }

    private Set<Category> createValidCategorySet() {
        Category fantasy = new Category();
        fantasy.setName("FANTASY");
        fantasy.setId(1L);
        fantasy.setDescription("Fantasy books");
        fantasy.setDeleted(false);

        Category action = new Category();
        action.setName("ACTION");
        action.setId(2L);
        action.setDeleted(false);
        action.setDescription("Action books");

        Set<Category> categories = new HashSet<>();
        categories.add(fantasy);
        categories.add(action);

        return categories;
    }

    private AddBookRequestDto createValidAddBookRequestDto() {
        AddBookRequestDto addBookRequestDto = new AddBookRequestDto();
        addBookRequestDto.setBookId(1L);
        addBookRequestDto.setQuantity(10);
        return addBookRequestDto;
    }

    private ShoppingCartResponseDto createValidShoppingCartResponseDto(
            User user, CartItemDto cartItem) {
        ShoppingCartResponseDto shoppingCartResponseDto = new ShoppingCartResponseDto();
        shoppingCartResponseDto.setId(1L);
        shoppingCartResponseDto.setUserId(user.getId());
        shoppingCartResponseDto.getCartItems().add(cartItem);
        return shoppingCartResponseDto;
    }

    private CartItemDto createValidCartItemDto() {
        return new CartItemDto(
                1L, 1L, "Wiedźmin", 10
        );
    }

    private CartItemDto createUpdatedCartItemDto() {
        return new CartItemDto(
                1L, 1L, "Wiedźmin", 20
        );
    }
}
