package bookstore.repository;

import bookstore.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BookRepositoryTest {

    private static final Long TEST_CATEGORY_ID = 1L;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @Sql(scripts = "classpath:database/books/add-book-and-category.sql",
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-book-and-category.sql",
    executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_CategoryExists_ReturnsPageOfBooks() {
        //given
        Pageable pageable = Pageable.ofSize(20);

        //when
        Page<Book> actual = bookRepository.findAllByCategoryId(TEST_CATEGORY_ID, pageable);

        //then
        Assertions.assertEquals(1, actual.getTotalElements());
        Assertions.assertEquals("Wiedzmin", actual.getContent().get(0).getTitle());
    }
}
