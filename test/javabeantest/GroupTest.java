package javabeantest;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.javabean.Group;
import ba.unsa.etf.rpr.projekat.javabean.GroupColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {
    private static Group group;

    @BeforeAll
    static void setUp () {
        group = new Group ();
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @Test
    void groupIsEmpty() {
        group = new Group ();
        assertEquals (0, group.getId ());
        assertEquals (0, group.getAccountId ());
        assertNull (group.getGroupName ());
        assertNull (group.getDescription ());
        assertNull (group.getGroupColor ());
    }

    @Test
    void secondConstructorTest() {
        group = new Group (0, 0, "groupname", "groupdescription", "BLUE");
        assertEquals (0, group.getId ());
        assertEquals (0, group.getAccountId ());
        assertEquals ("Groupname", group.getGroupName ());
        assertEquals ("groupdescription", group.getDescription ());
        assertEquals (GroupColor.BLUE, group.getGroupColor ());
    }




}
