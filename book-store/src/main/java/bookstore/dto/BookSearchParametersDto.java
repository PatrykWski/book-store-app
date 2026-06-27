package bookstore.dto;

import java.math.BigDecimal;

public record BookSearchParametersDto(String title, String author, BigDecimal price,
                                      String isbn, String description, String coverImage) {
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImage() {
        return coverImage;
    }
}
