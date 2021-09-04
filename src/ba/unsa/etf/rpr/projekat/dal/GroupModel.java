package ba.unsa.etf.rpr.projekat.dal;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dto.Account;
import ba.unsa.etf.rpr.projekat.dto.Group;
import ba.unsa.etf.rpr.projekat.dto.GroupColor;
import ba.unsa.etf.rpr.projekat.dto.Note;
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

    private final ObservableList<Group> allGroups;
    private final ObservableList<String> stringGroups;
    private final SimpleStringProperty currentGroup;

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
        var optional = allGroups.stream ().filter (group -> group.getId () == id).findFirst ();
        return optional.orElse (null);
    }

    private String getGroupFromStringGroups(int id) {
        Group group = getGroupFromAllGroups (id);
        if(group == null) return "";
        else return group.getGroupName ();
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

    public void createGroup(Group group) {
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
            stringGroups.add (group.getGroupName ());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    public void updateGroup(Group group) {
        try {
            updateGroupStatement.setInt(4, group.getId());
            updateGroupStatement.setString(1, group.getGroupName());
            updateGroupStatement.setString(2, group.getDescription());
            updateGroupStatement.setString(3, group.getGroupColor().name());
            updateGroupStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void deleteGroup(int id) {
        NoteModel noteModel = DatabaseConnection.getInstance ().getNoteModel ();
        try {
            for(Note note : noteModel.getAllNotesForGroup (id)) {
                noteModel.deleteNote (note.getId ());
            }
            deleteGroupStatement.setInt (1, id);
            deleteGroupStatement.executeUpdate ();
            stringGroups.remove (getGroupFromStringGroups (id));
        } catch (SQLException throwables) {
            throwables.printStackTrace ();
        }

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
        var optional = allGroups.stream().filter(g -> g.getId() == id).findFirst();
        if(optional.isEmpty ()) return "";
        else return  optional.get().getGroupName();
    }

    public int getIdFromName(String name) {
        var optional = allGroups.stream().filter(g -> g.getGroupName().equals(name)).findFirst();
        if(optional.isEmpty ()) return 0;
        else return  optional.get().getId ();
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
