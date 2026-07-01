package bookstore.repository.specification;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder<T> {
    private final List<Specification<T>> specs = new ArrayList<>();

    public SpecificationBuilder<T> with(Specification<T> spec) {
        if (spec != null) {
            specs.add(spec);
        }
        return this;
    }

    public Specification<T> build() {
        return specs.stream()
                .reduce(Specification::and)
                .orElse(null);
    }
}
