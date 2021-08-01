package ba.unsa.etf.rpr.projekat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

public class ProjectDAO {
    private static ProjectDAO instance = null;
    private static Connection connection;

    // ACCOUNT
    private PreparedStatement createAccountStatement, getAllAccountsStatement, updateFirstNameAccountStatement,
            updateLastNameAccountStatement, updateUserNameAccountStatement, updateEmailAccountStatement,
            updatePasswordAccountStatement, deleteAccountStatement, getNewIdAccountStatement;

    // GROUPS
    private PreparedStatement createGroupStatement, getAllGroupsForAccountStatement, deleteGroupStatement,
            updateGroupNameGroupStatement, updateDescriptionGroupStatement, updateGroupColorGroupStatement, getNewIdGroupStatement,
            deleteGroupsForAccountStatement;

    // LABELS
    private PreparedStatement createLabelStatement, getAllLabelsForAccountStatement, deleteLabelStatement,
            updateLabelNameLabelStatement, updateDescriptionLabelStatement, updateLabelColorLabelStatement,
            getNewIdLabelStatement, deleteLabelsForAccountStatement;

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
            getAllNotesForGroupStatement = connection.prepareStatement("SELECT * FROM notes WHERE groupId = ?");
        } catch (SQLException throwables) {
            createDatabase();
            try {
                // ACCOUNT
                createAccountStatement = connection.prepareStatement("INSERT INTO account (id, firstName, lastName," +
                        " userName, emailAdress, password) VALUES (?,?,?,?,?,?)");
                getAllAccountsStatement = connection.prepareStatement("SELECT * FROM account");
                updateFirstNameAccountStatement = connection.prepareStatement("UPDATE account SET firstName = ? WHERE id = ?");
                updateLastNameAccountStatement = connection.prepareStatement("UPDATE account SET lastName = ? WHERE id = ?");
                updateUserNameAccountStatement = connection.prepareStatement("UPDATE account SET userName = ? WHERE id = ?");
                updateEmailAccountStatement = connection.prepareStatement("UPDATE account SET emailAdress = ? WHERE id = ?");
                updatePasswordAccountStatement = connection.prepareStatement("UPDATE account SET password = ? WHERE id = ?");
                deleteAccountStatement = connection.prepareStatement("DELETE FROM account WHERE id = ?");
                getNewIdAccountStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM account");

                // GROUPS
                createGroupStatement = connection.prepareStatement("INSERT INTO group (id, accountId, groupName, " +
                        "description, groupColor) VALUES (?, ?, ?, ?, ?)");
                getAllGroupsForAccountStatement = connection.prepareStatement("SELECT * FROM groups WHERE accountId = ?");
                updateGroupNameGroupStatement = connection.prepareStatement("UPDATE group SET groupName = ? WHERE id = ?");
                updateDescriptionGroupStatement = connection.prepareStatement("UPDATE group SET description = ? WHERE id = ?");
                updateGroupColorGroupStatement = connection.prepareStatement("UPDATE group SET groupColor = ? WHERE id = ?");
                deleteGroupStatement = connection.prepareStatement("DELETE FROM group WHERE id = ?");
                deleteGroupsForAccountStatement = connection.prepareStatement("DELETE FROM group WHERE accountId = ?");
                getNewIdGroupStatement = connection.prepareStatement("SELECT MAX(id) + 1 FROM group");

                // LABELS
                createLabelStatement = connection.prepareStatement("INSERT INTO label (id, accountId, labelName, " +
                        "description, labelColor) VALUES (?, ?, ?, ?, ?)");
                getAllLabelsForAccountStatement = connection.prepareStatement("SELECT * FROM label WHERE accountId = ?");
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

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

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




}
