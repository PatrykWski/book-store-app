package bookstore.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 99L;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    public void save_ValidCreateBookRequestDto_ReturnsBookDto() {
        //given
        CreateBookRequestDto createBookRequestDto = createValidBookRequestDto();
        Book book = createValidBook();
        BookDto expected = createValidBookDto();

        when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        when(categoryRepository.findAllById(createBookRequestDto.getCategoryIds()))
                .thenReturn(new ArrayList<>(createValidCategorySet()));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        //when

        BookDto actual = bookService.save(createBookRequestDto);

        //then

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void save_InvalidCreateBookRequestDto_ThrowsEntityNotFoundException() {
        //given
        CreateBookRequestDto createBookRequestDto = createValidBookRequestDto();
        Book book = createValidBook();

        when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        when(categoryRepository.findAllById(createBookRequestDto.getCategoryIds()))
                .thenReturn(new ArrayList<>());

        //when & then

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.save(createBookRequestDto));
    }

    @Test
    public void findById_BookExists_ReturnsBookResponse() {
        //given
        Book book = createValidBook();

        BookDto expected = createValidBookDto();

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);
        //when

        BookDto actual = bookService.findById(book.getId());

        //then

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void findById_BookDoesNotExist_ThrowsEntityNotFoundException() {
        //given

        Long nonExistingId = VALID_ID;

        when(bookRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //when & then

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(nonExistingId));
    }

    @Test
    public void findAll_BooksExist_ReturnPageOfBookDto() {
        //given
        Pageable pageable = Pageable.ofSize(20);
        Book singleBook = createValidBook();
        BookDto singleBookDto = createValidBookDto();

        Page<Book> pages = new PageImpl<>(List.of(singleBook));
        Page<BookDto> expected = new PageImpl<>(List.of(singleBookDto));
        when(bookRepository.findAll(pageable)).thenReturn(pages);
        when(bookMapper.toDto(singleBook)).thenReturn(singleBookDto);

        //when
        Page<BookDto> actual = bookService.findAll(pageable);

        //then

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void updateBookById_ValidCreateBookRequestDto_ReturnBookDto() {
        //given
        Book book = createValidBook();
        CreateBookRequestDto createBookRequestDto = createValidBookRequestDto();
        Book updatedBook = createValidBook();
        Set<Category> categories = createValidCategorySet();
        updatedBook.setCategories(categories);
        BookDto expected = createValidBookDto();

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.updateBook(book, createBookRequestDto)).thenReturn(updatedBook);
        when(categoryRepository.findAllById(createValidLongSet())).thenReturn(
                new ArrayList<>(categories));
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(expected);

        //when

        BookDto actual = bookService.updateBookById(book.getId(), createBookRequestDto);

        //then

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void updateBookById_BookDoesNotExist_ThrowEntityNotFoundException() {
        //given
        CreateBookRequestDto createBookRequestDto = createValidBookRequestDto();
        when(bookRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        //when & then

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBookById(VALID_ID, createBookRequestDto));
    }

    @Test
    public void updateBookById_CategoryDoesNotExist_ThrowEntityNotFoundException() {
        //given
        Book book = createValidBook();
        CreateBookRequestDto createBookRequestDto = createValidBookRequestDto();
        Book updatedBook = createValidBook();
        updatedBook.setCategories(createValidCategorySet());

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.updateBook(book, createBookRequestDto)).thenReturn(updatedBook);
        when(categoryRepository.findAllById(createBookRequestDto.getCategoryIds()))
                .thenReturn(new ArrayList<>());

        //when & then

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBookById(VALID_ID, createBookRequestDto));
    }

    @Test
    public void deleteBookById_ValidId_DeletesSuccessfully() {
        //given
        when(bookRepository.existsById(VALID_ID)).thenReturn(true);

        //when
        bookService.deleteBookById(VALID_ID);

        //then
        verify(bookRepository, times(1)).existsById(VALID_ID);
        verify(bookRepository, times(1)).deleteById(VALID_ID);
    }

    @Test
    public void deleteBookById_InvalidId_ReturnsNotFound() {
        //given
        when(bookRepository.existsById(INVALID_ID)).thenReturn(false);

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteBookById(INVALID_ID));

        verify(bookRepository, times(1)).existsById(INVALID_ID);
        verify(bookRepository, never()).deleteById(INVALID_ID);
    }

    @Test
    public void search_ValidRequestParamsDto_ReturnsPage() {
        //given
        BookSearchParametersDto bookSearchParametersDto = createValidSearchParametersDto();
        Pageable pageable = Pageable.ofSize(20);
        Book book = new Book();
        Page<Book> page = new PageImpl<>(List.of(book));
        BookDto bookDto = createValidBookDto();
        Specification<Book> spec = mock(Specification.class);

        when(bookSpecificationBuilder.create(bookSearchParametersDto)).thenReturn(spec);
        when(bookRepository.findAll(spec, pageable)).thenReturn(page);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        //when
        Page<BookDto> result = bookService.search(bookSearchParametersDto, pageable);

        //then
        Assertions.assertEquals(1, result.getContent().size());
        verify(bookSpecificationBuilder).create(bookSearchParametersDto);
        verify(bookRepository).findAll(spec,pageable);
    }

    @Test
    public void search_EmptyRequestParamsDto_ReturnsPage() {
        //given
        BookSearchParametersDto bookSearchParametersDto = new BookSearchParametersDto();
        Pageable pageable = Pageable.ofSize(20);
        Book book = new Book();
        Page<Book> page = new PageImpl<>(List.of());
        Specification<Book> spec = mock(Specification.class);

        when(bookSpecificationBuilder.create(bookSearchParametersDto)).thenReturn(spec);
        when(bookRepository.findAll(spec, pageable)).thenReturn(page);

        //when
        Page<BookDto> result = bookService.search(bookSearchParametersDto, pageable);

        //then
        Assertions.assertEquals(0, result.getContent().size());
        verify(bookSpecificationBuilder).create(bookSearchParametersDto);
        verify(bookRepository).findAll(spec,pageable);
    }

    @Test
    public void search_PartialRequestParamsDto_ReturnsPage() {
        //given
        BookSearchParametersDto bookSearchParametersDto = new BookSearchParametersDto();
        bookSearchParametersDto.setAuthor("Andrzej Sapkowski");
        Book book = new Book();
        book.setAuthor("Andrzej Sapkowski");
        Page<Book> page = new PageImpl<>(List.of(book));
        Specification<Book> spec = mock(Specification.class);
        BookDto bookDto = new BookDto();
        bookDto.setAuthor("Andrzej Sapkowski");
        Pageable pageable = Pageable.ofSize(20);

        when(bookSpecificationBuilder.create(bookSearchParametersDto)).thenReturn(spec);
        when(bookRepository.findAll(spec, pageable)).thenReturn(page);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        //when
        Page<BookDto> result = bookService.search(bookSearchParametersDto, pageable);

        //then
        Assertions.assertEquals(1, result.getContent().size());
        verify(bookSpecificationBuilder).create(bookSearchParametersDto);
        verify(bookRepository).findAll(spec,pageable);
    }

    private Book createValidBook() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Andrzej Sapkowski");
        book.setIsbn("978-1-4919-4600-2");
        book.setPrice(new BigDecimal(12));
        book.setTitle("Wiedźmin");
        book.setDescription("A book about monster killer");
        book.setCategories(createValidCategorySet());
        return book;
    }

    private BookSearchParametersDto createValidSearchParametersDto() {
        BookSearchParametersDto book = new BookSearchParametersDto();
        book.setAuthor("Andrzej Sapkowski");
        book.setIsbn("978-1-4919-4600-2");
        book.setTitle("Wiedźmin");
        book.setMinPrice(new BigDecimal(10));
        book.setMaxPrice(new BigDecimal(50));
        return book;
    }

    private BookDto createValidBookDto() {
        BookDto book = new BookDto();
        book.setId(1L);
        book.setAuthor("Andrzej Sapkowski");
        book.setIsbn("978-1-4919-4600-2");
        book.setPrice(new BigDecimal(12));
        book.setTitle("Wiedźmin");
        book.setDescription("A book about monster killer");
        book.setCategoryIds(createValidLongSet());
        return book;
    }

    private CreateBookRequestDto createValidBookRequestDto() {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setAuthor("Andrzej Sapkowski");
        createBookRequestDto.setIsbn("978-1-4919-4600-2");
        createBookRequestDto.setPrice(new BigDecimal(12));
        createBookRequestDto.setTitle("Wiedźmin");
        createBookRequestDto.setDescription("A book about monster killer");
        createBookRequestDto.setCategoryIds(createValidLongSet());
        return createBookRequestDto;
    }

    private Set<Long> createValidLongSet() {
        Category fantasy = new Category();
        fantasy.setName("FANTASY");
        fantasy.setId(1L);
        fantasy.setDescription("Fantasy books");
        fantasy.setDeleted(false);

        Category action = new Category();
        action.setName("ACTION");
        action.setId(2L);
        action.setDeleted(false);
        action.setDescription("Action books");

        Set<Long> categories = new HashSet<>();
        categories.add(fantasy.getId());
        categories.add(action.getId());

        return categories;
    }

    private Set<Category> createValidCategorySet() {
        Category fantasy = new Category();
        fantasy.setName("FANTASY");
        fantasy.setId(1L);
        fantasy.setDescription("Fantasy books");
        fantasy.setDeleted(false);

        Category action = new Category();
        action.setName("ACTION");
        action.setId(2L);
        action.setDeleted(false);
        action.setDescription("Action books");

        Set<Category> categories = new HashSet<>();
        categories.add(fantasy);
        categories.add(action);

        return categories;
    }
}
