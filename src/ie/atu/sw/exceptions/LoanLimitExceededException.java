package ie.atu.sw.exceptions;

 //Thrown when a loan request exceeds the permitted amount,

public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}