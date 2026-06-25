package bookstore.dto;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CreateBookRequestDto {
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
}
