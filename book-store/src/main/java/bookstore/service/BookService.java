package bookstore.service;

import bookstore.dto.BookDto;
import bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {

    BookDto save(CreateBookRequestDto createBookRequestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto updateBookById(Long id, CreateBookRequestDto createBookRequestDto);

    void deleteBookById(Long id);
}
