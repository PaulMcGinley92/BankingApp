package ie.atu.sw.exceptions;

/**
 * Thrown when an operation is attempted on an account
 * that does not exist in the banking system.
 */

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}