package ba.unsa.etf.rpr.projekat.dao;


import ba.unsa.etf.rpr.projekat.model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProjectDAO {
    private static ProjectDAO instance = null;
    private static Connection connection;
    private FileInputStream fileInputStream;
    private ResourceBundle resourceBundle;

    // ACCOUNT
    private PreparedStatement createAccountStatement, getAllAccountsStatement, updateAccountStatement,
            deleteAccountStatement, getNewIdAccountStatement,
            emailAdressUniqueStatement, usernameUniqueStatement;

    // GROUPS
    private PreparedStatement createGroupStatement, getAllGroupsForAccountStatement, deleteGroupStatement,
            updateGroupStatement,  getNewIdGroupStatement,
            deleteGroupsForAccountStatement;

    // LABELS
    private PreparedStatement createLabelStatement, getAllLabelsForAccountStatement, deleteLabelStatement,
            updateLabelStatement,
            getNewIdLabelStatement, deleteLabelsForAccountStatement, getLabelFromIdStatement;

    // INTERTABLE
    private PreparedStatement getAllLabelsIdForNoteStatement, getAllNotesIdForNoteStatement, addNewLabelForNoteStatement,
            removeLAbelFromNoteStatement, deleteNoteIdStatement, deleteLabelIdStatement;

    // NOTES
    private PreparedStatement getAllNotesForGroupStatement, getAllNotesForLabelStatement, createNoteStatement, updateGroupIdNoteStatement, updateNotesStatement,
            deleteNoteStatement, getNewIdNoteStatement, deleteNotesForGroupStatement;

    private ProjectDAO() {
        Locale currentLocale = Locale.getDefault();
        resourceBundle = ResourceBundle.getBundle("Translation", currentLocale);



        try {
            connection = DriverManager.getConnection("jdbc:sqlite:projectdatabase.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            getAllAccountsStatement = connection.prepareStatement("SELECT * FROM account");
            createAccountStatement = connection.prepareStatement("INSERT INTO account (id, firstName, lastName," +
                    " userName, emailAdress, password) VALUES (?,?,?,?,?,?)");
            updateAccountStatement = connection.prepareStatement("UPDATE account SET (firstName, lastName, " +
                    "userName, emailAdress, password) = (?,?,?,?,?) WHERE id = ?");
            deleteAccountStatement = connection.prepareStatement("DELETE FROM account WHERE id = ?");
            getNewIdAccountStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM account");
            usernameUniqueStatement = connection.prepareStatement("SELECT COUNT(id) FROM account WHERE userName = ?");
            emailAdressUniqueStatement = connection.prepareStatement("SELECT COUNT(id) FROM account WHERE emailAdress = ?");

            // GROUPS
            createGroupStatement = connection.prepareStatement("INSERT INTO groups (id, accountId, groupName, " +
                  "description, groupColor) VALUES (?, ?, ?, ?, ?)");
            getAllGroupsForAccountStatement = connection.prepareStatement("SELECT * FROM groups WHERE accountId = ?");
            updateGroupStatement = connection.prepareStatement("UPDATE groups SET (groupName, description, groupColor) " +
                    "= (?,?,?) WHERE id = ?");
            deleteGroupStatement = connection.prepareStatement("DELETE FROM groups WHERE id = ?");
            deleteGroupsForAccountStatement = connection.prepareStatement("DELETE FROM groups WHERE accountId = ?");
            getNewIdGroupStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM groups");

            // LABELS
            createLabelStatement = connection.prepareStatement("INSERT INTO label (id, accountId, labelName, " +
                    "description, labelColor) VALUES (?, ?, ?, ?, ?)");
            getAllLabelsForAccountStatement = connection.prepareStatement("SELECT * FROM label WHERE accountId = ?");
            getLabelFromIdStatement = connection.prepareStatement("SELECT * FROM label WHERE id = ?");
            updateLabelStatement = connection.prepareStatement("UPDATE label SET (labelName, description, labelColor) " +
                    "= (?,?,?) WHERE id = ?");
            deleteLabelStatement = connection.prepareStatement("DELETE FROM label WHERE id = ?");
            deleteLabelsForAccountStatement = connection.prepareStatement("DELETE FROM label WHERE accountId = ?");
            getNewIdLabelStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM label");

            // INTERTABLE
            getAllLabelsIdForNoteStatement = connection.prepareStatement("SELECT labelId FROM intertable WHERE noteId = ?");
            getAllNotesIdForNoteStatement = connection.prepareStatement("SELECT noteId FROM intertable WHERE labelId = ?");
            addNewLabelForNoteStatement = connection.prepareStatement("INSERT INTO intertable (noteId, labelId) VALUES (?, ?)");
            removeLAbelFromNoteStatement = connection.prepareStatement("DELETE FROM intertable WHERE labelId = ? AND noteId = ?");
            deleteLabelIdStatement = connection.prepareStatement("DELETE FROM intertable WHERE labelId = ?");
            deleteNoteIdStatement = connection.prepareStatement("DELETE FROM intertable WHERE noteId = ?");

            // NOTES
            createNoteStatement = connection.prepareStatement("INSERT INTO notes (id, groupId, noteTitle, " +
                    "description, noteColor, image) VALUES (?, ?, ?, ?, ?, ?)");
            updateNotesStatement = connection.prepareStatement("UPDATE notes SET (noteTitle, description, noteColor, image) " +
                    "= (?,?,?,?) WHERE id = ?");
            getAllNotesForGroupStatement = connection.prepareStatement("SELECT * FROM notes WHERE groupId = ?");
            deleteNoteStatement = connection.prepareStatement("DELETE FROM notes WHERE id = ?");
            deleteNotesForGroupStatement = connection.prepareStatement("DELETE FROM notes WHERE groupId = ?");
            getNewIdNoteStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM notes");
            getAllNotesForLabelStatement = connection.prepareStatement ("SELECT n.* FROM notes n, intertable i" +
                    " WHERE i.labelId = ? AND n.id = i.noteId");
        } catch (SQLException throwables) {
            createDatabase();
            try {
                // ACCOUNT
                getAllAccountsStatement = connection.prepareStatement("SELECT * FROM account");
                createAccountStatement = connection.prepareStatement("INSERT INTO account (id, firstName, lastName," +
                        " userName, emailAdress, password) VALUES (?,?,?,?,?,?)");
                updateAccountStatement = connection.prepareStatement("UPDATE account SET (firstName, lastName, " +
                        "userName, emailAdress, password) = (?,?,?,?,?) WHERE id = ?");
                deleteAccountStatement = connection.prepareStatement("DELETE FROM account WHERE id = ?");
                getNewIdAccountStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM account");
                usernameUniqueStatement = connection.prepareStatement("SELECT COUNT(id) FROM account WHERE userName = ?");
                emailAdressUniqueStatement = connection.prepareStatement("SELECT COUNT(id) FROM account WHERE emailAdress = ?");

                // GROUPS
                createGroupStatement = connection.prepareStatement("INSERT INTO groups (id, accountId, " +
                        "groupName, description, groupColor) VALUES (?,?,?,?,?)");
                getAllGroupsForAccountStatement = connection.prepareStatement("SELECT * FROM groups WHERE accountId = ?");
                updateGroupStatement = connection.prepareStatement("UPDATE groups SET (groupName, description, groupColor) " +
                        "= (?,?,?) WHERE id = ?");
                deleteGroupStatement = connection.prepareStatement("DELETE FROM groups WHERE id = ?");
                deleteGroupsForAccountStatement = connection.prepareStatement("DELETE FROM groups WHERE accountId = ?");
                getNewIdGroupStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM groups");

                // LABELS
                createLabelStatement = connection.prepareStatement("INSERT INTO label (id, accountId, labelName, " +
                        "description, labelColor) VALUES (?, ?, ?, ?, ?)");
                updateLabelStatement = connection.prepareStatement("UPDATE label SET (labelName, description, labelColor) " +
                        "= (?,?,?) WHERE id = ?");
                getAllLabelsForAccountStatement = connection.prepareStatement("SELECT * FROM label WHERE accountId = ?");
                getLabelFromIdStatement = connection.prepareStatement("SELECT * FROM label WHERE id = ?");
                deleteLabelStatement = connection.prepareStatement("DELETE FROM label WHERE id = ?");
                deleteLabelsForAccountStatement = connection.prepareStatement("DELETE FROM label WHERE accountId = ?");
                getNewIdLabelStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM label");

                // INTERTABLE
                getAllLabelsIdForNoteStatement = connection.prepareStatement("SELECT labelId FROM intertable WHERE noteId = ?");
                getAllNotesIdForNoteStatement = connection.prepareStatement("SELECT noteId FROM intertable WHERE labelId = ?");
                addNewLabelForNoteStatement = connection.prepareStatement("INSERT INTO intertable (noteId, labelId) VALUES (?, ?)");
                removeLAbelFromNoteStatement = connection.prepareStatement("DELETE FROM intertable WHERE labelId = ? AND noteId = ?");
                deleteLabelIdStatement = connection.prepareStatement("DELETE FROM intertable WHERE labelId = ?");
                deleteNoteIdStatement = connection.prepareStatement("DELETE FROM intertable WHERE noteId = ?");

                // NOTES
                createNoteStatement = connection.prepareStatement("INSERT INTO notes (id, groupId, noteTitle, " +
                        "description, noteColor, image) VALUES (?, ?, ?, ?, ?, ?)");
                updateNotesStatement = connection.prepareStatement("UPDATE notes SET (noteTitle, description, noteColor, image) " +
                        "= (?,?,?,?) WHERE id = ?");
                getAllNotesForGroupStatement = connection.prepareStatement("SELECT * FROM notes WHERE groupId = ?");
                deleteNoteStatement = connection.prepareStatement("DELETE FROM notes WHERE id = ?");
                deleteNotesForGroupStatement = connection.prepareStatement("DELETE FROM notes WHERE groupId = ?");
                getNewIdNoteStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM notes");
                getAllNotesForLabelStatement = connection.prepareStatement ("SELECT n.* FROM notes n, intertable i" +
                        " WHERE i.labelId = ? AND n.id = i.noteId");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ------------------------------------------------------------------------------- //

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

    public void returnToDefault() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DELETE FROM account");
        stmt.executeUpdate("DELETE FROM groups");
        stmt.executeUpdate("DELETE FROM notes");
        stmt.executeUpdate("DELETE FROM label");
        stmt.executeUpdate("DELETE FROM intertable");
        createDatabase();
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
            System.out.println("The SQL database doesn't exist.. Continuing with empty database");
        }

    }

    // ------------------------------------------------------------------------------- //

    // ACCOUNT
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
            List<Account> accounts = new ArrayList<>();
            while(resultSetAccountList.next()) {
                //resultSetAccountList.previous();
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
        return null;
    }

    public void isEmailUnique (String emailAdress) {
        try {
            emailAdressUniqueStatement.setString(1, emailAdress);
            ResultSet resultSet = emailAdressUniqueStatement.executeQuery();
            if(resultSet.next() && resultSet.getInt(1) != 0)
                throw new IllegalArgumentException(resourceBundle.getString("EmailNotUniqueError"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return;
    }

    public void isUsernameUnique (String username) {
        try {
            usernameUniqueStatement.setString(1, username);
            ResultSet resultSet = usernameUniqueStatement.executeQuery();
            if(resultSet.next() && resultSet.getInt(1) != 0)
                    throw new IllegalArgumentException(resourceBundle.getString("UsernameNotUniqueError"));
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

    public boolean deleteAccount(ResultSet resultSet, Account account) {
        return true;
    }

    // ------------------------------------------------------------------------------- //

    // GROUP
    private GroupColor stringToGroupColor(String string) {
        return GroupColor.valueOf(string);
    }

    private String groupColorToString(GroupColor groupColor) { return groupColor.toString(); }

    private Group getGroupFromResultSet(ResultSet resultSetGroup) {
        try {
            Group group = new Group();
            group.setId(resultSetGroup.getInt(1));
            group.setAccountId(resultSetGroup.getInt(2));
            group.setGroupName(resultSetGroup.getString(3));
            group.setDescription(resultSetGroup.getString(4));
            group.setGroupColor(stringToGroupColor(resultSetGroup.getString(5)));
            return group;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    private List<Group> getGroupListFromResultSet(ResultSet resultSetGroupList) {
        try {
            List<Group> groups = new ArrayList<>();
            while(resultSetGroupList.next()) {
                groups.add(getGroupFromResultSet(resultSetGroupList));
            }
            return  groups;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Group> getAllGroupsForAccount(Account account) {
        try {
            getAllGroupsForAccountStatement.setInt(1, account.getId());
            return getGroupListFromResultSet(getAllGroupsForAccountStatement.executeQuery());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public boolean createGroup(Group group) {
        try {
            ResultSet resultSet = getNewIdGroupStatement.executeQuery();
            if(resultSet.next()) {
                group.setId(resultSet.getInt(1));
            } else {
                group.setId(1);
            }
            createGroupStatement.setInt(1, group.getId());
            createGroupStatement.setInt(2, group.getAccountId());
            createGroupStatement.setString(3, group.getGroupName());
            createGroupStatement.setString(4, group.getDescription());
            createGroupStatement.setString(5, group.getGroupColor().name());

            createGroupStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }



    public boolean updateGroup(Group group) {
        try {
            updateGroupStatement.setInt(4, group.getId());
            updateGroupStatement.setString(1, group.getGroupName());
            updateGroupStatement.setString(2, group.getDescription());
            updateGroupStatement.setString(3, group.getGroupColor().name());
            updateGroupStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return  false;
        }


        return  true;
    }

    // ------------------------------------------------------------------------------- //

    // LABEL
    private LabelColor stringToLabelColor(String string) {
        return LabelColor.valueOf(string);
    }

    private Label getLabelFromResultSet(ResultSet resultSetLabel) {
        try {
            Label label = new Label();
            label.setId(resultSetLabel.getInt(1));
            label.setAccountId(resultSetLabel.getInt(2));
            label.setLabelName(resultSetLabel.getString(3));
            label.setDescription(resultSetLabel.getString(4));
            label.setLabelColor(stringToLabelColor(resultSetLabel.getString(5)));

            return label;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private Label getLabelFromId(int labelId) {
        try {
            getLabelFromIdStatement.setInt(1, labelId);
            ResultSet resultSet = getLabelFromIdStatement.executeQuery();
            if(resultSet.next())
                return getLabelFromResultSet(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public List<Label> getAllLabelsForAccount(Account account) {
        try {
            getAllLabelsForAccountStatement.setInt(1, account.getId());
            return getLabelListFromResultSet(getAllLabelsForAccountStatement.executeQuery());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<Label> getLabelListFromResultSet(ResultSet resultSetLabel) {
        try {
            List<Label> labels = new ArrayList<>();
            while(resultSetLabel.next()) {
                labels.add(getLabelFromResultSet(resultSetLabel));
            }
            return labels;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    public boolean createLabel(Label label) {
        try {
            ResultSet resultSet = getNewIdLabelStatement.executeQuery();
            if(resultSet.next()) {
                label.setId(resultSet.getInt(1));
            } else {
                label.setId(1);
            }
            createLabelStatement.setInt(1, label.getId());
            createLabelStatement.setInt(2, label.getAccountId());
            createLabelStatement.setString(3, label.getLabelName());
            createLabelStatement.setString(4, label.getDescription());
            createLabelStatement.setString(5, label.getLabelColor().name());

            createLabelStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean updateLabel(Label label) {

        try {
            updateLabelStatement.setInt(4, label.getId());
            updateLabelStatement.setString(1, label.getLabelName());
            updateLabelStatement.setString(2, label.getDescription());
            updateLabelStatement.setString(3, label.getLabelColor().name());
            updateLabelStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return  false;
        }
        return true;
    }

    // ------------------------------------------------------------------------------- //

    // INTERTABLE
    private boolean deleteNotesFromIntertable(ResultSet resultSet, Note note) {
        return true;
    }

    private List<Label> getLabelListForNote(int noteId) {
        try {
            List<Label> labels = new ArrayList<>();
            getAllLabelsIdForNoteStatement.setInt(1, noteId);
            ResultSet resultSet = getAllLabelsIdForNoteStatement.executeQuery();
            while (resultSet.next()) {
                labels.add(getLabelFromId(resultSet.getInt(1)));
            }
            return labels;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    private void setLabelListForNote(int id, List<Label> labels) {
        for (Label label : labels) {
            try {
                addNewLabelForNoteStatement.setInt (1, id);
                addNewLabelForNoteStatement.setInt (2, label.getId ());
                addNewLabelForNoteStatement.executeUpdate ();
            } catch (SQLException throwables) {
                throwables.printStackTrace ();
            }
        }
    }

    // ------------------------------------------------------------------------------- //

    // NOTE
    private NoteColor stringToNoteColor(String string) {
        return NoteColor.valueOf(string);
    }

    private Note getNoteFromResultSet (ResultSet resultSetNotes) {
        try {
            Note note = new Note();
            note.setId(resultSetNotes.getInt(1));
            note.setGroupId(resultSetNotes.getInt(2));
            note.setNoteTitle(resultSetNotes.getString(3));
            note.setDescription(resultSetNotes.getString(4));
            note.setNoteColor(stringToNoteColor(resultSetNotes.getString(5)));

            note.setLabels (getLabelListForNote (note.getId ()));
            note.setImage (null);


            return note;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<Note> getNoteListFromResultSet(ResultSet resultSetNotes) {
        try {
            List<Note> notes = new ArrayList<>();
            while(resultSetNotes.next()) {
                notes.add(getNoteFromResultSet(resultSetNotes));
            }
            return notes;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Note> getAllNotesForGroup(int groupId) {
        try {
            getAllNotesForGroupStatement.setInt(1, groupId);
            return getNoteListFromResultSet(getAllNotesForGroupStatement.executeQuery());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Note> getAllNotesForLabel(int labelId) {
        try {
            getAllNotesForLabelStatement.setInt (1, labelId);
            return getNoteListFromResultSet (getAllNotesForLabelStatement.executeQuery ());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    public boolean createNote (Note note) {
        try {
            ResultSet resultSet = getNewIdNoteStatement.executeQuery();
            if(resultSet.next()) {
                note.setId(resultSet.getInt(1));
            } else {
                note.setId(1);
            }
            createNoteStatement.setInt(1, note.getId());
            createNoteStatement.setInt(2, note.getGroupId ());
            createNoteStatement.setString(3, note.getNoteTitle ());
            createNoteStatement.setString(4, note.getDescription ());
            createNoteStatement.setString(5, note.getNoteColor ().name ());
            createNoteStatement.setBytes (6, note.getImage ());

            setLabelListForNote (note.getId (), note.getLabels ());

            createNoteStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }
}
