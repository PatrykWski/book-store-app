package bookstore.controller;

import bookstore.dto.cartItem.AddBookRequestDto;
import bookstore.dto.shoppingCart.ShoppingCartResponseDto;
import bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
            description = "Add a book to the cart or make a new one if doesn't exist")
    public ShoppingCartResponseDto addABookToACart(@Valid @RequestBody AddBookRequestDto addBookRequestDto) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return shoppingCartService.addABookToACart(userEmail, addBookRequestDto);
    }

    @GetMapping
    @Operation(summary = "Show a cart", description = "Show users cart")
    public ShoppingCartResponseDto showACart() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return shoppingCartService.showACart(userEmail);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book from the cart",
            description = "Delete a book from the cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ShoppingCartResponseDto deleteABook(@PathVariable Long id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return shoppingCartService.deleteABookFromTheCart(userEmail, id);
    }
}
