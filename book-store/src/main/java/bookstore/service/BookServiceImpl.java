package bookstore.service;

import bookstore.dto.book.BookDto;
import bookstore.dto.book.BookSearchParametersDto;
import bookstore.dto.book.CreateBookRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.BookMapper;
import bookstore.model.Book;
import bookstore.model.Category;
import bookstore.repository.BookRepository;
import bookstore.repository.CategoryRepository;
import bookstore.repository.specification.BookSpecificationBuilder;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final BookSpecificationBuilder bookSpecificationBuilder;

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public BookDto save(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toModel(createBookRequestDto);

        Set<Category> categories = getCategories(createBookRequestDto);

        book.setCategories(categories);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public BookDto findById(Long id) {
        Book book = getBookById(id);
        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public BookDto updateBookById(Long id, CreateBookRequestDto createBookRequestDto) {
        Book book = getBookById(id);

        Book updatedBook = bookMapper.updateBook(book, createBookRequestDto);

        Set<Category> categories = getCategories(createBookRequestDto);

        updatedBook.setCategories(categories);

        bookRepository.save(updatedBook);
        return bookMapper.toDto(updatedBook);
    }

    @Transactional
    @Override
    public void deleteBookById(Long id) {
        Book book = getBookById(id);
        bookRepository.deleteById(id);
    }

    private Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Couldn't find book with id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BookDto> search(BookSearchParametersDto bookSearchParametersDto,
                                Pageable pageable) {
        Specification<Book> booksSpecifications = bookSpecificationBuilder
                .create(bookSearchParametersDto);
        return bookRepository.findAll(booksSpecifications, pageable)
                .map(bookMapper::toDto);
    }

    private Set<Category> getCategories(CreateBookRequestDto createBookRequestDto) {
        Set<Category> categories = new HashSet<>(categoryRepository
                .findAllById(createBookRequestDto.getCategoryIds()));

        if (categories.size() != createBookRequestDto.getCategoryIds().size()) {
            throw new EntityNotFoundException("One or more categories were not found.");
        }

        return categories;
    }
}
