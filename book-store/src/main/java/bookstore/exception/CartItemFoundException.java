package bookstore.exception;

public class CartItemFoundException extends RuntimeException {
    public CartItemFoundException(String message) {
        super(message);
    }
}
