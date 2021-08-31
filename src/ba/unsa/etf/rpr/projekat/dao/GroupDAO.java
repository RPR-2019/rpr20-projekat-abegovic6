package ba.unsa.etf.rpr.projekat.dao;

import ba.unsa.etf.rpr.projekat.model.Account;
import ba.unsa.etf.rpr.projekat.model.Group;
import ba.unsa.etf.rpr.projekat.model.GroupColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GroupDAO {

    private static GroupDAO instance = null;

    private final PreparedStatement createGroupStatement;
    private final PreparedStatement getAllGroupsForAccountStatement;
    private final PreparedStatement deleteGroupStatement;
    private final PreparedStatement updateGroupStatement;
    private final PreparedStatement getNewIdGroupStatement;

    private GroupDAO (Connection conn) throws SQLException {
        createGroupStatement = conn.prepareStatement("INSERT INTO groups (id, accountId, groupName, " +
                "description, groupColor) VALUES (?, ?, ?, ?, ?)");
        getAllGroupsForAccountStatement = conn.prepareStatement("SELECT * FROM groups WHERE accountId = ?");
        updateGroupStatement = conn.prepareStatement("UPDATE groups SET (groupName, description, groupColor) " +
                "= (?,?,?) WHERE id = ?");
        deleteGroupStatement = conn.prepareStatement("DELETE FROM groups WHERE id = ?");
        getNewIdGroupStatement = conn.prepareStatement("SELECT MAX(id) + 1 FROM groups");
    }

    public static GroupDAO getInstance(Connection conn) throws SQLException {
        if(instance == null) instance = new GroupDAO (conn);
        return instance;
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
        } catch (SQLException throwables) {
            throwables.printStackTrace ();
            return false;
        }
        return true;

    }

}
