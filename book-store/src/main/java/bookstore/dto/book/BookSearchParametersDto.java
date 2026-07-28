package bookstore.dto.book;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BookSearchParametersDto that = (BookSearchParametersDto) o;
        return Objects.equals(title, that.title)
                && Objects.equals(author, that.author)
                && Objects.equals(minPrice, that.minPrice)
                && Objects.equals(maxPrice, that.maxPrice)
                && Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, minPrice, maxPrice, isbn);
    }
}
