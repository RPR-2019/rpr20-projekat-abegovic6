package ba.unsa.etf.rpr.projekat.dal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    private static DatabaseConnection instance = null;
    private Connection connection;


    private AccountModel accountModel = null;
    private GroupModel groupModel = null;
    private LabelModel labelModel = null;
    private NoteModel noteModel = null;


    private DatabaseConnection () {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:projectdatabase.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            accountModel = AccountModel.getInstance (connection);
            groupModel = GroupModel.getInstance (connection);
            labelModel = LabelModel.getInstance (connection);
            noteModel = NoteModel.getInstance (connection);

        } catch (SQLException throwables) {
            createDatabase();
            try {
                accountModel = AccountModel.getInstance (connection);
                groupModel = GroupModel.getInstance (connection);
                labelModel = LabelModel.getInstance (connection);
                noteModel = NoteModel.getInstance (connection);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    // METHODS FOR DATABASE
    public static DatabaseConnection getInstance() {
        if(instance == null) instance = new DatabaseConnection ();
        return instance;
    }

    public static void removeInstance() {
        if (instance != null) {
            try {
                instance.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }

    private void createDatabase() {
        Scanner input = null;
        try {
            input = new Scanner(new FileInputStream("projectdatabase.db.sql"));
            String sqlStatement = "";
            while (input.hasNext()) {
                sqlStatement += input.nextLine();
                if ( sqlStatement.length() > 1 && sqlStatement.charAt( sqlStatement.length()-1 ) == ';') {
                    try {
                        Statement stmt = connection.createStatement();
                        stmt.execute(sqlStatement);
                        sqlStatement = "";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace ();
        }

    }

    public void vratiNaDefault() {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM account");
            stmt.executeUpdate("DELETE FROM groups");
            stmt.executeUpdate("DELETE FROM label");
            stmt.executeUpdate("DELETE FROM intertable");
            stmt.executeUpdate("DELETE FROM notes");
            deleteAllData ();
            createDatabase ();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stmt != null) {
                try {
                    stmt.close ();
                } catch (SQLException throwables) {
                    throwables.printStackTrace ();
                }
            }
        }
    }

    private void deleteAllData() {
        accountModel.getAllAccounts ().clear ();
        groupModel.getAllGroups ().clear ();
        groupModel.getGroups ().clear ();
        labelModel.getAllLabels ().clear ();
        noteModel.getAllNotes ().clear ();
        noteModel.getCurrentNotes ().clear ();
    }

    public Connection getConnection () {
        return connection;
    }

    public AccountModel getAccountModel () {
        return accountModel;
    }

    public GroupModel getGroupModel () {
        return groupModel;
    }

    public LabelModel getLabelModel () {
        return labelModel;
    }
    public NoteModel getNoteModel () { return noteModel; }
}
