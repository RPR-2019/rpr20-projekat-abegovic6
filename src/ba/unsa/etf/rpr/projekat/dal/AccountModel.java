package ba.unsa.etf.rpr.projekat.dal;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dto.Account;
import ba.unsa.etf.rpr.projekat.dto.Group;
import ba.unsa.etf.rpr.projekat.dto.Label;
import ba.unsa.etf.rpr.projekat.utilities.AccountValidationFailedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AccountModel {
    private static AccountModel instance = null;

    private final PreparedStatement createAccountStatement;
    private final PreparedStatement getAllAccountsStatement;
    private final PreparedStatement updateAccountStatement;
    private final PreparedStatement deleteAccountStatement;
    private final PreparedStatement getNewIdAccountStatement;
    private final PreparedStatement emailAdressUniqueStatement;
    private final PreparedStatement usernameUniqueStatement;

    private static Account currentUser = new Account ();

    private AccountModel (Connection conn) throws SQLException {
        getAllAccountsStatement = conn.prepareStatement("SELECT * FROM account");
        createAccountStatement = conn.prepareStatement("INSERT INTO account (id, firstName, lastName," +
                " userName, emailAdress, password) VALUES (?,?,?,?,?,?)");
        updateAccountStatement = conn.prepareStatement("UPDATE account SET (firstName, lastName, " +
                "userName, emailAdress, password) = (?,?,?,?,?) WHERE id = ?");
        deleteAccountStatement = conn.prepareStatement("DELETE FROM account WHERE id = ?");
        getNewIdAccountStatement = conn.prepareStatement("SELECT MAX(id) + 1 FROM account");
        usernameUniqueStatement = conn.prepareStatement("SELECT COUNT(id) FROM account WHERE userName = ?");
        emailAdressUniqueStatement = conn.prepareStatement("SELECT COUNT(id) FROM account WHERE emailAdress = ?");
    }

    public static Account getCurrentUser () {
        return currentUser;
    }

    public static void setCurrentUser (Account currentUser) {
        AccountModel.currentUser = currentUser;
    }

    public static AccountModel getInstance (Connection conn)
            throws SQLException {
        if(instance == null) instance = new AccountModel (conn);
        return instance;
    }

    private Account getAccountFromResultSet(ResultSet resultSetAccount) {
        try {
            Account account = new Account();
            account.setId(resultSetAccount.getInt(1));
            account.setFirstName(resultSetAccount.getString(2));
            account.setLastName(resultSetAccount.getString(3));
            account.setUserName(resultSetAccount.getString(4));
            account.setEmailAdress(resultSetAccount.getString(5));
            account.setPassword(resultSetAccount.getString(6));
            return account;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    private List<Account> getAccountListFromResultSet(ResultSet resultSetAccountList) {
        try {
            List<Account> accounts = new ArrayList<> ();
            while(resultSetAccountList.next()) {
                accounts.add(getAccountFromResultSet(resultSetAccountList));
            }
            return  accounts;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Account> getAllAccounts() {
        try {
            return getAccountListFromResultSet(getAllAccountsStatement.executeQuery());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList ();
    }

    public void isEmailUnique (String emailAdress) {
        try {
            emailAdressUniqueStatement.setString(1, emailAdress);
            ResultSet resultSet = emailAdressUniqueStatement.executeQuery();
            if(resultSet.next() && resultSet.getInt(1) != 0)
                throw new AccountValidationFailedException (MyResourceBundle.getString("EmailNotUniqueError"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void isUsernameUnique (String username) {
        try {
            usernameUniqueStatement.setString(1, username);
            ResultSet resultSet = usernameUniqueStatement.executeQuery();
            if(resultSet.next() && resultSet.getInt(1) != 0)
                throw new AccountValidationFailedException (MyResourceBundle.getString("UsernameNotUniqueError"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean createAccount(Account account) {
        try {
            ResultSet resultSet = getNewIdAccountStatement.executeQuery();
            if(resultSet.next()) {
                account.setId(resultSet.getInt(1));
            } else {
                account.setId(1);
            }
            if(DatabaseConnection.isTesting ()) {
                account.setId (0);
                deleteAccount (account);
            } else if(account.getId () == 0) {
                account.setId (1);
            }
            createAccountStatement.setInt(1, account.getId());
            createAccountStatement.setString(2, account.getFirstName());
            createAccountStatement.setString(3, account.getLastName());
            createAccountStatement.setString(4, account.getUserName());
            createAccountStatement.setString(5, account.getEmailAdress());
            createAccountStatement.setString(6, account.getPassword());

            createAccountStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }


        return true;
    }

    public void updateUser (Account user) {
        try {
            updateAccountStatement.setInt(6, user.getId());
            updateAccountStatement.setString(1, user.getFirstName ());
            updateAccountStatement.setString(2, user.getLastName ());
            updateAccountStatement.setString(3, user.getUserName ());
            updateAccountStatement.setString(4, user.getEmailAdress ());
            updateAccountStatement.setString(5, user.getPassword ());
            updateAccountStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void deleteAccount(Account account) {
        try {
            var groupModel = DatabaseConnection.getInstance ().getGroupModel ();
            var labelModel = DatabaseConnection.getInstance ().getLabelModel ();
            List<Group> groups = groupModel.getAllGroupsForAccount (account);
            List<Label> labels = labelModel.getAllLabelsForAccount (account);

            for(Group group : groups) {
                groupModel.deleteGroup (group.getId ());
            }
            for(Label label : labels) {
                labelModel.deleteLabel (label.getId ());
            }

            deleteAccountStatement.setInt (1, account.getId ());
            deleteAccountStatement.executeUpdate ();
        } catch (SQLException throwables) {
            throwables.printStackTrace ();
        }

    }


}
