package dtotest;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dto.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private static Account account;

    @BeforeAll
    static void setUp () {
        account = new Account ();
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @Test
    void accountIsEmpty() {
        account = new Account ();
        assertEquals (0, account.getId ());
        assertNull (account.getFirstName ());
        assertNull (account.getLastName ());
        assertNull (account.getEmailAdress ());
        assertNull (account.getUserName ());
        assertNull (account.getPassword ());
    }


    @Test
    void firstnameTest() {
        // first name must start with an uppercase letter
        account.setFirstName ("firstname");
        assertEquals ("Firstname", account.getFirstName ());
    }

    @Test
    void lastnameTest() {
        // last name must start with an uppercase letter
        account.setLastName ("lastname");
        assertEquals ("Lastname", account.getLastName ());
    }

    @Test
    void usernameValidationOK() {
        // username must be at least 5 characters long
        assertThrows (IllegalArgumentException.class, () -> account.setUserName ("user"),
                MyResourceBundle.getString ("UssernameLenghtError"));
        assertDoesNotThrow (() -> account.setUserName ("username"));

    }

    @Test
    void emailValidationOK() {
        // does email validation work
        assertThrows (IllegalArgumentException.class, () -> account.setEmailAdress ("email"),
                MyResourceBundle.getString ("InvalidEmailAddress"));
        assertThrows (IllegalArgumentException.class, () -> account.setEmailAdress ("email@"),
                MyResourceBundle.getString ("InvalidEmailAddress"));
        assertThrows (IllegalArgumentException.class, () -> account.setEmailAdress ("email@adress."),
                MyResourceBundle.getString ("InvalidEmailAddress"));
        assertThrows (IllegalArgumentException.class, () -> account.setEmailAdress ("emailadress.com"),
                MyResourceBundle.getString ("InvalidEmailAddress"));
        assertDoesNotThrow (() -> account.setEmailAdress ("email@etf.unsa.ba"));
        assertDoesNotThrow (() -> account.setEmailAdress ("email@adress.com"));
    }

    @Test
    void passwordValidationOK() {
        // password must be at least 8 and less than 25 characters long
        assertThrows (IllegalArgumentException.class, () -> account.setPassword ("short"),
                MyResourceBundle.getString ("PasswordLengthError"));
        assertThrows (IllegalArgumentException.class, () -> account.setPassword (":thispasswordwillbetoolong:"),
                MyResourceBundle.getString ("PasswordLengthError"));

        // have at least one uppercase letter
        assertThrows (IllegalArgumentException.class, () -> account.setPassword ("doesnthaveuppercase"),
                MyResourceBundle.getString ("PasswordUppercaseError"));

        // have at least one lowercase letter
        assertThrows (IllegalArgumentException.class, () -> account.setPassword ("DOESNTHAVELOWERCASE"),
                MyResourceBundle.getString ("PasswordLowercaseError"));

        // have at least one number
        assertThrows (IllegalArgumentException.class, () -> account.setPassword ("DOESNTHAVEnumber"),
                MyResourceBundle.getString ("PasswordNumberError"));

        // have at least one special characther @#$%
        assertThrows (IllegalArgumentException.class, () -> account.setPassword ("SpecialCharacter22"),
                MyResourceBundle.getString ("PasswordSpecialCharacterError"));

        //this password is okey
        assertDoesNotThrow (() -> account.setPassword ("Password10#"));
    }

    @Test
    void secondConstructorTest() {
        // username not correct
        assertThrows (IllegalArgumentException.class,
                () -> new Account (0, "firstname", "lastname", "user",
                        "email@gmail.com", "Password10#"));
        // email not correct
        assertThrows (IllegalArgumentException.class,
                () -> new Account (0, "firstname", "lastname", "username",
                        "email.com", "Password10#"));
        // password not correct
        assertThrows (IllegalArgumentException.class,
                () -> new Account (0, "firstname", "lastname", "username",
                        "email@gmail.com", "Password1"));

        // everything correct
        assertDoesNotThrow (() -> account = new Account (0, "firstname", "lastname",
                "username", "email@gmail.com", "Password10#"));

        // checking id, firstname and last name
        assertEquals (0, account.getId ());
        assertEquals ("Firstname", account.getFirstName ());
        assertEquals ("Lastname", account.getLastName ());
    }

}
