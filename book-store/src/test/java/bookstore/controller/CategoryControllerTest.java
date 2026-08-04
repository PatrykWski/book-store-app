package bookstore.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookstore.dto.book.BookDtoWithoutCategoryIds;
import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CategoryRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.security.JwtUtil;
import bookstore.service.CategoryService;
import bookstore.util.RestResponsePage;
import java.math.BigDecimal;
import java.util.List;
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

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 2L;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void createCategory_ValidCategoryRequestDto_ReturnsCategoryDto() throws Exception {
        //given
        CategoryRequestDto categoryRequestDto = createValidCategoryRequestDto();
        CategoryDto expected = createValidCategoryDto();
        when(categoryService.save(categoryRequestDto)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(json, CategoryDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void createCategory_InvalidCategoryRequestDto_ReturnsBadRequest() throws Exception {
        //given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        //when & then
        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getAll_CategoriesExist_ReturnsPage() throws Exception {
        //given
        CategoryDto categoryDto = createValidCategoryDto();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Page<CategoryDto> expected = new PageImpl<>(List.of(categoryDto));
        when(categoryService.findAll(pageable)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        Page<CategoryDto> actual = objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructParametricType(RestResponsePage.class, CategoryDto.class));

        Assertions.assertEquals(expected.getContent().get(0).getId(),
                actual.getContent().get(0).getId());
        Assertions.assertEquals(expected.getTotalElements(), actual.getTotalElements());
        Assertions.assertEquals(expected.getContent().size(), actual.getContent().size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getAll_CategoriesDoesNotExist_ReturnsEmptyPage() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Page<CategoryDto> expected = new PageImpl<>(List.of());
        when(categoryService.findAll(pageable)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        Page<CategoryDto> actual = objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructParametricType(RestResponsePage.class, CategoryDto.class));

        Assertions.assertEquals(expected.getContent().size(), actual.getContent().size());
        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getCategoryById_ValidId_ReturnsCategoryDto() throws Exception {
        //given
        CategoryDto expected = createValidCategoryDto();
        when(categoryService.getById(VALID_ID)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(get("/api/categories/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(json, CategoryDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getCategoryById_InvalidId_ReturnsNotFound() throws Exception {
        //given
        when(categoryService.getById(INVALID_ID)).thenThrow(new EntityNotFoundException(
                "Category with id: " + INVALID_ID + " doesn't exist"));

        //when & then
        mockMvc.perform(get("/api/categories/{id}", INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void updateCategory_ValidRequestDto_ReturnsCategoryDto() throws Exception {
        //given
        CategoryRequestDto categoryRequestDto = createValidCategoryRequestDto();
        CategoryDto expected = createValidCategoryDto();
        when(categoryService.update(VALID_ID, categoryRequestDto)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(put("/api/categories/{id}", VALID_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(json, CategoryDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void updateCategory_InvalidId_ReturnsNotFound() throws Exception {
        //given
        CategoryRequestDto categoryRequestDto = createValidCategoryRequestDto();
        when(categoryService.update(INVALID_ID, categoryRequestDto))
                .thenThrow(new EntityNotFoundException(
                "Category with id: " + INVALID_ID + " doesn't exist"));

        //when & then
        mockMvc.perform(put("/api/categories/{id}", INVALID_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void deleteCategory_ValidId_ReturnsNoContent() throws Exception {
        //given & when & then
        mockMvc.perform(delete("/api/categories/{id}", VALID_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void deleteCategory_InvalidId_ReturnsNotFound() throws Exception {
        //given
        doThrow(new EntityNotFoundException("Category with id: " + INVALID_ID + " doesn't exist"))
                .when(categoryService).deleteById(INVALID_ID);

        //when & then
        mockMvc.perform(delete("/api/categories/{id}", INVALID_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getBooksByCategoryId_ValidId_ReturnsPage() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));
        BookDtoWithoutCategoryIds bookDtoWithoutId = createValidBookWithoutCategoryIdDto();
        Page<BookDtoWithoutCategoryIds> expected = new PageImpl<>(
                List.of(bookDtoWithoutId), pageable, 1);

        when(categoryService.getBooksByCategoryId(VALID_ID, pageable)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(get("/api/categories/{id}/books", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        Page<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(json, objectMapper
                .getTypeFactory().constructParametricType(
                        RestResponsePage.class,
                        BookDtoWithoutCategoryIds.class));

        Assertions.assertEquals(expected.getContent().get(0).getId(),
                actual.getContent().get(0).getId());
        Assertions.assertEquals(expected.getTotalElements(), actual.getTotalElements());
        Assertions.assertEquals(expected.getContent().size(), actual.getContent().size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getBooksByCategoryId_InvalidId_ReturnsEmptyPage() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));
        Page<BookDtoWithoutCategoryIds> expected = new PageImpl<>(List.of(), pageable, 0);

        when(categoryService.getBooksByCategoryId(INVALID_ID, pageable)).thenReturn(expected);

        //when
        MvcResult result = mockMvc.perform(get("/api/categories/{id}/books", INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String json = result.getResponse().getContentAsString();
        Page<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(json, objectMapper
                .getTypeFactory().constructParametricType(
                        RestResponsePage.class,
                        BookDtoWithoutCategoryIds.class));

        Assertions.assertEquals(expected.getContent().isEmpty(), actual.getContent().isEmpty());
        Assertions.assertEquals(expected.getTotalElements(), actual.getTotalElements());
        Assertions.assertEquals(expected.getContent().size(), actual.getContent().size());
    }

    private BookDtoWithoutCategoryIds createValidBookWithoutCategoryIdDto() {
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
}
