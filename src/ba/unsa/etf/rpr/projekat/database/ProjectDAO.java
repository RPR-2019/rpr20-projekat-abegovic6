package ba.unsa.etf.rpr.projekat.database;


import ba.unsa.etf.rpr.projekat.model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ProjectDAO {
    private static ProjectDAO instance = null;
    private static Connection connection;

    // ACCOUNT
    private PreparedStatement createAccountStatement, getAllAccountsStatement, updateFirstNameAccountStatement,
            updateLastNameAccountStatement, updateUserNameAccountStatement, updateEmailAccountStatement,
            updatePasswordAccountStatement, deleteAccountStatement, getNewIdAccountStatement,
            emailAdressUniqueStatement, usernameUniqueStatement;

    // GROUPS
    private PreparedStatement createGroupStatement, getAllGroupsForAccountStatement, deleteGroupStatement,
            updateGroupNameGroupStatement, updateDescriptionGroupStatement, updateGroupColorGroupStatement, getNewIdGroupStatement,
            deleteGroupsForAccountStatement;

    // LABELS
    private PreparedStatement createLabelStatement, getAllLabelsForAccountStatement, deleteLabelStatement,
            updateLabelNameLabelStatement, updateDescriptionLabelStatement, updateLabelColorLabelStatement,
            getNewIdLabelStatement, deleteLabelsForAccountStatement, getLabelFromIdStatement;

    // INTERTABLE
    private PreparedStatement getAllLabelsIdForNoteStatement, getAllNotesIdForNoteStatement, addNewLabelForNoteStatement,
            removeLAbelFromNoteStatement, deleteNoteIdStatement, deleteLabelIdStatement;

    // NOTES
    private PreparedStatement getAllNotesForGroupStatement, createNoteStatement, updateGroupIdNoteStatement, updateNoteTitleNoteStatement,
            deleteNoteStatement, updateDescriptionNoteStatement, updateDateUpdatedNoteStatement, updateNoteColorNoteStatement,
            getNewIdNoteStatement, deleteNotesForGroupStatement;

    private ProjectDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:projectdatabase.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            getAllAccountsStatement = connection.prepareStatement("SELECT * FROM account");
            createAccountStatement = connection.prepareStatement("INSERT INTO account (id, firstName, lastName," +
                    " userName, emailAdress, password) VALUES (?,?,?,?,?,?)");
            updateFirstNameAccountStatement = connection.prepareStatement("UPDATE account SET firstName = ? WHERE id = ?");
            updateLastNameAccountStatement = connection.prepareStatement("UPDATE account SET lastName = ? WHERE id = ?");
            updateUserNameAccountStatement = connection.prepareStatement("UPDATE account SET userName = ? WHERE id = ?");
            updateEmailAccountStatement = connection.prepareStatement("UPDATE account SET emailAdress = ? WHERE id = ?");
            updatePasswordAccountStatement = connection.prepareStatement("UPDATE account SET password = ? WHERE id = ?");
            deleteAccountStatement = connection.prepareStatement("DELETE FROM account WHERE id = ?");
            getNewIdAccountStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM account");
            usernameUniqueStatement = connection.prepareStatement("SELECT COUNT(id) FROM account WHERE userName = ?");
            emailAdressUniqueStatement = connection.prepareStatement("SELECT COUNT(id) FROM account WHERE emailAdress = ?");

            // GROUPS
            /*createGroupStatement = connection.prepareStatement("INSERT INTO group (id, accountId, groupName, " +
                  "description, groupColor) VALUES (?, ?, ?, ?, ?)");
            getAllGroupsForAccountStatement = connection.prepareStatement("SELECT * FROM group WHERE accountId = ?");
            updateGroupNameGroupStatement = connection.prepareStatement("UPDATE group SET groupName = ? WHERE id = ?");
            updateDescriptionGroupStatement = connection.prepareStatement("UPDATE group SET description = ? WHERE id = ?");
            updateGroupColorGroupStatement = connection.prepareStatement("UPDATE group SET groupColor = ? WHERE id = ?");
            deleteGroupStatement = connection.prepareStatement("DELETE FROM group WHERE id = ?");
            deleteGroupsForAccountStatement = connection.prepareStatement("DELETE FROM group WHERE accountId = ?");
            getNewIdGroupStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM group");*/

            // LABELS
            createLabelStatement = connection.prepareStatement("INSERT INTO label (id, accountId, labelName, " +
                    "description, labelColor) VALUES (?, ?, ?, ?, ?)");
            getAllLabelsForAccountStatement = connection.prepareStatement("SELECT * FROM label WHERE accountId = ?");
            getLabelFromIdStatement = connection.prepareStatement("SELECT * FROM label WHERE id = ?");
            updateLabelNameLabelStatement = connection.prepareStatement("UPDATE label SET labelName = ? WHERE id = ?");
            updateLabelColorLabelStatement = connection.prepareStatement("UPDATE label SET labelColor = ? WHERE id = ?");
            updateDescriptionLabelStatement = connection.prepareStatement("UPDATE label SET description = ? WHERE id = ?");
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
            createNoteStatement = connection.prepareStatement("INSERT INTO note (id, groupId, noteTitle, " +
                    "description, dateCreated, dateUpdated, noteColor) VALUES (?, ?, ?, ?, ?, ?, ?)");
            updateNoteTitleNoteStatement = connection.prepareStatement("UPDATE notes SET noteTitle = ? WHERE id = ?");
            updateGroupIdNoteStatement = connection.prepareStatement("UPDATE notes SET groupId = ? WHERE id = ?");
            updateNoteColorNoteStatement = connection.prepareStatement("UPDATE notes SET noteColor = ? WHERE id = ?");
            updateDateUpdatedNoteStatement = connection.prepareStatement("UPDATE notes SET dateUpdated = ? WHERE id = ?");
            updateDescriptionNoteStatement = connection.prepareStatement("UPDATE notes SET description = ? WHERE id = ?");
            getAllNotesForGroupStatement = connection.prepareStatement("SELECT * FROM notes WHERE groupId = ?");
            deleteNoteStatement = connection.prepareStatement("DELETE FROM notes WHERE id = ?");
            deleteNotesForGroupStatement = connection.prepareStatement("DELETE FROM notes WHERE groupId = ?");
            getNewIdNoteStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM notes");
        } catch (SQLException throwables) {
            createDatabase();
            try {
                // ACCOUNT
                getAllAccountsStatement = connection.prepareStatement("SELECT * FROM account");
                createAccountStatement = connection.prepareStatement("INSERT INTO account (id, firstName, lastName," +
                        " userName, emailAdress, password) VALUES (?,?,?,?,?,?)");

                updateFirstNameAccountStatement = connection.prepareStatement("UPDATE account SET firstName = ? WHERE id = ?");
                updateLastNameAccountStatement = connection.prepareStatement("UPDATE account SET lastName = ? WHERE id = ?");
                updateUserNameAccountStatement = connection.prepareStatement("UPDATE account SET userName = ? WHERE id = ?");
                updateEmailAccountStatement = connection.prepareStatement("UPDATE account SET emailAdress = ? WHERE id = ?");
                updatePasswordAccountStatement = connection.prepareStatement("UPDATE account SET password = ? WHERE id = ?");
                deleteAccountStatement = connection.prepareStatement("DELETE FROM account WHERE id = ?");
                getNewIdAccountStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM account");
                usernameUniqueStatement = connection.prepareStatement("SELECT COUNT(id) FROM account WHERE userName = ?");
                emailAdressUniqueStatement = connection.prepareStatement("SELECT COUNT(id) FROM account WHERE emailAdress = ?");

                // GROUPS
                /*createGroupStatement = connection.prepareStatement("INSERT INTO group (id, accountId, groupName, description, groupColor) VALUES (?,?,?,?,?)");
                getAllGroupsForAccountStatement = connection.prepareStatement("SELECT * FROM group WHERE accountId = ?");
                updateGroupNameGroupStatement = connection.prepareStatement("UPDATE group SET groupName = ? WHERE id = ?");
                updateDescriptionGroupStatement = connection.prepareStatement("UPDATE group SET description = ? WHERE id = ?");
                updateGroupColorGroupStatement = connection.prepareStatement("UPDATE group SET groupColor = ? WHERE id = ?");
                deleteGroupStatement = connection.prepareStatement("DELETE FROM group WHERE id = ?");
                deleteGroupsForAccountStatement = connection.prepareStatement("DELETE FROM group WHERE accountId = ?");
                getNewIdGroupStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM group");*/

                // LABELS
                createLabelStatement = connection.prepareStatement("INSERT INTO label (id, accountId, labelName, " +
                        "description, labelColor) VALUES (?, ?, ?, ?, ?)");
                getAllLabelsForAccountStatement = connection.prepareStatement("SELECT * FROM label WHERE accountId = ?");
                getLabelFromIdStatement = connection.prepareStatement("SELECT * FROM label WHERE id = ?");
                updateLabelNameLabelStatement = connection.prepareStatement("UPDATE label SET labelName = ? WHERE id = ?");
                updateLabelColorLabelStatement = connection.prepareStatement("UPDATE label SET labelColor = ? WHERE id = ?");
                updateDescriptionLabelStatement = connection.prepareStatement("UPDATE label SET description = ? WHERE id = ?");
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
                        "description, dateCreated, dateUpdated, noteColor) VALUES (?, ?, ?, ?, ?, ?, ?)");
                updateNoteTitleNoteStatement = connection.prepareStatement("UPDATE notes SET noteTitle = ? WHERE id = ?");
                updateGroupIdNoteStatement = connection.prepareStatement("UPDATE notes SET groupId = ? WHERE id = ?");
                updateNoteColorNoteStatement = connection.prepareStatement("UPDATE notes SET noteColor = ? WHERE id = ?");
                updateDateUpdatedNoteStatement = connection.prepareStatement("UPDATE notes SET dateUpdated = ? WHERE id = ?");
                updateDescriptionNoteStatement = connection.prepareStatement("UPDATE notes SET description = ? WHERE id = ?");
                getAllNotesForGroupStatement = connection.prepareStatement("SELECT * FROM notes WHERE groupId = ?");
                deleteNoteStatement = connection.prepareStatement("DELETE FROM notes WHERE id = ?");
                deleteNotesForGroupStatement = connection.prepareStatement("DELETE FROM notes WHERE groupId = ?");
                getNewIdNoteStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM notes");

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
        stmt.executeUpdate("DELETE FROM group");
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
            if(resultSetAccount.next()) {
                account.setId(resultSetAccount.getInt(1));
                account.setFirstName(resultSetAccount.getString(2));
                account.setLastName(resultSetAccount.getString(3));
                account.setUserName(resultSetAccount.getString(4));
                account.setEmailAdress(resultSetAccount.getString(5));
                account.setPassword(resultSetAccount.getString(6));
            }
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
                resultSetAccountList.previous();
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

    public boolean isEmailUnique (String emailAdress) {
        try {
            emailAdressUniqueStatement.setString(1, emailAdress);
            ResultSet resultSet = emailAdressUniqueStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean isUsernameUnique (String username) {
        try {
            usernameUniqueStatement.setString(1, username);
            ResultSet resultSet = usernameUniqueStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean deleteAccount(ResultSet resultSet, Account account) {
        return true;
    }

    // ------------------------------------------------------------------------------- //

    // GROUP
    private GroupColor stringToGroupColor(String string) {
        return GroupColor.valueOf(string);
    }

    private Group getGroupFromResultSet(ResultSet resultSetGroup) {
        try {
            Group group = new Group();
            if(resultSetGroup.next()) {
                group.setId(resultSetGroup.getInt(1));
                group.setAccountId(resultSetGroup.getInt(2));
                group.setGroupName(resultSetGroup.getString(3));
                group.setDescription(resultSetGroup.getString(4));
                group.setGroupColor(stringToGroupColor(resultSetGroup.getString(5)));
            }
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
                resultSetGroupList.previous();
                groups.add(getGroupFromResultSet(resultSetGroupList));
            }
            return  groups;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    // ------------------------------------------------------------------------------- //

    // LABEL
    private LabelColor stringToLabelColor(String string) {
        return LabelColor.valueOf(string);
    }

    private Label getLabelFromResultSet(ResultSet resultSetLabel) {
        try {
            Label label = new Label();
            if(resultSetLabel.next()) {
                label.setId(resultSetLabel.getInt(1));
                label.setAccountId(resultSetLabel.getInt(2));
                label.setLabelName(resultSetLabel.getString(3));
                label.setDescription(resultSetLabel.getString(4));
                label.setLabelColor(stringToLabelColor(resultSetLabel.getString(5)));
            }
            return label;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private Label getLabelFromId(int labelId) {
        try {
            getLabelFromIdStatement.setInt(1, labelId);
            return getLabelFromResultSet(getLabelFromIdStatement.executeQuery());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    private List<Label> getLabelListFromResultSet(ResultSet resultSetLabel) {
        try {
            List<Label> labels = new ArrayList<>();
            while(resultSetLabel.next()) {
                resultSetLabel.previous();
                labels.add(getLabelFromResultSet(resultSetLabel));
            }
            return labels;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    // ------------------------------------------------------------------------------- //

    // INTERTABLE
    public boolean deleteNotesFromIntertable(ResultSet resultSet, Note note) {
        return true;
    }

    public List<Label> getLabelListForNote(int noteId) {
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

    // ------------------------------------------------------------------------------- //

    // NOTE
    private LocalDateTime stringToLocalDateTime(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(string, formatter);
    }

    private String localeDateTimeToString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return date.format(formatter);
    }

    private NoteColor stringToNoteColor(String string) {
        return NoteColor.valueOf(string);
    }

    private Note getNoteFromResultSet (ResultSet resultSetNotes) {
        try {
            Note note = new Note();
            if(resultSetNotes.next()) {
                note.setId(resultSetNotes.getInt(1));
                note.setGroupId(resultSetNotes.getInt(2));
                note.setNoteTitle(resultSetNotes.getString(3));
                note.setDescription(resultSetNotes.getString(4));
                note.setDateCreated(stringToLocalDateTime(resultSetNotes.getString(5)));
                note.setDateUpdated(stringToLocalDateTime(resultSetNotes.getString(6)));
                note.setNoteColor(stringToNoteColor(resultSetNotes.getString(7)));
                note.setLabels(getLabelListForNote(resultSetNotes.getInt(1)));
            }
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
                resultSetNotes.previous();
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

}
