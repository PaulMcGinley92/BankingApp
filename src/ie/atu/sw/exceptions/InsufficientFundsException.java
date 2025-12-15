package ie.atu.sw.exceptions;
/**
 * Thrown when a withdrawal or similar operation is attempted
 * that exceeds the available balance in an account.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}