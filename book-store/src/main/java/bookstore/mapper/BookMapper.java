package bookstore.mapper;

import bookstore.dto.BookDto;
import bookstore.dto.CreateBookRequestDto;
import bookstore.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto createBookRequestDto);
}
