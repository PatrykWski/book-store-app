package bookstore.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchParametersDto {
    private String title;
    private String author;
    private BigDecimal min;
    private BigDecimal max;
    private String isbn;
}
