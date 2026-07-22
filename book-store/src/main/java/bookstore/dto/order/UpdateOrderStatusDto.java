package bookstore.dto.order;

import bookstore.model.Status;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusDto(@NotNull Status status) {
}
