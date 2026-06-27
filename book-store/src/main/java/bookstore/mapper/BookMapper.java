package bookstore.mapper;

import bookstore.dto.BookDto;
import bookstore.dto.CreateBookRequestDto;
import bookstore.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto createBookRequestDto);

    Book updateBook(@MappingTarget Book book, CreateBookRequestDto createBookRequestDto);
}
