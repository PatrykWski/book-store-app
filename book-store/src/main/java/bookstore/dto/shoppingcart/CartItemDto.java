package bookstore.dto.shoppingcart;

import java.util.Objects;

public record CartItemDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CartItemDto that = (CartItemDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
