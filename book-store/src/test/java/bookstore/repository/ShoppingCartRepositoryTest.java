package bookstore.repository;

import bookstore.model.ShoppingCart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ShoppingCartRepositoryTest {
    private static final Long VALID_ID = 10L;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @Sql(scripts = "classpath:shoppingCarts/add-user-and-shopping-cart.sql",
            executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:shoppingCarts/delete-user.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findShoppingCartByUserId_ValidUserId_ReturnsShoppingCart() {

        //when
        ShoppingCart actual = shoppingCartRepository.findShoppingCartByUserId(VALID_ID).get();

        //then
        Assertions.assertEquals(10, actual.getId());
    }
}
