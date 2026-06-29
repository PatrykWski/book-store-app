package bookstore.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchParametersDto {
    private String title;
    private String author;
    private BigDecimal price;
    private String isbn;
}
