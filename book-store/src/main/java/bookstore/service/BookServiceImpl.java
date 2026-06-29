package bookstore.service;

import bookstore.dto.BookDto;
import bookstore.dto.CreateBookRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.BookMapper;
import bookstore.model.Book;
import bookstore.repository.BookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toModel(createBookRequestDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = checkIfBookExist(id);
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateBookById(Long id, CreateBookRequestDto createBookRequestDto) {
        Book book = checkIfBookExist(id);
        Book updatedBook = bookMapper.updateBook(book, createBookRequestDto);
        bookRepository.save(updatedBook);
        return bookMapper.toDto(updatedBook);
    }

    @Override
    public void deleteBookById(Long id) {
        Book book = checkIfBookExist(id);
        bookRepository.deleteById(id);
    }

    private Book checkIfBookExist(Long id) {
        return bookRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Couldn't find book with id: " + id));
    }
}
