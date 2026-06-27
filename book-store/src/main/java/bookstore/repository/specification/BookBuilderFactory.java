package bookstore.repository.specification;

import bookstore.dto.BookSearchParametersDto;
import bookstore.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookBuilderFactory {
    public Specification<Book> create(BookSearchParametersDto bookSearchParametersDto) {
        return new SpecificationBuilder<Book>()
                .with(BookSpecification.getByAuthor(bookSearchParametersDto.getAuthor()))
                .with(BookSpecification.getByCoverImage(bookSearchParametersDto.getCoverImage()))
                .with(BookSpecification.getByDescription(bookSearchParametersDto.getDescription()))
                .with(BookSpecification.getByIsbn(bookSearchParametersDto.getIsbn()))
                .with(BookSpecification.getByPrice(bookSearchParametersDto.getPrice()))
                .with(BookSpecification.getByTitle(bookSearchParametersDto.getTitle()))
                .build();
    }
}
