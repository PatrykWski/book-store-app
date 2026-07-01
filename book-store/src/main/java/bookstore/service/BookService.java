package bookstore.service;

import bookstore.dto.BookDto;
import bookstore.dto.BookSearchParametersDto;
import bookstore.dto.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookDto save(CreateBookRequestDto createBookRequestDto);

    Page<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    BookDto updateBookById(Long id, CreateBookRequestDto createBookRequestDto);

    void deleteBookById(Long id);

    Page<BookDto> search(BookSearchParametersDto bookSearchParametersDto, Pageable pageable);
}
