package ie.atu.sw.exceptions;

/**
 * Thrown when an operation is attempted using an invalid
 * monetary amount (e.g., negative deposit, negative withdrawal,
 * or repayment exceeding allowed limits).
 */

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}