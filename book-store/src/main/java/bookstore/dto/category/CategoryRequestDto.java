package bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {
    @NotBlank(message = "Category name must not be blank")
    @Size(max = 255)
    private String name;
    @Size(max = 255)
    private String description;
}
