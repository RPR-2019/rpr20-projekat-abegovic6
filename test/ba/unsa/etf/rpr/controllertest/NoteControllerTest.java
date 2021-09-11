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
import javafx.scene.control.TextField;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class NoteControllerTest {

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
    void createFail(FxRobot robot) {
        robot.clickOn ("#newNote");
        javafx.scene.control.Label label = robot.lookup ("#noteTitleLabel").queryAs (javafx.scene.control.Label.class);
        assertEquals (MyResourceBundle.getString ("CreateANewNote"), label.getText ());

        robot.clickOn ("#noteOkButton");

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#noteNameTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#noteNameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        robot.closeCurrentWindow ();
    }

    @Test
    void createAndDelete(FxRobot robot) {
        robot.clickOn ("#groupRadioButton");
        robot.clickOn ("Group 1");

        FlowPane flowPane = robot.lookup("#flowPaneForNotes").queryAs(FlowPane.class);
        assertEquals (2, flowPane.getChildren ().size ());

        robot.clickOn ("#newNote");
        javafx.scene.control.Label label = robot.lookup ("#noteTitleLabel").queryAs (javafx.scene.control.Label.class);
        assertEquals (MyResourceBundle.getString ("CreateANewNote"), label.getText ());

        robot.clickOn ("#noteNameTextField").write ("Note 4");
        robot.clickOn ("#noteDescriptionTextArea").write ("New note created");
        robot.clickOn ("#noteColorChoiceBox").clickOn ("ORANGE");
        robot.clickOn ("#noteGroupChoiceBox").clickOn ("Group 1");
        robot.clickOn ("#noteOkButton");

        robot.clickOn ("#groupRadioButton");
        robot.clickOn ("Group 1");

        flowPane = robot.lookup("#flowPaneForNotes").queryAs(FlowPane.class);
        assertEquals (3, flowPane.getChildren ().size ());

        assertTrue (flowPane.getChildren ().stream ()
                .anyMatch (n -> ((javafx.scene.control.Label)((GridPane)n).getChildren ().get (0)).getText ().equals ("Note 4")));

        robot.clickOn ("Note 4");

        robot.clickOn ("#edit");
        robot.clickOn ("Delete");
        robot.clickOn ("OK");

        flowPane = robot.lookup("#flowPaneForNotes").queryAs(FlowPane.class);
        assertEquals (2, flowPane.getChildren ().size ());

        robot.closeCurrentWindow ();
    }

    @Test
    void updateNote(FxRobot robot) {
        robot.clickOn ("#labelRadioButton");
        robot.clickOn ("Label 2");

        FlowPane flowPane = robot.lookup("#flowPaneForNotes").queryAs(FlowPane.class);
        assertEquals (0, flowPane.getChildren ().size ());

        robot.clickOn ("#groupRadioButton");
        robot.clickOn ("Group 1");

        flowPane = robot.lookup("#flowPaneForNotes").queryAs(FlowPane.class);
        assertEquals (2, flowPane.getChildren ().size ());

        robot.clickOn ("Note 2");

        robot.clickOn ("#edit");
        robot.clickOn ("Edit note");
        robot.clickOn ("Label 2");
        robot.clickOn ("#noteOkButton");

        robot.clickOn ("#labelRadioButton");
        robot.clickOn ("Label 2");

        flowPane = robot.lookup("#flowPaneForNotes").queryAs(FlowPane.class);
        assertEquals (1, flowPane.getChildren ().size ());

        robot.clickOn ("Note 2");

        robot.clickOn ("#edit");
        robot.clickOn ("Edit note");
        robot.clickOn ("Label 2");
        robot.clickOn ("#noteOkButton");
        robot.lookup ("NOTA");

        flowPane = robot.lookup("#flowPaneForNotes").queryAs(FlowPane.class);
        assertEquals (0, flowPane.getChildren ().size ());

        robot.closeCurrentWindow ();
    }

}
