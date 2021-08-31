package ba.unsa.etf.rpr.projekat.javabean;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;

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

    private void validateEmailAddress (String emailAddress) {
        Pattern regexPattern = Pattern.compile ("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        Matcher regMatcher = regexPattern.matcher (emailAddress);
        if (!regMatcher.matches ())
            throw new IllegalArgumentException (MyResourceBundle.getString ("InvalidEmailAddress"));
    }

    private void validateUserName (String userName) {
        if (userName.length () < 5)
            throw new IllegalArgumentException (MyResourceBundle.getString ("UssernameLenghtError"));

    }


    private void validatePassword (String password) {
        if (password.length () > 25 || password.length () < 8) {
            throw new IllegalArgumentException (MyResourceBundle.getString ("PasswordLengthError"));
        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches (upperCaseChars)) {
            throw new IllegalArgumentException (MyResourceBundle.getString ("PasswordUppercaseError"));
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches (lowerCaseChars)) {
            throw new IllegalArgumentException (MyResourceBundle.getString ("PasswordLowercaseError"));
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches (numbers)) {
            throw new IllegalArgumentException (MyResourceBundle.getString ("PasswordNumberError"));
        }
        String specialChars = "(.*[@,#,$,%].*$)";
        if (!password.matches (specialChars)) {
            throw new IllegalArgumentException (MyResourceBundle.getString ("PasswordSpecialCharacterError"));
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
        return id == account.id &&
                userName.equals (account.userName) &&
                emailAdress.equals (account.emailAdress);
    }

    @Override
    public int hashCode () {
        return Objects.hash (id, userName, emailAdress);
    }
}
