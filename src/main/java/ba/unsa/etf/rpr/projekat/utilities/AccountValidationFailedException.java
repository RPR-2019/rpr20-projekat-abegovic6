package ba.unsa.etf.rpr.projekat.utilities;

public class AccountValidationFailedException extends RuntimeException {

    public AccountValidationFailedException (String message) {
        super (message);
    }
}
