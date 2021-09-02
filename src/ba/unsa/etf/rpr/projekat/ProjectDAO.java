package ba.unsa.etf.rpr.projekat;

import ba.unsa.etf.rpr.projekat.javabean.*;
import ba.unsa.etf.rpr.projekat.model.AccountModel;
import ba.unsa.etf.rpr.projekat.model.GroupModel;
import ba.unsa.etf.rpr.projekat.model.LabelModel;
import ba.unsa.etf.rpr.projekat.model.NoteModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

public class ProjectDAO {
    private static ProjectDAO instance = null;
    private Connection connection;


    private AccountModel accountModel = null;
    private GroupModel groupModel = null;
    private LabelModel labelModel = null;
    private NoteModel noteModel = null;


    private ProjectDAO() {
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

    public Connection getConnection () {
        return connection;
    }

    // ------------------------------------------------------------------------------- //

    // ACCOUNT
    public void isEmailUnique (String emailAdress) {
        accountModel.isEmailUnique (emailAdress);
    }

    public void isUsernameUnique (String username) {
        accountModel.isUsernameUnique (username);
    }

    public boolean createAccount(Account user) {
        return accountModel.createAccount (user);
    }

    public List<Account> getAllAccounts() {
        return accountModel.getAllAccounts ();
    }

    public void updateUser (Account user) {
        accountModel.updateUser (user);
    }

    public void deleteAccount(Account user) {
        List<Group> groups = groupModel.getAllGroupsForAccount (user);
        List<Label> labels = labelModel.getAllLabelsForAccount (user);

        for(Group group : groups) {
            deleteGroup (group.getId ());
        }
        for(Label label : labels) {
            deleteLabel (label.getId ());
        }

        accountModel.deleteAccount (user);
    }

    // ------------------------------------------------------------------------------- //

    // GROUP
    public List<Group> getAllGroupsForAccount(Account user) {
        return groupModel.getAllGroupsForAccount (user);
    }

    public boolean createGroup(Group group) {
        return groupModel.createGroup (group);
    }

    public void updateGroup(Group group) {
        groupModel.updateGroup (group);
    }

    public void deleteGroup(int id) {
        for(Note note : getAllNotesForGroup (id)) {
            noteModel.deleteNote (note.getId ());
        }
        groupModel.deleteGroup (id);
    }

    public GroupModel getGroupModel () {
        return groupModel;
    }

    // ------------------------------------------------------------------------------- //

    // LABEL

    public List<Label> getAllLabelsForAccount(Account account) {
        return labelModel.getAllLabelsForAccount (account);
    }

    public boolean createLabel(Label label) {
        return labelModel.createLabel (label);
    }

    public void updateLabel(Label label) {
        labelModel.updateLabel (label);
    }

    public void deleteLabel(int id) {
        labelModel.deleteLabel (id);
    }

    public LabelModel getLabelModel () {
        return labelModel;
    }

    // ------------------------------------------------------------------------------- //


    // NOTE
    public List<Note> getAllNotesForUser(Account user) {
        return noteModel.getAllNotesForUser (user, labelModel);
    }

    public List<Note> getAllNotesForGroup (int groupId) {
        return noteModel.getAllNotesForGroup (groupId, labelModel);
    }

    public boolean createNote (Note note) {
        return noteModel.createNote (note);
    }

    public void updateNote (Note note) {
        noteModel.updateNote (note);
    }

    public void deleteNote(int id) {
        noteModel.deleteNote (id);
    }

    public NoteModel getNoteModel () {
        return noteModel;
    }
}
