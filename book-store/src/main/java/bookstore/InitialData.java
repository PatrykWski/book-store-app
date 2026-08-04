package bookstore;

import bookstore.service.UserInitializerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitialData implements CommandLineRunner {
    private final UserInitializerService userInitializerService;

    @Override
    public void run(String... args) throws Exception {
        boolean adminCreated = userInitializerService.initializeUser(
                System.getenv("ADMIN_EMAIL"),
                System.getenv("ADMIN_PASSWORD"),
                System.getenv("ADMIN_FIRST_NAME"),
                System.getenv("ADMIN_LAST_NAME")
        );
    }
}
