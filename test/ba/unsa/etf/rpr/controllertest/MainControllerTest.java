package ba.unsa.etf.rpr.controllertest;

import static org.junit.jupiter.api.Assertions.*;

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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.Locale;

@ExtendWith(ApplicationExtension.class)
class MainControllerTest {
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
        DatabaseConnection.setTesting (true);
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
    void checkIfAllThere1(FxRobot robot) {
        robot.clickOn ("#labelRadioButton");
        ListView listView2= robot.lookup("#labelListView").queryAs(ListView.class);
        assertEquals (3, listView2.getItems ().size ());

        robot.clickOn ("Label 1");
        FlowPane flowPane = robot.lookup ("#flowPaneForNotes").queryAs (FlowPane.class);
        assertEquals (1, flowPane.getChildren ().size ());

        robot.clickOn ("#groupRadioButton");
        ListView listView1 = robot.lookup("#groupListView").queryAs(ListView.class);
        assertEquals (3, listView1.getItems ().size ());

        robot.clickOn ("Group 1");
        flowPane = robot.lookup ("#flowPaneForNotes").queryAs (FlowPane.class);
        assertEquals (2, flowPane.getChildren ().size ());

    }

    @Test
    void checkIfAllThere2(FxRobot robot) {
        javafx.scene.control.Label label = robot.lookup ("#userNameLabel").queryAs (javafx.scene.control.Label.class);
        assertTrue (label.getText ().contains (account.getFirstName ()));
        assertTrue (label.getText ().contains (account.getLastName ()));
        label = robot.lookup ("#userEmailLabel").queryAs (javafx.scene.control.Label.class);
        assertTrue (label.getText ().contains (account.getEmailAdress ()));
        label = robot.lookup ("#userUsernameLabel").queryAs (javafx.scene.control.Label.class);
        assertTrue (label.getText ().contains (account.getUserName ()));
    }

    @Test
    void testSearchAndReset(FxRobot robot) {
        robot.clickOn ("#searchNotesField").write ("1");
        robot.clickOn ("#searchNottesButton");
        FlowPane flowPane = robot.lookup ("#flowPaneForNotes").queryAs (FlowPane.class);
        assertEquals (1, flowPane.getChildren ().size ());
        robot.clickOn ("#resetButton");
        assertEquals (0, flowPane.getChildren ().size ());
    }

    @Test
    void testSortGroups(FxRobot robot) {
        robot.clickOn ("#groupRadioButton");
        robot.clickOn ("#sortGroupLabelsChoiceBox").clickOn (MyResourceBundle.getString ("LastAdded"));
        ListView listView1 = robot.lookup("#groupListView").queryAs(ListView.class);
        assertEquals ("Group 3", ((Group)listView1.getItems ().get (0)).getGroupName ());
        robot.clickOn ("#labelRadioButton");
        ListView listView2= robot.lookup("#labelListView").queryAs(ListView.class);
        assertEquals ("Label 3", ((Label)listView2.getItems ().get (0)).getLabelName ());
    }

    @Test
    void testSortNotes(FxRobot robot) {
        robot.clickOn ("#groupRadioButton");
        robot.clickOn ("Group 1");
        robot.clickOn ("#sortNotesChoiceBox").clickOn (MyResourceBundle.getString ("FirstAdded"));
        FlowPane flowPane = robot.lookup ("#flowPaneForNotes").queryAs (FlowPane.class);
        assertEquals ("Note 1", ((javafx.scene.control.Label)
                ((GridPane) flowPane.getChildren ().get (0)).getChildren ().get (0)).getText ());

    }






}
