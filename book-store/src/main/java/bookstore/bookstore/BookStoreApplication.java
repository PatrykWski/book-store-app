package bookstore.bookstore;

import bookstore.bookstore.model.Book;
import bookstore.bookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(BookService bookService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book theWitcher = new Book();
                theWitcher.setAuthor("Andrzej Sapkowski");
                theWitcher.setPrice(BigDecimal.TEN);
                theWitcher.setTitle("The witcher");
                theWitcher.setIsbn("9780316029186");
                theWitcher.setDescription("A nice book about the monster killer");
                theWitcher.setCoverImage("");
                bookService.save(theWitcher);
                System.out.println(bookService.findAll());
            }
        };
    }
}
