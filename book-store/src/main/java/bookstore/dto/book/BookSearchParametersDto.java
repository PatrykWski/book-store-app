package bookstore.dto.book;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchParametersDto {
    private String title;
    private String author;
    @PositiveOrZero
    private BigDecimal minPrice;
    @Positive
    private BigDecimal maxPrice;
    private String isbn;
}
