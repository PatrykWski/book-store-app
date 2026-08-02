package bookstore.controller;

import bookstore.dto.cartitem.AddBookRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import bookstore.dto.shoppingcart.UpdateShoppingCartQuantityDto;
import bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "End points for managing cart")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a book to a cart",
            description = "Adds a selected book and quantity to the authenticated user's cart")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ShoppingCartResponseDto addCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddBookRequestDto request) {
        return shoppingCartService.addABookToACart(
                userDetails.getUsername(),
                request
        );
    }

    @GetMapping
    @Operation(summary = "Show a cart", description = "Show users cart")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ShoppingCartResponseDto getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return shoppingCartService.showACart(userDetails.getUsername());
    }

    @DeleteMapping("/cart-items/{itemCartId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a book from the cart",
            description = "Delete a book from the cart")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ShoppingCartResponseDto deleteCartItem(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long itemCartId) {
        return shoppingCartService.deleteABookFromTheCart(userDetails.getUsername(), itemCartId);
    }

    @PutMapping("/cart-item/{cartItemId}")
    @Operation(summary = "Update a book", description = "Update a book in the cart")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ShoppingCartResponseDto updateCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateShoppingCartQuantityDto updateShoppingCartQuantityDto) {
        return shoppingCartService.updateABookInTheCart(
                userDetails.getUsername(), cartItemId, updateShoppingCartQuantityDto);
    }
}
