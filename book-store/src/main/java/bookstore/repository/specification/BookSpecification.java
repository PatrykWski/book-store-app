package bookstore.repository.specification;

import bookstore.model.Book;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    private static Specification<Book> containsIgnoreCase(String fieldName, String value) {
        if (value == null) {
            return null;
        }
        return new Specification<Book>() {
            @Override
            public @Nullable Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query,
                                                   CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get(fieldName)),
                        "%" + value.toLowerCase() + "%");
            }
        };
    }

    private static Specification<Book> getByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return new Specification<Book>() {
            @Override
            public @Nullable Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query,
                                                   CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (minPrice != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
                }
                if (maxPrice != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    public static Specification<Book> getByTitle(String title) {
        return containsIgnoreCase("title", title);
    }

    public static Specification<Book> getByAuthor(String author) {
        return containsIgnoreCase("author", author);
    }

    public static Specification<Book> getByIsbn(String isbn) {
        return containsIgnoreCase("isbn", isbn);
    }

    public static Specification<Book> getByPrice(BigDecimal min, BigDecimal max) {
        return getByPriceRange(min, max);
    }
}
