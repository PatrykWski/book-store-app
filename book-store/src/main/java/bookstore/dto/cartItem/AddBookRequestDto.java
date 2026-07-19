package bookstore.dto.cartItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddBookRequestDto {
    @NotBlank
    private Long bookId;
    @NotBlank
    @Size(min = 1)
    private int quantity;
}
