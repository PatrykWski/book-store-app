package bookstore.dto.order;

import bookstore.model.Status;
import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusDto(@NotBlank Status status) {
}
