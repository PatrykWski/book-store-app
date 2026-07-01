package bookstore.service;

import bookstore.dto.BookDto;
import bookstore.dto.BookSearchParametersDto;
import bookstore.dto.CreateBookRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.BookMapper;
import bookstore.model.Book;
import bookstore.repository.BookRepository;
import bookstore.repository.specification.BookSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toModel(createBookRequestDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public BookDto findById(Long id) {
        Book book = getBookById(id);
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateBookById(Long id, CreateBookRequestDto createBookRequestDto) {
        Book book = getBookById(id);
        Book updatedBook = bookMapper.updateBook(book, createBookRequestDto);
        bookRepository.save(updatedBook);
        return bookMapper.toDto(updatedBook);
    }

    @Override
    public void deleteBookById(Long id) {
        Book book = getBookById(id);
        bookRepository.deleteById(id);
    }

    private Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Couldn't find book with id: " + id));
    }

    @Override
    public Page<BookDto> search(BookSearchParametersDto bookSearchParametersDto,
                                Pageable pageable) {
        Specification<Book> booksSpecifications = bookSpecificationBuilder
                .create(bookSearchParametersDto);
        return bookRepository.findAll(booksSpecifications, pageable)
                .map(bookMapper::toDto);
    }
}
