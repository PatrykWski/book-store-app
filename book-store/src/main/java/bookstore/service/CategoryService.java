package bookstore.service;

import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CategoryRequestDto;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll();

    CategoryDto getById(Long id);

    CategoryDto save(CategoryRequestDto categoryRequestDtoDto);

    CategoryDto update(Long id, CategoryRequestDto categoryRequestDto);

    void deleteById(Long id);
}
