package bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchParametersDto {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @PositiveOrZero
    private BigDecimal minPrice;
    @Positive
    private BigDecimal maxPrice;
    @NotBlank
    private String isbn;
}
