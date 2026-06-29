package bookstore.repository.specification;

import bookstore.dto.BookSearchParametersDto;
import bookstore.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookSpecificationBuilder {
    public Specification<Book> create(BookSearchParametersDto bookSearchParametersDto) {
        return new SpecificationBuilder<Book>()
                .with(BookSpecification.getByAuthor(bookSearchParametersDto.getAuthor()))
                .with(BookSpecification.getByIsbn(bookSearchParametersDto.getIsbn()))
                .with(BookSpecification.getByPrice(bookSearchParametersDto.getMin(),
                        bookSearchParametersDto.getMax()))
                .with(BookSpecification.getByTitle(bookSearchParametersDto.getTitle()))
                .build();
    }
}
