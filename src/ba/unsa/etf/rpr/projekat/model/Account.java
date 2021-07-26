package ba.unsa.etf.rpr.projekat.model;

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

    public Account() {
    }

    private static void validateEmailAddress(String emailAddress) {
        Pattern regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        Matcher regMatcher   = regexPattern.matcher(emailAddress);
        if(regMatcher.matches()) {
            return;
        } else {
            throw new IllegalArgumentException("Invalid Email Address");
        }
    }

    private static void validateUserName(String userName) {
        if(userName.length() >= 5) {
            return;
        } else {
            throw new IllegalArgumentException("Invalid Email Address");
        }
    }


    private static boolean validatePassword(String password)
    {
        if (password.length() > 25 || password.length() < 8) {
            throw new IllegalArgumentException("Password must be less than 25 and more than 8 characters in length.");
        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars )) {
            throw new IllegalArgumentException("Password must have atleast one uppercase character");
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars )) {
            throw new IllegalArgumentException("Password must have atleast one lowercase character");
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers )) {
            throw new IllegalArgumentException("Password must have atleast one number");
        }
        String specialChars = "(.*[@,#,$,%].*$)";
        if (!password.matches(specialChars )) {
            throw new IllegalArgumentException("Password must have atleast one special character among @#$%");
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        validateUserName(userName);
        this.userName = userName;
    }

    public String getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(String emailAdress) {
        validateEmailAddress(emailAdress);
        this.emailAdress = emailAdress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        validatePassword(password);
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id &&
                userName.equals(account.userName) &&
                emailAdress.equals(account.emailAdress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, emailAdress);
    }
}
