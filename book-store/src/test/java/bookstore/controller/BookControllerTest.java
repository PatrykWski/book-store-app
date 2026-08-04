package bookstore.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookstore.dto.book.BookDto;
import bookstore.dto.book.BookSearchParametersDto;
import bookstore.dto.book.CreateBookRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.model.Category;
import bookstore.security.JwtUtil;
import bookstore.service.BookService;
import bookstore.util.RestResponsePage;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 99L;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAll_BooksExist_ReturnsPageOfBooks() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));
        BookDto bookDto = createValidBookDto();

        Page<BookDto> bookDtoPage = new PageImpl<>(List.of(bookDto), pageable, 1);

        when(bookService.findAll(pageable)).thenReturn(bookDtoPage);

        //when
        MvcResult result = mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String jsonResponse = result.getResponse().getContentAsString();
        RestResponsePage<BookDto> pageResponse = objectMapper.readValue(
                jsonResponse, objectMapper.getTypeFactory()
                        .constructParametricType(RestResponsePage.class, BookDto.class));

        Assertions.assertEquals(1, pageResponse.getContent().size());
        Assertions.assertEquals("Wiedźmin", pageResponse.getContent().get(0).getTitle());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAll_BooksDoNotExist_ReturnsEmptyPage() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));
        Page<BookDto> bookDtoPage = new PageImpl<>(List.of(), pageable, 0);
        when(bookService.findAll(pageable)).thenReturn(bookDtoPage);

        //when & then
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getBookById_BookExist_ReturnsABook() throws Exception {
        //given
        BookDto expected = createValidBookDto();
        when(bookService.findById(VALID_ID)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(get("/api/books/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String jsonResponse = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(jsonResponse, BookDto.class);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getBookById_BookDoesNotExist_ReturnsNotFound() throws Exception {
        //given
        when(bookService.findById(INVALID_ID)).thenThrow(
                new EntityNotFoundException("Book with id: " + INVALID_ID + " doesn't exist"));

        //when & then
        mockMvc.perform(get("/api/books/{id}", INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_ValidBookRequestDto_ReturnsABook() throws Exception {
        //given
        CreateBookRequestDto createBookRequestDto = createValidBookRequestDto();
        BookDto expected = createValidBookDto();
        when(bookService.save(createBookRequestDto)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        BookDto bookDto = objectMapper.readValue(json, BookDto.class);

        Assertions.assertEquals(expected, bookDto);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_InvalidBookRequestDto_ReturnsBadRequest() throws Exception {
        //given
        CreateBookRequestDto createBookRequestDto = createInvalidBookRequestDto();

        //when & then
        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_ValidBookRequestDto_ReturnsABook() throws Exception {
        //given
        BookDto expected = createValidBookDto();
        CreateBookRequestDto bookRequestDto = createValidBookRequestDto();
        when(bookService.updateBookById(VALID_ID, bookRequestDto)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(put("/api/books/{id}", VALID_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(json, BookDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_InvalidId_ReturnsNotFound() throws Exception {
        //given
        CreateBookRequestDto createBookRequestDto = createValidBookRequestDto();
        when(bookService.updateBookById(INVALID_ID, createBookRequestDto)).thenThrow(
                new EntityNotFoundException("Book with id: " + INVALID_ID + " doesn't exist"));

        //when & then
        mockMvc.perform(put("/api/books/{id}", INVALID_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_InvalidBookRequest_ReturnsBadRequest() throws Exception {
        //given
        CreateBookRequestDto createBookRequestDto = createInvalidBookRequestDto();

        //when & then
        mockMvc.perform(put("/api/books/{id}", VALID_ID)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(createBookRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_ValidId_ReturnsNoContent() throws Exception {
        //given & when & then
        mockMvc.perform(delete("/api/books/{id}", VALID_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_InvalidId_ReturnsNotFound() throws Exception {
        //given
        doThrow(new EntityNotFoundException("Book with id: " + INVALID_ID + " doesn't exist"))
                .when(bookService).deleteBookById(INVALID_ID);

        // when & then
        mockMvc.perform(delete("/api/books/{id}", INVALID_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void search_ValidSearchRequest_ReturnPage() throws Exception {
        //given
        BookSearchParametersDto searchParametersDto = new BookSearchParametersDto();
        searchParametersDto.setTitle("Wiedźmin");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));

        BookDto bookDto = new BookDto();
        bookDto.setTitle("Wiedźmin");
        Page<BookDto> expected = new PageImpl<>(List.of(bookDto), pageable, 1);

        when(bookService.search(searchParametersDto, pageable)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(get("/api/books/search")
                        .param("title", "Wiedźmin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        Page<BookDto> actual = objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructParametricType(RestResponsePage.class, BookDto.class));

        Assertions.assertEquals(expected.getTotalElements(), actual.getTotalElements());
        Assertions.assertEquals(expected.getContent().get(0).getTitle(),
                actual.getContent().get(0).getTitle());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void search_InvalidSearchRequest_ReturnBadRequest() throws Exception {
        //when & then
        mockMvc.perform(get("/api/books/search")
                        .param("minPrice", "-5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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

    private CreateBookRequestDto createInvalidBookRequestDto() {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setIsbn("978-1-4919-4600-2");
        createBookRequestDto.setPrice(new BigDecimal(12));
        createBookRequestDto.setDescription("A book about monster killer");
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
}
