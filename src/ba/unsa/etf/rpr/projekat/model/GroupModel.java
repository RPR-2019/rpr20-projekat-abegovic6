package ba.unsa.etf.rpr.projekat.model;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.ProjectDAO;
import ba.unsa.etf.rpr.projekat.javabean.Account;
import ba.unsa.etf.rpr.projekat.javabean.Group;
import ba.unsa.etf.rpr.projekat.javabean.GroupColor;
import ba.unsa.etf.rpr.projekat.javabean.Note;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GroupModel {

    private static GroupModel instance = null;

    private final PreparedStatement createGroupStatement;
    private final PreparedStatement getAllGroupsForAccountStatement;
    private final PreparedStatement deleteGroupStatement;
    private final PreparedStatement updateGroupStatement;
    private final PreparedStatement getNewIdGroupStatement;

    private ObservableList<Group> allGroups;
    private ObservableList<String> stringGroups;
    private SimpleStringProperty currentGroup;

    private GroupModel (Connection conn) throws SQLException {
        createGroupStatement = conn.prepareStatement("INSERT INTO groups (id, accountId, groupName, " +
                "description, groupColor) VALUES (?, ?, ?, ?, ?)");
        getAllGroupsForAccountStatement = conn.prepareStatement("SELECT * FROM groups WHERE accountId = ?");
        updateGroupStatement = conn.prepareStatement("UPDATE groups SET (groupName, description, groupColor) " +
                "= (?,?,?) WHERE id = ?");
        deleteGroupStatement = conn.prepareStatement("DELETE FROM groups WHERE id = ?");
        getNewIdGroupStatement = conn.prepareStatement("SELECT MAX(id) + 1 FROM groups");

        this.stringGroups = FXCollections.observableArrayList();
        this.allGroups = FXCollections.observableArrayList ();
        this.currentGroup = new SimpleStringProperty();
    }

    public static GroupModel getInstance(Connection conn) throws SQLException {
        if(instance == null) instance = new GroupModel (conn);
        return instance;
    }

    private Group getGroupFromAllGroups(int id) {
        return allGroups.stream ().filter (group -> group.getId () == id).findFirst ().get ();
    }

    private String getGroupFromStringGroups(int id) {
        Group group = getGroupFromAllGroups (id);
        return group.getGroupName ();
    }

    private GroupColor stringToGroupColor(String string) {
        return GroupColor.valueOf(string);
    }

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
            List<Group> groups = new ArrayList<> ();
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
        return Collections.emptyList();
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

            allGroups.add (group);
            stringGroups.remove (group.getGroupName ());

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

    public boolean deleteGroup(int id) {
        try {
            deleteGroupStatement.setInt (1, id);
            deleteGroupStatement.executeUpdate ();

            allGroups.remove (getGroupFromAllGroups (id));
            stringGroups.remove (getGroupFromStringGroups (id));

            NoteModel noteModel = ProjectDAO.getInstance ().getNoteModel ();
            List<Note> notes = noteModel.getNotesForGroup (id);
            for(Note note : notes) {
                noteModel.deleteNote (note.getId ());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace ();
            return false;
        }
        return true;

    }

    public SimpleStringProperty currentGroupProperty() {
        return currentGroup;
    }

    public void setCurrentGroup(String group) {
        this.currentGroup.set(group);
    }

    public ObservableList<String> getGroups() {
        return stringGroups;
    }

    public String getNameFromId(int id) {
        return allGroups.stream().filter(g -> g.getId() == id).findFirst().get().getGroupName();
    }

    public int getIdFromName(String name) {
        return allGroups.stream().filter(g -> g.getGroupName().equals(name)).findFirst().get().getId();
    }

    public ObservableList<Group> getAllGroups () {
        return allGroups;
    }

    public void sortGroups (String sort) {
        if(sort.equals (MyResourceBundle.getString ("LastAdded"))) {
            allGroups.sort (Comparator.comparingInt (Group::getId).reversed ());
        } else if (sort.equals (MyResourceBundle.getString ("FirstAdded"))) {
            allGroups.sort (Comparator.comparingInt (Group::getId));
        } else if (sort.equals (MyResourceBundle.getString ("ByNameAsc"))) {
            allGroups.sort (Comparator.comparing(Group::getGroupName));
        } else if (sort.equals (MyResourceBundle.getString ("ByNameDesc"))) {
            allGroups.sort (Comparator.comparing(Group::getGroupName).reversed ());
        } else if (sort.equals (MyResourceBundle.getString ("ByDescriptionAsc"))) {
            allGroups.sort (Comparator.comparing(Group::getDescription));
        } else  {
            allGroups.sort (Comparator.comparing(Group::getDescription).reversed ());
        }
    }

}
