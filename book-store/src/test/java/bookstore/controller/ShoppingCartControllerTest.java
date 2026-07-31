package bookstore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookstore.dto.cartitem.AddBookRequestDto;
import bookstore.dto.shoppingcart.CartItemDto;
import bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import bookstore.dto.shoppingcart.UpdateShoppingCartQuantityDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.model.Role;
import bookstore.model.RoleName;
import bookstore.model.User;
import bookstore.security.JwtUtil;
import bookstore.service.CustomUserDetailService;
import bookstore.service.ShoppingCartService;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ShoppingCartController.class)
public class ShoppingCartControllerTest {
    private static final String VALID_USER_EMAIL = "patryk@gmail.com";
    private static final Long VALID_CART_ID = 1L;
    private static final Long INVALID_CART_ID = 2L;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ShoppingCartService shoppingCartService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @Test
    @WithMockUser(username = "patryk@gmail.com", roles = "USER")
    public void addCartItem_ValidRequest_ReturnsShoppingCartResponseDto() throws Exception {
        //given
        User user = createValidUser();
        CartItemDto cartItemDto = createValidCartItemDto();
        AddBookRequestDto addBookRequestDto = createValidAddBookRequestDto();
        ShoppingCartResponseDto expected = createValidShoppingCartResponseDto(
                user, cartItemDto);
        when(shoppingCartService.addABookToACart(eq(user.getEmail()),
                any(AddBookRequestDto.class)))
                .thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBookRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        ShoppingCartResponseDto actual = objectMapper
                .readValue(json, ShoppingCartResponseDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void addCartItem_InvalidRequest_ReturnsBadRequest() throws Exception {
        //given
        AddBookRequestDto addBookRequestDto = new AddBookRequestDto();
        addBookRequestDto.setQuantity(-5);
        addBookRequestDto.setBookId(null);

        //when & then
        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(new TestingAuthenticationToken(
                                VALID_USER_EMAIL, null, "ROLE_USER")))
                        .content(objectMapper.writeValueAsString(addBookRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getCart_ByValidEmail_ReturnsShoppingCartDto() throws Exception {
        //given
        User user = createValidUser();
        CartItemDto cartItemDto = createValidCartItemDto();
        ShoppingCartResponseDto expected = createValidShoppingCartResponseDto(user, cartItemDto);

        when(shoppingCartService.showACart(nullable(String.class)))
                .thenReturn(expected);

        //when & then
        MvcResult result = mockMvc.perform(get("/api/cart")
                        .with(authentication(new TestingAuthenticationToken(
                                VALID_USER_EMAIL, null, "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        ShoppingCartResponseDto actual = objectMapper
                .readValue(json, ShoppingCartResponseDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deleteCartItem_ValidCartItemId_ReturnsShoppingCartResponseDto() throws Exception {
        //given
        User user = createValidUser();
        CartItemDto cartItemDto = createValidCartItemDto();
        ShoppingCartResponseDto expected = createValidShoppingCartResponseDto(
                user, cartItemDto);
        when(shoppingCartService.deleteABookFromTheCart(nullable(String.class), eq(VALID_CART_ID)))
                .thenReturn(expected);

        //when & then
        MvcResult result = mockMvc.perform(delete("/api/cart/cart-items/{itemCartId}",
                        VALID_CART_ID)
                        .with(authentication(new TestingAuthenticationToken(
                                VALID_USER_EMAIL, null, "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ShoppingCartResponseDto actual = objectMapper
                .readValue(json, ShoppingCartResponseDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deleteCartItem_InvalidCartItemId_ReturnsNotFound() throws Exception {
        //given
        when(shoppingCartService.deleteABookFromTheCart(any(), eq(INVALID_CART_ID)))
                .thenThrow(new EntityNotFoundException("Cart item not found"));

        // when & then
        mockMvc.perform(delete("/api/cart/cart-items/{cartItemId}", INVALID_CART_ID)
                        .with(authentication(new TestingAuthenticationToken(
                                VALID_USER_EMAIL, null, "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateCartItem_ValidRequest_ReturnsShoppingCartResponseDto() throws Exception {
        //given
        User user = createValidUser();
        CartItemDto cartItemDto = createValidCartItemDto();
        UpdateShoppingCartQuantityDto quantity = new UpdateShoppingCartQuantityDto(10);
        ShoppingCartResponseDto expected = createValidShoppingCartResponseDto(
                user, cartItemDto);

        when(shoppingCartService
                .updateABookInTheCart(nullable(String.class), eq(VALID_CART_ID),
                        eq(quantity)))
                .thenReturn(expected);

        //when & then
        MvcResult result = mockMvc.perform(put("/api/cart/cart-item/{itemCartId}", VALID_CART_ID)
                        .with(authentication(new TestingAuthenticationToken(
                                VALID_USER_EMAIL, null, "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quantity)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ShoppingCartResponseDto actual = objectMapper
                .readValue(json, ShoppingCartResponseDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void updateCartItem_InvalidCartId_ReturnsNotFound() throws Exception {
        //given
        UpdateShoppingCartQuantityDto quantity = new UpdateShoppingCartQuantityDto(10);

        when(shoppingCartService.updateABookInTheCart(
                any(), eq(INVALID_CART_ID), eq(quantity)))
                .thenThrow(new EntityNotFoundException(
                        "Cart with id: " + INVALID_CART_ID + " does not exist"));
        //when & then
        mockMvc.perform(put("/api/cart/cart-item/{cartItemId}", INVALID_CART_ID)
                        .with(authentication(new TestingAuthenticationToken(
                                VALID_USER_EMAIL, null, "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quantity)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateCartItem_InvalidRequest_ReturnsBadRequest() throws Exception {
        //given
        UpdateShoppingCartQuantityDto quantity = new UpdateShoppingCartQuantityDto(-5);

        //when & then
        mockMvc.perform(put("/api/cart/cart-item/{cartItemId}", VALID_CART_ID)
                        .with(authentication(new TestingAuthenticationToken(
                                VALID_USER_EMAIL, null, "ROLE_USER")))
                        .content(objectMapper.writeValueAsString(quantity))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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

    private Role createValidRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.USER);
        return role;
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
        shoppingCartResponseDto.setCartItems(new HashSet<>());
        shoppingCartResponseDto.getCartItems().add(cartItem);
        return shoppingCartResponseDto;
    }

    private CartItemDto createValidCartItemDto() {
        return new CartItemDto(
                1L, 1L, "Wiedźmin", 10
        );
    }
}
