package ba.unsa.etf.rpr.daltest;

import ba.unsa.etf.rpr.Utility;
import ba.unsa.etf.rpr.projekat.utilities.AccountValidationFailedException;
import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dto.Account;
import ba.unsa.etf.rpr.projekat.dal.AccountModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class AccountModelTest {
    private static DatabaseConnection databaseConnection;
    private static AccountModel accountModel;
    private static Account account;

    @BeforeAll
    static void setUp () {
        account = Utility.getInstance ().getTestAccount ();
        databaseConnection = DatabaseConnection.getInstance ();
        DatabaseConnection.setTesting (true);
        accountModel = DatabaseConnection.getInstance ().getAccountModel ();
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @BeforeEach
    void forEachTest() {
        databaseConnection.deleteTestUser (account);
    }

    @Test
    void isCurrentUserNull() {
        assertNotNull (AccountModel.getCurrentUser ());
    }

    @Test
    void accountIsCreating() {
        // database empty
        accountModel.createAccount (account);
        // accounts creating
        assertTrue (accountModel.getAllAccounts ().contains (account));
    }

    @Test
    void accountIsUpdating() {
        // database empty
        // create account before updating
        accountModel.createAccount (account);
        // edit account
        account.setEmailAdress ("newemail@gmail.com");
        // update account
        accountModel.updateUser (account);
        // check if updated

        var acc = accountModel.getAllAccounts ().stream ()
                .filter (a-> a.getId () == account.getId ()).findFirst ();
        acc.ifPresent (value -> assertEquals ("newemail@gmail.com", value.getEmailAdress ()));
    }

    @Test
    void accountIsDeleting() {
        // database empty
        // create account before deleting
        accountModel.createAccount (account);
        assertTrue (accountModel.getAllAccounts ().contains (account));
        // delete account
        accountModel.deleteAccount (account);
        // check if deleted
        assertFalse (accountModel.getAllAccounts ().contains (account));
    }

    @Test
    void emailUniqueTest() {
        // database empty
        // check if email unique
        assertDoesNotThrow (() -> accountModel.isEmailUnique (account.getEmailAdress ()));
        // create account
        accountModel.createAccount (account);
        // isEmailUnique should now thow exception
        assertThrows (AccountValidationFailedException.class,
                () -> accountModel.isEmailUnique (account.getEmailAdress ()),
                MyResourceBundle.getString("EmailNotUniqueError"));


    }

    @Test
    void usernameUniqueTest() {
        // database empty
        // check if email unique
        assertDoesNotThrow (() -> accountModel.isUsernameUnique (account.getUserName ()));
        // create account
        accountModel.createAccount (account);
        // isEmailUnique should now thow exception
        assertThrows (AccountValidationFailedException.class,
                () -> accountModel.isUsernameUnique (account.getUserName ()),
                MyResourceBundle.getString("UsernameNotUniqueError"));

    }



}
