package ba.unsa.etf.rpr.controllertest;

import ba.unsa.etf.rpr.Utility;
import ba.unsa.etf.rpr.projekat.controller.LoginController;
import ba.unsa.etf.rpr.projekat.controller.SignupController;
import ba.unsa.etf.rpr.projekat.dal.AccountModel;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dto.Account;
import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class LoginControllerTest {
    private static DatabaseConnection databaseConnection;
    private static Account account;

    @Start
    public void start (Stage stage) throws Exception {
        account = Utility.getInstance ().getTestAccount ();
        databaseConnection = DatabaseConnection.getInstance ();

        MyResourceBundle.setLocale (Locale.getDefault());
        AccountModel.setCurrentUser (new Account ());

        LoginController loginController = new LoginController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"),
                MyResourceBundle.getResourceBundle ());
        loader.setController(loginController);
        stage.setTitle(MyResourceBundle.getString("LogInTitle"));
        stage.setScene(new Scene (loader.load(), 1100, 600));
        stage.setMinHeight(600);
        stage.setMinWidth(1100);
        stage.show ();
        stage.toFront ();
    }

    @BeforeEach
    void forEachTest() {
        databaseConnection.deleteTestUser (account);
    }

    @AfterAll
    static void onEnd() {
        databaseConnection.deleteTestUser (account);
        DatabaseConnection.removeInstance ();
    }

    @Test
    void testTranslation(FxRobot robot) {
        RadioButton radioButton1 = robot.lookup ("#english").queryAs (RadioButton.class);
        RadioButton radioButton2 = robot.lookup ("#bosnian").queryAs (RadioButton.class);
        Label title = robot.lookup ("#title").queryAs (Label.class);

        assertTrue (radioButton1.isSelected ());
        assertFalse (radioButton2.isSelected ());
        assertEquals ("WELCOME TO NOTA", title.getText ());

        robot.clickOn ("#bosnian");
        radioButton1 = robot.lookup ("#english").queryAs (RadioButton.class);
        radioButton2 = robot.lookup ("#bosnian").queryAs (RadioButton.class);
        assertTrue (radioButton2.isSelected ());
        assertFalse (radioButton1.isSelected ());
        title = robot.lookup ("#title").queryAs (Label.class);
        assertEquals ("DOBRODOŠLI U NOTA", title.getText ());

        robot.clickOn ("#english");
        radioButton1 = robot.lookup ("#english").queryAs (RadioButton.class);
        radioButton2 = robot.lookup ("#bosnian").queryAs (RadioButton.class);
        assertTrue (radioButton1.isSelected ());
        assertFalse (radioButton2.isSelected ());
        title = robot.lookup ("#title").queryAs (Label.class);
        assertEquals ("WELCOME TO NOTA", title.getText ());
    }

    @Test
    void testLogIn1(FxRobot robot) {
        databaseConnection.getAccountModel ().createAccount (account);
        robot.clickOn ("#usernameLoginTextField").write ("user");
        robot.clickOn ("#passwordLoginPasswordField").write (account.getPassword ());
        robot.clickOn ("#loginbutton");
        robot.clickOn ("OK");

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#usernameLoginTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#passwordLoginPasswordField")
                .queryAs (TextField.class), "turnRed"));
    }

    @Test
    void testLogIn2(FxRobot robot) {
        databaseConnection.getAccountModel ().createAccount (account);
        robot.clickOn ("#usernameLoginTextField").write (account.getUserName ());
        robot.clickOn ("#passwordLoginPasswordField").write ("somepasswordthatisnotcorrect");
        robot.clickOn ("#loginbutton");
        robot.clickOn ("OK");

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#usernameLoginTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#passwordLoginPasswordField")
                .queryAs (TextField.class), "turnRed"));
    }

    @Test
    void testLogIn3(FxRobot robot) {
        databaseConnection.getAccountModel ().createAccount (account);
        robot.clickOn ("#usernameLoginTextField").write (account.getUserName ());
        robot.clickOn ("#passwordLoginPasswordField").write (account.getPassword ());
        robot.clickOn ("#loginbutton");
        robot.lookup ("NOTA");
    }
}
