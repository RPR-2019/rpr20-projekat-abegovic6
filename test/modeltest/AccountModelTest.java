package modeltest;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.ProjectDAO;
import ba.unsa.etf.rpr.projekat.javabean.Account;
import ba.unsa.etf.rpr.projekat.model.AccountModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class AccountModelTest {
    private static ProjectDAO projectDAO;
    private static AccountModel accountModel;

    @BeforeAll
    static void setUp () {
        projectDAO = ProjectDAO.getInstance ();
        accountModel = ProjectDAO.getInstance ().getAccountModel ();
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @AfterAll
    static void onEnd() {
        ProjectDAO.removeInstance ();
    }

    @Test
    void isCurrentUserNull() {
        assertNotNull (AccountModel.getCurrentUser ());
    }

    @Test
    void accountIsCreating() {
        // database empty
        projectDAO.vratiNaDefault ();

        Account acc1 = new Account (0, "firstname", "lastname",
                "username", "email@gmail.com", "Password10#");
        Account acc2 = new Account (0, "firstname", "lastname",
                "username", "email@gmail.com", "Password10#");

        accountModel.createAccount (acc1);
        accountModel.createAccount (acc2);
        // accounts creating
        assertEquals (2, accountModel.getAllAccounts ().size ());

        // createAccount should change account id
        assertEquals (0, acc1.getId ());
        assertEquals (1, acc2.getId ());

    }

    @Test
    void accountIsUpdating() {
        // database empty
        projectDAO.vratiNaDefault ();
        Account acc1 = new Account (0, "firstname", "lastname",
                "username", "email@gmail.com", "Password10#");
        // create account before updating
        accountModel.createAccount (acc1);
        assertEquals (1, accountModel.getAllAccounts ().size ());
        // edit account
        acc1.setEmailAdress ("newemail@gmail.com");
        // update account
        accountModel.updateUser (acc1);
        // check if updated
        List<Account> accounts = new ArrayList<> (accountModel.getAllAccounts ());
        assertEquals ("newemail@gmail.com", accounts.get (0).getEmailAdress ());
    }

    @Test
    void accountIsDeleting() {
        // database empty
        projectDAO.vratiNaDefault ();
        Account acc1 = new Account (0, "firstname", "lastname",
                "username", "email@gmail.com", "Password10#");
        // create account before deleting
        accountModel.createAccount (acc1);
        assertEquals (1, accountModel.getAllAccounts ().size ());
        // delete account
        accountModel.deleteAccount (acc1);
        // check if deleted
        assertEquals (0, accountModel.getAllAccounts ().size ());
    }

    @Test
    void emailUniqueTest() {
        // database empty
        projectDAO.vratiNaDefault ();
        Account acc1 = new Account (0, "firstname", "lastname",
                "username", "email@gmail.com", "Password10#");
        // check if email unique
        assertDoesNotThrow (() -> accountModel.isEmailUnique (acc1.getEmailAdress ()));
        // create account
        accountModel.createAccount (acc1);
        // isEmailUnique should now thow exception
        assertThrows (IllegalArgumentException.class, () -> accountModel.isEmailUnique (acc1.getEmailAdress ()),
                MyResourceBundle.getString("EmailNotUniqueError"));


    }

    @Test
    void usernameUniqueTest() {
        // database empty
        projectDAO.vratiNaDefault ();
        Account acc1 = new Account (0, "firstname", "lastname",
                "username", "email@gmail.com", "Password10#");
        // check if email unique
        assertDoesNotThrow (() -> accountModel.isUsernameUnique (acc1.getUserName ()));
        // create account
        accountModel.createAccount (acc1);
        // isEmailUnique should now thow exception
        assertThrows (IllegalArgumentException.class, () -> accountModel.isUsernameUnique (acc1.getUserName ()),
                MyResourceBundle.getString("UsernameNotUniqueError"));

    }



}
