package bookstore.mapper;

import bookstore.dto.book.BookDto;
import bookstore.dto.book.CreateBookRequestDto;
import bookstore.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto createBookRequestDto);

    Book updateBook(@MappingTarget Book book, CreateBookRequestDto createBookRequestDto);
}
