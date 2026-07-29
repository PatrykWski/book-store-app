package bookstore.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.dto.book.BookDtoWithoutCategoryIds;
import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CategoryRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.BookMapper;
import bookstore.mapper.CategoryMapper;
import bookstore.model.Book;
import bookstore.model.Category;
import bookstore.repository.BookRepository;
import bookstore.repository.CategoryRepository;
import java.math.BigDecimal;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 2L;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void findAll_CategoriesExist_ReturnPage() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Category category = createValidCategory();
        CategoryDto categoryDto = createValidCategoryDto();
        Page<Category> categories = new PageImpl<>(List.of(category), pageable, 1);
        Page<CategoryDto> expected = new PageImpl<>(List.of(categoryDto), pageable, 1);

        when(categoryRepository.findAll(pageable)).thenReturn(categories);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //when
        Page<CategoryDto> actual = categoryService.findAll(pageable);

        //then
        Assertions.assertEquals(expected.getContent().get(0).getId(),
                actual.getContent().get(0).getId());
        Assertions.assertEquals(expected.getContent().size(), actual.getContent().size());
        Assertions.assertEquals(expected.getTotalElements(), actual.getTotalElements());
    }

    @Test
    public void findAll_CategoriesDoesNotExist_ReturnEmptyPage() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(List.of(), pageable, 0);
        Page<CategoryDto> expected = new PageImpl<>(List.of(), pageable, 0);

        when(categoryRepository.findAll(pageable)).thenReturn(page);

        //when
        Page<CategoryDto> actual = categoryService.findAll(pageable);

        //then
        Assertions.assertEquals(expected.getTotalElements(), actual.getTotalElements());
        Assertions.assertTrue(actual.getContent().isEmpty());
    }

    @Test
    public void getById_ValidId_ReturnCategoryDto() {
        //given
        Category category = createValidCategory();
        CategoryDto expected = createValidCategoryDto();

        when(categoryRepository.findById(VALID_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.getById(VALID_ID);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getById_InvalidId_ReturnEntityNotFoundException() {
        //when
        when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(INVALID_ID));
    }

    @Test
    public void save_ValidRequestDto_ReturnCategoryDto() {
        //given
        Category category = createValidCategory();
        CategoryDto expected = createValidCategoryDto();
        CategoryRequestDto categoryRequestDto = createValidCategoryRequestDto();

        when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.save(categoryRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void update_ValidRequestDto_ReturnCategoryDto() {
        //given
        Category category = createValidCategory();
        CategoryDto expected = createValidCategoryDto();
        CategoryRequestDto categoryRequestDto = createValidCategoryRequestDto();

        when(categoryRepository.findById(VALID_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.update(VALID_ID, categoryRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void update_InvalidRequestDto_ThrowEntityNotFoundException() {
        //given
        CategoryRequestDto categoryRequestDto = createValidCategoryRequestDto();
        //when
        when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(INVALID_ID, categoryRequestDto));
    }

    @Test
    public void deleteById_ValidId_SuccessfullyDeleted() {
        //given
        Category category = createValidCategory();
        category.setDeleted(false);

        when(categoryRepository.findById(VALID_ID)).thenReturn(Optional.of(category));

        //when
        categoryService.deleteById(VALID_ID);

        //then
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    public void deleteById_InvalidId_NotDeleted() {
        //when
        when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(INVALID_ID));
    }

    @Test
    public void getBooksByCategoryId_ValidId_ReturnsPage() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Book book = createValidBook();
        BookDtoWithoutCategoryIds bookDto = createValidBookDto();
        Page<Book> books = new PageImpl<>(List.of(book), pageable, 1);

        when(categoryRepository.existsById(VALID_ID)).thenReturn(true);
        when(bookRepository.findAllByCategoryId(VALID_ID, pageable)).thenReturn(books);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDto);

        Page<BookDtoWithoutCategoryIds> expected = new PageImpl<>(List.of(bookDto));
        //when
        Page<BookDtoWithoutCategoryIds> actual = categoryService
                .getBooksByCategoryId(VALID_ID, pageable);

        //then
        Assertions.assertEquals(expected.getContent().get(0).getId(),
                actual.getContent().get(0).getId());
        Assertions.assertEquals(expected.getTotalElements(), actual.getTotalElements());
        Assertions.assertEquals(expected.getContent().size(), actual.getContent().size());
    }

    @Test
    public void getBooksByCategoryId_InvalidId_ThrowsEntityNotFound() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.existsById(INVALID_ID)).thenReturn(false);

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getBooksByCategoryId(INVALID_ID, pageable));
    }

    private Category createValidCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("ACTION");
        category.setDeleted(false);
        category.setDescription("Action books");
        return category;
    }

    private BookDtoWithoutCategoryIds createValidBookDto() {
        BookDtoWithoutCategoryIds book = new BookDtoWithoutCategoryIds();
        book.setId(1L);
        book.setAuthor("Andrzej Sapkowski");
        book.setIsbn("978-1-4919-4600-2");
        book.setPrice(new BigDecimal(12));
        book.setTitle("Wiedźmin");
        book.setDescription("A book about monster killer");
        book.setCoverImage("");
        return book;
    }

    private CategoryDto createValidCategoryDto() {
        CategoryDto category = new CategoryDto();
        category.setId(1L);
        category.setName("ACTION");
        category.setDescription("Action books");
        return category;
    }

    private CategoryRequestDto createValidCategoryRequestDto() {
        CategoryRequestDto category = new CategoryRequestDto();
        category.setName("ACTION");
        category.setDescription("Action books");
        return category;
    }

    private Book createValidBook() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Andrzej Sapkowski");
        book.setPrice(new BigDecimal(15));
        book.setIsbn("978-1-4919-4600-2");
        book.setDeleted(false);
        book.setDescription("A book about monster killer");
        book.setTitle("Wiedźmin");
        book.setCoverImage("");
        book.setCategories(createValidCategorySet());
        return book;
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
