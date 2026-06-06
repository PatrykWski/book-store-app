package bookstore.bookstore.repository;

import bookstore.bookstore.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);
    List<Book> findAll();
}
