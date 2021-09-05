package daltest;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dto.Account;
import ba.unsa.etf.rpr.projekat.dto.Label;
import ba.unsa.etf.rpr.projekat.dal.LabelModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LabelModelTest {
    private static DatabaseConnection databaseConnection;
    private static LabelModel labelModel;
    private static Account account;

    @BeforeAll
    static void setUp () {
        account = new Account (0, "firstname", "lastname",
                "username", "email@gmail.com", "Password10#");
        databaseConnection = DatabaseConnection.getInstance ();
        labelModel = DatabaseConnection.getInstance ().getLabelModel ();
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @AfterAll
    static void onEnd() {
        DatabaseConnection.removeInstance ();
    }

    @Test
    void testProperty() {
        // database empty
        databaseConnection.vratiNaDefault ();
        Label label1 = new Label (0, 0, "labelname", "labeldescription", "BLUE");
        // create labels
        labelModel.createLabel (label1);
        labelModel.createLabel (label1);
        labelModel.createLabel (label1);
        assertEquals (3, labelModel.getAllLabelsForAccount (account).size ());
        assertEquals (3, labelModel.getAllLabels ().size ());
    }

    @Test
    void testSorting() {
        // database empty
        databaseConnection.vratiNaDefault ();
        Label label1 = new Label (0, 0, "2 by name", "3 by description", "BLUE");
        Label label2 = new Label (0, 0, "3 by name", "1 by description", "BLUE");
        Label label3 = new Label (0, 0, "1 by name", "2 by description", "BLUE");
        // create labels
        labelModel.createLabel (label1); // id = 0
        labelModel.createLabel (label2); // id = 1
        labelModel.createLabel (label3); // id = 2

        // sort by date created
        labelModel.sortLabels (MyResourceBundle.getString ("LastAdded"));
        assertEquals (2, labelModel.getAllLabels ().get (0).getId ());
        labelModel.sortLabels (MyResourceBundle.getString ("FirstAdded"));
        assertEquals (0, labelModel.getAllLabels ().get (0).getId ());

        // sort by name
        labelModel.sortLabels (MyResourceBundle.getString ("ByNameAsc"));
        assertEquals (2, labelModel.getAllLabels ().get (0).getId ());
        labelModel.sortLabels (MyResourceBundle.getString ("ByNameDesc"));
        assertEquals (1, labelModel.getAllLabels ().get (0).getId ());

        // sort by description
        labelModel.sortLabels (MyResourceBundle.getString ("ByDescriptionAsc"));
        assertEquals (1, labelModel.getAllLabels ().get (0).getId ());
        labelModel.sortLabels (MyResourceBundle.getString ("ByDescriptionDesc"));
        assertEquals (0, labelModel.getAllLabels ().get (0).getId ());

    }

    @Test
    void labelIsCreating() {
        // database empty
        databaseConnection.vratiNaDefault ();

        Label label1 = new Label (0, 0, "labelname", "labeldescription", "BLUE");
        Label label2 = new Label (0, 0, "labelname", "labeldescription", "BLUE");

        labelModel.createLabel (label1);
        labelModel.createLabel (label2);

        // labels creating
        assertEquals (2, labelModel.getAllLabelsForAccount (account).size ());

        // createLabel should change label id
        assertEquals (0, label1.getId ());
        assertEquals (1, label2.getId ());

    }

    @Test
    void labelIsUpdating() {
        // database empty
        databaseConnection.vratiNaDefault ();
        Label label1 = new Label (0, 0, "labelname", "labeldescription", "BLUE");
        // create label before updating
        labelModel.createLabel (label1);
        assertEquals (1, labelModel.getAllLabelsForAccount (account).size ());
        // edit label
        label1.setLabelName ("newname");
        // update label
        labelModel.updateLabel (label1);
        // check if updated
        List<Label> lables = new ArrayList<> (labelModel.getAllLabelsForAccount (account));
        assertEquals ("Newname", lables.get (0).getLabelName ());
    }

    @Test
    void labelIsDeleting() {
        // database empty
        databaseConnection.vratiNaDefault ();
        Label label1 = new Label (0, 0, "labelname", "labeldescription", "BLUE");
        // create label before deleting
        labelModel.createLabel (label1);
        assertEquals (1, labelModel.getAllLabelsForAccount (account).size ());
        // delete label
        labelModel.deleteLabel (label1.getId ());
        // check if deleted
        assertEquals (0, labelModel.getAllLabelsForAccount (account).size ());
    }

}
