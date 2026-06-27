package bookstore.repository.specification;

import bookstore.dto.CreateBookRequestDto;
import bookstore.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookBuilderFactory {
    public Specification<Book> create (CreateBookRequestDto createBookRequestDto) {
        return new SpecificationBuilder<Book>()
                .with(BookSpecification.getByAuthor(createBookRequestDto.getAuthor()))
                .with(BookSpecification.getByCoverImage(createBookRequestDto.getCoverImage()))
                .with(BookSpecification.getByDescription(createBookRequestDto.getDescription()))
                .with(BookSpecification.getByISBN(createBookRequestDto.getIsbn()))
                .with(BookSpecification.getByPrice(createBookRequestDto.getPrice()))
                .with(BookSpecification.getByTitle(createBookRequestDto.getTitle()))
                .build();
    }
}
