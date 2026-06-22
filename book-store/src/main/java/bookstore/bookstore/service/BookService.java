package bookstore.bookstore.service;

import bookstore.bookstore.model.Book;
import java.util.List;

public interface BookService {

    Book save(Book book);

    List<Book> findAll();
}
