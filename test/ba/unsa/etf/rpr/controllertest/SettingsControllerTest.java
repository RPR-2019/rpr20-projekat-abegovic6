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

@ExtendWith(ApplicationExtension.class)
class SettingsControllerTest {

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
    void openWithButton(FxRobot robot) {
        robot.clickOn ("#settingsButton");
        assertEquals (account.getFirstName (), robot.lookup ("#firstNameSignUpTextField").queryAs (TextField.class).getText ());
        assertEquals (account.getLastName (), robot.lookup ("#lastNameSignUpTextField").queryAs (TextField.class).getText ());
        assertEquals (account.getEmailAdress (), robot.lookup ("#emailAdressSignUpTextField").queryAs (TextField.class).getText ());
        assertEquals (account.getUserName (), robot.lookup ("#usernameSignUpTextField").queryAs (TextField.class).getText ());
        assertEquals (account.getPassword (), robot.lookup ("#passwordSignUpPasswordField").queryAs (TextField.class).getText ());
        assertEquals (account.getPassword (), robot.lookup ("#repeatPasswordSignUpPasswordField").queryAs (TextField.class).getText ());

        robot.closeCurrentWindow ();
    }

    @Test
    void openWithMenu(FxRobot robot) {
        robot.clickOn ("#file").clickOn ("Settings");
        assertEquals (account.getFirstName (), robot.lookup ("#firstNameSignUpTextField").queryAs (TextField.class).getText ());
        assertEquals (account.getLastName (), robot.lookup ("#lastNameSignUpTextField").queryAs (TextField.class).getText ());
        assertEquals (account.getEmailAdress (), robot.lookup ("#emailAdressSignUpTextField").queryAs (TextField.class).getText ());
        assertEquals (account.getUserName (), robot.lookup ("#usernameSignUpTextField").queryAs (TextField.class).getText ());
        assertEquals (account.getPassword (), robot.lookup ("#passwordSignUpPasswordField").queryAs (TextField.class).getText ());
        assertEquals (account.getPassword (), robot.lookup ("#repeatPasswordSignUpPasswordField").queryAs (TextField.class).getText ());

        robot.closeCurrentWindow ();
    }

    @Test
    void testLanguage(FxRobot robot) {
        robot.clickOn ("#settingsButton");
        robot.clickOn ("#bosnian");

        assertEquals ("Postavke", robot.lookup ("#title").queryAs (javafx.scene.control.Label.class).getText ());
        robot.clickOn ("#buttonOK");
        assertEquals ("Sortiraj:", robot.lookup ("#sortLabel").queryAs (javafx.scene.control.Label.class).getText ());

        robot.clickOn ("#settingsButton");
        robot.clickOn ("#english");

        assertEquals ("Settings", robot.lookup ("#title").queryAs (javafx.scene.control.Label.class).getText ());
        robot.clickOn ("#buttonOK");
        assertEquals ("Sort:", robot.lookup ("#sortLabel").queryAs (javafx.scene.control.Label.class).getText ());

        robot.closeCurrentWindow ();
    }

    @Test
    void updateAccountFail1(FxRobot robot) {
        robot.clickOn ("#settingsButton");

        TextField fname = robot.lookup ("#firstNameSignUpTextField").queryAs (TextField.class);
        fname.setText ("");
        fname = robot.lookup ("#lastNameSignUpTextField").queryAs (TextField.class);
        fname.setText ("");
        robot.clickOn ("#buttonOK");

        robot.clickOn ("OK");

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#firstNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#firstNameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#lastNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#lastNameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#usernameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#usernameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#emailAdressSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#emailErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#passwordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#passwordErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#repeatPasswordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#repeatPasswordErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        robot.closeCurrentWindow ();
    }

    @Test
    void updateAccountFail2(FxRobot robot) {
        robot.clickOn ("#settingsButton");

        TextField fname = robot.lookup ("#usernameSignUpTextField").queryAs (TextField.class);
        fname.setText ("user");
        fname = robot.lookup ("#emailAdressSignUpTextField").queryAs (TextField.class);
        fname.setText ("email");
        robot.clickOn ("#buttonOK");

        robot.clickOn ("OK");

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#firstNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#firstNameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#lastNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#lastNameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#usernameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#usernameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#emailAdressSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#emailErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#passwordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#passwordErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#repeatPasswordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#repeatPasswordErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        robot.closeCurrentWindow ();
    }


    @Test
    void updateAccountFail3(FxRobot robot) {
        robot.clickOn ("#settingsButton");

        TextField fname = robot.lookup ("#passwordSignUpPasswordField").queryAs (TextField.class);
        fname.setText ("password");
        fname = robot.lookup ("#repeatPasswordSignUpPasswordField").queryAs (TextField.class);
        fname.setText ("password1");
        robot.clickOn ("#buttonOK");

        robot.clickOn ("OK");

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#firstNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#firstNameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#lastNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#lastNameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#usernameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#usernameErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#emailAdressSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#emailErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#passwordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#passwordErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#repeatPasswordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#repeatPasswordErrorLabel")
                .queryAs (javafx.scene.control.Label.class), "errorLabel"));

        robot.closeCurrentWindow ();
    }

    @Test
    void accountUpdateSuccess(FxRobot robot) {
        robot.clickOn ("#settingsButton");

        robot.clickOn ("#usernameSignUpTextField").press (KeyCode.BACK_SPACE);
        robot.clickOn ("#buttonOK");

        javafx.scene.control.Label label = robot.lookup ("#userUsernameLabel").queryAs (javafx.scene.control.Label.class);
        assertEquals (MyResourceBundle.getString ("Username") + " " + "usernam", label.getText ());

        robot.clickOn ("#settingsButton");

        robot.clickOn ("#usernameSignUpTextField").write ("e");
        robot.clickOn ("#buttonOK");

        label = robot.lookup ("#userUsernameLabel").queryAs (javafx.scene.control.Label.class);
        assertEquals (MyResourceBundle.getString ("Username") + " " + "username", label.getText ());

        robot.closeCurrentWindow ();
    }
}
