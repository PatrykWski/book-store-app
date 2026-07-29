package bookstore.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddBookRequestDto {
    @NotNull
    private Long bookId;
    @Positive
    private int quantity;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddBookRequestDto that = (AddBookRequestDto) o;
        return Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bookId);
    }
}
