package ba.unsa.etf.rpr.controllertest;

import ba.unsa.etf.rpr.Utility;
import ba.unsa.etf.rpr.projekat.controller.MainController;
import ba.unsa.etf.rpr.projekat.dal.AccountModel;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dto.Account;
import ba.unsa.etf.rpr.projekat.dto.Group;
import ba.unsa.etf.rpr.projekat.dto.Label;
import ba.unsa.etf.rpr.projekat.dto.Note;
import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class LabelControllerTest {
    private static Account account;

    @Start
    public void start (Stage stage) throws Exception {
        MyResourceBundle.setLocale (Locale.getDefault());
        AccountModel.setCurrentUser (account);
        MainController mainController = new MainController ();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"),
                MyResourceBundle.getResourceBundle ());
        loader.setController(mainController);
        stage.setTitle(MyResourceBundle.getString("NotesTitle"));
        stage.setScene(new Scene (loader.load(), 1100, 600));
        stage.setMinHeight(600);
        stage.setMinWidth(1100);
        stage.show ();
        stage.toFront ();
    }

    @BeforeAll
    static void setUp() {
        account = Utility.getInstance ().getTestAccount ();
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance ();
        databaseConnection.deleteTestUser (account);
        databaseConnection.getAccountModel ().createAccount (account);

        Group group1 = new Group (0, account.getId (), "Group 1", "description", "BLUE");
        Group group2 = new Group (0, account.getId (), "Group 2", "description", "RED");
        Group group3 = new Group (0, account.getId (), "Group 3", "description", "ORANGE");

        databaseConnection.getGroupModel ().createGroup (group1);
        databaseConnection.getGroupModel ().createGroup (group2);
        databaseConnection.getGroupModel ().createGroup (group3);

        Label label1 = new Label (0, account.getId (), "Label 1", "description", "COPPER");
        Label label2 = new Label (0, account.getId (),  "Label 2", "description", "BLUE");
        Label label3 = new Label (0, account.getId (), "Label 3", "description", "PURPLE");

        databaseConnection.getLabelModel ().createLabel (label1);
        databaseConnection.getLabelModel ().createLabel (label2);
        databaseConnection.getLabelModel ().createLabel (label3);

        Note note1 = new Note (0, group1.getId (), "Note 1", "notedescription", "PURPLE");
        Note note2 = new Note (0, group1.getId (), "Note 2", "notedescription", "BLUE");
        Note note3 = new Note (0, group2.getId (), "Note 3", "notedescription", "BLUE");

        note1.getLabels ().add (label1);

        databaseConnection.getNoteModel ().createNote (note1);
        databaseConnection.getNoteModel ().createNote (note2);
        databaseConnection.getNoteModel ().createNote (note3);
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @Test
    void createFail(FxRobot robot) {
        robot.clickOn ("#newLabel");
        javafx.scene.control.Label label = robot.lookup ("#labelTitleLabel").queryAs (javafx.scene.control.Label.class);
        assertEquals (MyResourceBundle.getString ("CreateANewLabel"), label.getText ());

        robot.clickOn ("#labelOkButton");

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#labelNameTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#labelNameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        robot.closeCurrentWindow ();
    }

    @Test
    void createAndDelete(FxRobot robot) {
        robot.clickOn ("#labelRadioButton");
        ListView listView1 = robot.lookup("#labelListView").queryAs(ListView.class);
        assertEquals (3, listView1.getItems ().size ());

        robot.clickOn ("#newLabel");
        javafx.scene.control.Label label = robot.lookup ("#labelTitleLabel").queryAs (javafx.scene.control.Label.class);
        assertEquals (MyResourceBundle.getString ("CreateANewLabel"), label.getText ());

        robot.clickOn ("#labelNameTextField").write ("Label 4");
        robot.clickOn ("#labelDescriptionTextField").write ("New label created");
        robot.clickOn ("#labelColorChoiceBox").clickOn ("RED");
        robot.clickOn ("#labelOkButton");

        listView1 = robot.lookup("#labelListView").queryAs(ListView.class);
        assertEquals (4, listView1.getItems ().size ());

        assertTrue (listView1.getItems ().stream ().anyMatch (g -> ((Label)g).getLabelName ().equals ("Label 4")));
        int id = DatabaseConnection.getInstance ().getLabelModel ().getIdFromName ("Label 4");
        robot.clickOn ("#label" + id);

        robot.clickOn ("#edit");
        robot.clickOn ("Delete");
        robot.clickOn ("OK");

        listView1 = robot.lookup("#labelListView").queryAs(ListView.class);
        assertEquals (3, listView1.getItems ().size ());

        robot.closeCurrentWindow ();
    }

    @Test
    void updateGroup(FxRobot robot) {
        robot.clickOn ("#labelRadioButton");
        int id = DatabaseConnection.getInstance ().getLabelModel ().getIdFromName ("Label 3");
        robot.clickOn ("#label" + id);

        robot.clickOn ("#edit");
        robot.clickOn ("Edit label");

        robot.clickOn ("#labelNameTextField").press (KeyCode.DELETE);
        robot.clickOn ("#labelNameTextField").write ("4");
        robot.clickOn ("Ok");

        ListView listView1 = robot.lookup("#labelListView").queryAs(ListView.class);
        assertEquals (3, listView1.getItems ().size ());
        assertTrue (listView1.getItems ().stream ().anyMatch (g -> ((Label)g).getLabelName ().equals ("Label 4")));
        robot.clickOn ("#label" + id);

        robot.clickOn ("#edit");
        robot.clickOn ("Edit label");

        robot.clickOn ("#labelNameTextField").press (KeyCode.DELETE);
        robot.clickOn ("#labelNameTextField").write ("3");
        robot.clickOn ("Ok");

        listView1 = robot.lookup("#labelListView").queryAs(ListView.class);
        assertEquals (3, listView1.getItems ().size ());
        assertTrue (listView1.getItems ().stream ().anyMatch (g -> ((Label)g).getLabelName ().equals ("Label 3")));

        robot.closeCurrentWindow ();
    }
}
