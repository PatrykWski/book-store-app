package bookstore.dto.book;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private String isbn;
    private String description;
    private String coverImage;
    private Set<Long> categoryIds;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BookDto bookDto = (BookDto) o;
        return Objects.equals(id, bookDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
