package ba.unsa.etf.rpr.projekat.dto;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.utilities.AccountValidationFailedException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Account {
    private int id;
    private String firstName;
    private String lastName;
    private String userName;
    private String emailAdress;
    private String password;

    public Account () {
    }

    public Account (int id, String firstName, String lastName, String userName, String emailAdress, String password) {
        this.id = id;
        setFirstName (firstName);
        setLastName (lastName);
        setUserName (userName);
        setEmailAdress (emailAdress);
        setPassword (password);
    }

    private void validateEmailAddress (String emailAddress) {
        Pattern regexPattern = Pattern.compile ("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]" +
                "+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\" +
                "x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x" +
                "0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
                "\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9]" +
                ")|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9]" +
                "[0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-" +
                "\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        Matcher regMatcher = regexPattern.matcher (emailAddress);
        if (!regMatcher.matches ())
            throw new AccountValidationFailedException (MyResourceBundle.getString ("InvalidEmailAddress"));
    }

    private void validateUserName (String userName) {
        if (userName.length () < 5)
            throw new AccountValidationFailedException (MyResourceBundle.getString ("UssernameLenghtError"));

    }


    private void validatePassword (String password) {
        if (password.length () > 25 || password.length () < 8) {
            throw new AccountValidationFailedException (MyResourceBundle.getString ("PasswordLengthError"));
        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches (upperCaseChars)) {
            throw new AccountValidationFailedException (MyResourceBundle.getString ("PasswordUppercaseError"));
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches (lowerCaseChars)) {
            throw new AccountValidationFailedException (MyResourceBundle.getString ("PasswordLowercaseError"));
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches (numbers)) {
            throw new AccountValidationFailedException (MyResourceBundle.getString ("PasswordNumberError"));
        }
        String specialChars = "(.*[@,#,$,%].*$)";
        if (!password.matches (specialChars)) {
            throw new AccountValidationFailedException (MyResourceBundle.getString ("PasswordSpecialCharacterError"));
        }
    }

    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getFirstName () {
        return firstName;
    }

    public void setFirstName (String firstName) {
        this.firstName = firstName.substring (0, 1).toUpperCase () + firstName.substring (1);
    }

    public String getLastName () {
        return lastName;
    }

    public void setLastName (String lastName) {
        this.lastName = lastName.substring (0, 1).toUpperCase () + lastName.substring (1);
    }

    public String getUserName () {
        return userName;
    }

    public void setUserName (String userName) {
        validateUserName (userName);
        this.userName = userName;
    }

    public String getEmailAdress () {
        return emailAdress;
    }

    public void setEmailAdress (String emailAdress) {
        validateEmailAddress (emailAdress);
        this.emailAdress = emailAdress;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        validatePassword (password);
        this.password = password;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;
        Account account = (Account) o;
        return id == account.id;
    }

    @Override
    public int hashCode () {
        return Objects.hash (id, userName, emailAdress);
    }
}
