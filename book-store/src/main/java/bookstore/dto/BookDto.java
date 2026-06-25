package bookstore.dto;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private String description;
    private String coverImage;
}
