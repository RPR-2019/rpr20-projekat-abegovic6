package ba.unsa.etf.rpr.daltest;

import ba.unsa.etf.rpr.Utility;
import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dto.Account;
import ba.unsa.etf.rpr.projekat.dto.Group;
import ba.unsa.etf.rpr.projekat.dal.GroupModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class GroupModelTest {
    private static DatabaseConnection databaseConnection;
    private static GroupModel groupModel;
    private static Account account;

    @BeforeAll
    static void setUp () {
        account = Utility.getInstance ().getTestAccount ();
        databaseConnection = DatabaseConnection.getInstance ();
        DatabaseConnection.setTesting (true);
        databaseConnection.deleteTestUser (account);
        databaseConnection.getAccountModel ().createAccount (account);
        groupModel = DatabaseConnection.getInstance ().getGroupModel ();
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @BeforeEach
    void forEachTest() {
        databaseConnection.deleteTestUser (account);
        databaseConnection.getAccountModel ().createAccount (account);
    }

    @Test
    void testProperty() {
        // database empty
        Group group1 = new Group (0, account.getId (),
                "groupname", "groupdescription", "BLUE");
        // create groups
        groupModel.createGroup (group1);
        groupModel.createGroup (group1);
        groupModel.createGroup (group1);
        assertEquals (3, groupModel.getAllGroupsForAccount (account).size ());
        assertEquals (3, groupModel.getAllGroups ().size ());
        assertEquals (3, groupModel.getGroups ().size ());
    }

    @Test
    void testSorting() {
        // database empty;
        Group group1 = new Group (0, account.getId (), "2 by name", "3 by description", "BLUE");
        Group group2 = new Group (0, account.getId (), "3 by name", "1 by description", "BLUE");
        Group group3 = new Group (0, account.getId (), "1 by name", "2 by description", "BLUE");
        // create groups
        groupModel.createGroup (group1); // id = 0
        groupModel.createGroup (group2); // id = 1
        groupModel.createGroup (group3); // id = 2

        // sort by date created
        groupModel.sortGroups (MyResourceBundle.getString ("LastAdded"));
        assertEquals (2, groupModel.getAllGroups ().get (0).getId ());
        groupModel.sortGroups (MyResourceBundle.getString ("FirstAdded"));
        assertEquals (0, groupModel.getAllGroups ().get (0).getId ());

        // sort by name
        groupModel.sortGroups (MyResourceBundle.getString ("ByNameAsc"));
        assertEquals (2, groupModel.getAllGroups ().get (0).getId ());
        groupModel.sortGroups (MyResourceBundle.getString ("ByNameDesc"));
        assertEquals (1, groupModel.getAllGroups ().get (0).getId ());

        // sort by description
        groupModel.sortGroups (MyResourceBundle.getString ("ByDescriptionAsc"));
        assertEquals (1, groupModel.getAllGroups ().get (0).getId ());
        groupModel.sortGroups (MyResourceBundle.getString ("ByDescriptionDesc"));
        assertEquals (0, groupModel.getAllGroups ().get (0).getId ());

    }

    @Test
    void getNameFromId() {
        // database empty
        Group group1 = new Group (0, account.getId (), "first", "groupdescription", "BLUE");
        Group group2 = new Group (0, account.getId (), "second", "groupdescription", "BLUE");
        Group group3 = new Group (0, account.getId (), "third", "groupdescription", "BLUE");
        // create groups
        groupModel.createGroup (group1);
        groupModel.createGroup (group2);
        groupModel.createGroup (group3);
        // see if getNameForId works
        assertEquals ("First", groupModel.getNameFromId (0));
        assertEquals ("Second", groupModel.getNameFromId (1));
        assertEquals ("Third", groupModel.getNameFromId (2));
    }

    @Test
    void getIdFromName() {
        // database empty
        Group group1 = new Group (0, account.getId (), "first", "groupdescription", "BLUE");
        Group group2 = new Group (0, account.getId (),"second", "groupdescription", "BLUE");
        Group group3 = new Group (0, account.getId (), "third", "groupdescription", "BLUE");
        // create some groups
        groupModel.createGroup (group1);
        groupModel.createGroup (group2);
        groupModel.createGroup (group3);
        // see if getIdForName works
        assertEquals (0, groupModel.getIdFromName ("First"));
        assertEquals (1, groupModel.getIdFromName ("Second"));
        assertEquals (2, groupModel.getIdFromName ("Third"));
    }

    @Test
    void groupIsCreating() {
        // database empty
        Group group1 = new Group (0, account.getId (), "groupname", "groupdescription", "BLUE");
        Group group2 = new Group (0, account.getId (), "groupname", "groupdescription", "BLUE");

        groupModel.createGroup (group1);
        groupModel.createGroup (group2);
        // groups creating
        assertEquals (2, groupModel.getAllGroupsForAccount (account).size ());

        // createGroup should change group id
        assertEquals (0, group1.getId ());
        assertEquals (1, group2.getId ());

    }

    @Test
    void groupIsUpdating() {
        // database empty
        Group group1 = new Group (0, account.getId (), "groupname", "groupdescription", "BLUE");
        // create group before updating
        groupModel.createGroup (group1);
        assertEquals (1, groupModel.getAllGroupsForAccount (account).size ());
        // edit group
        group1.setGroupName ("newname");
        // update group
        groupModel.updateGroup (group1);
        // check if updated
        List<Group> groups = new ArrayList<> (groupModel.getAllGroupsForAccount (account));
        assertEquals ("Newname", groups.get (0).getGroupName ());
    }

    @Test
    void groupIsDeleting() {
        // database empty
        Group group1 = new Group (0, account.getId (), "groupname", "groupdescription", "BLUE");
        // create group before updating
        groupModel.createGroup (group1);
        assertEquals (1, groupModel.getAllGroupsForAccount (account).size ());
        // delete group
        groupModel.deleteGroup (group1.getId ());
        // check if deleted
        assertEquals (0, groupModel.getAllGroupsForAccount (account).size ());
    }



}
