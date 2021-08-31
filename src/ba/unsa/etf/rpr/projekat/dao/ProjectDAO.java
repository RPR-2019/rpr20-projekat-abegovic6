package ba.unsa.etf.rpr.projekat.dao;

import ba.unsa.etf.rpr.projekat.model.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

public class ProjectDAO {
    private static ProjectDAO instance = null;
    private Connection connection;

    private AccountDAO accountDAO = null;
    private GroupDAO groupDAO = null;
    private LabelDAO labelDAO = null;
    private NoteDAO noteDAO = null;


    private ProjectDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:projectdatabase.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            accountDAO = AccountDAO.getInstance (connection);
            groupDAO = GroupDAO.getInstance (connection);
            labelDAO = LabelDAO.getInstance (connection);
            noteDAO = NoteDAO.getInstance (connection);

        } catch (SQLException throwables) {
            createDatabase();
            try {
                accountDAO = AccountDAO.getInstance (connection);
                groupDAO = GroupDAO.getInstance (connection);
                labelDAO = LabelDAO.getInstance (connection);
                noteDAO = NoteDAO.getInstance (connection);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // METHODS FOR DATABASE
    public static ProjectDAO getInstance() {
        if(instance == null) instance = new ProjectDAO();
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

    // ------------------------------------------------------------------------------- //

    // ACCOUNT
    public void isEmailUnique (String emailAdress) {
        accountDAO.isEmailUnique (emailAdress);
    }

    public void isUsernameUnique (String username) {
        accountDAO.isUsernameUnique (username);
    }

    public boolean createAccount(Account user) {
        return accountDAO.createAccount (user);
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts ();
    }

    public void updateUser (Account user) {
        accountDAO.updateUser (user);
    }

    public void deleteAccount(Account user) {
        List<Group> groups = groupDAO.getAllGroupsForAccount (user);
        List<Label> labels = labelDAO.getAllLabelsForAccount (user);

        for(Group group : groups) {
            deleteGroup (group.getId ());
        }
        for(Label label : labels) {
            deleteLabel (label.getId ());
        }

        accountDAO.deleteAccount (user);
    }

    // ------------------------------------------------------------------------------- //

    // GROUP
    public List<Group> getAllGroupsForAccount(Account user) {
        return groupDAO.getAllGroupsForAccount (user);
    }

    public boolean createGroup(Group group) {
        return groupDAO.createGroup (group);
    }

    public void updateGroup(Group group) {
        groupDAO.updateGroup (group);
    }

    public void deleteGroup(int id) {
        for(Note note : getAllNotesForGroup (id)) {
            noteDAO.deleteNote (note.getId ());
        }
        groupDAO.deleteGroup (id);
    }

    // ------------------------------------------------------------------------------- //

    // LABEL

    public List<Label> getAllLabelsForAccount(Account account) {
        return labelDAO.getAllLabelsForAccount (account);
    }

    public boolean createLabel(Label label) {
        return labelDAO.createLabel (label);
    }

    public void updateLabel(Label label) {
        labelDAO.updateLabel (label);
    }

    public void deleteLabel(int id) {
        labelDAO.deleteLabel (id);
    }

    // ------------------------------------------------------------------------------- //


    // NOTE
    public List<Note> getAllNotesForUser(Account user) {
        return noteDAO.getAllNotesForUser (user, labelDAO);
    }

    public List<Note> getAllNotesForGroup (int groupId) {
        return noteDAO.getAllNotesForGroup (groupId, labelDAO);
    }

    public boolean createNote (Note note) {
        return noteDAO.createNote (note);
    }

    public void updateNote (Note note) {
        noteDAO.updateNote (note);
    }

    public void deleteNote(int id) {
        noteDAO.deleteNote (id);
    }


}
