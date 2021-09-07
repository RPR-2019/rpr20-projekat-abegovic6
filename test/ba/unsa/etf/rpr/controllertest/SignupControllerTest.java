package ba.unsa.etf.rpr.controllertest;

import static org.junit.jupiter.api.Assertions.*;

import ba.unsa.etf.rpr.Utility;
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
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.api.FxRobot;

import java.util.Locale;
@ExtendWith(ApplicationExtension.class)
class SignupControllerTest {
    private static DatabaseConnection databaseConnection;
    private static Account account;

    @Start
    public void start (Stage stage) throws Exception {
        account = Utility.getInstance ().getTestAccount ();
        databaseConnection = DatabaseConnection.getInstance ();

        MyResourceBundle.setLocale (Locale.getDefault());
        AccountModel.setCurrentUser (new Account ());

        SignupController signupController = new SignupController ();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"),
                MyResourceBundle.getResourceBundle ());
        loader.setController(signupController);
        stage.setTitle(MyResourceBundle.getString("SignUpTitle"));
        stage.setScene(new Scene(loader.load(), 1100, 600));
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
        assertEquals ("SIGN UP", title.getText ());

        robot.clickOn ("#bosnian");
        radioButton1 = robot.lookup ("#english").queryAs (RadioButton.class);
        radioButton2 = robot.lookup ("#bosnian").queryAs (RadioButton.class);
        assertTrue (radioButton2.isSelected ());
        assertFalse (radioButton1.isSelected ());
        title = robot.lookup ("#title").queryAs (Label.class);
        assertEquals ("REGISTRIRAJTE SE", title.getText ());

        robot.clickOn ("#english");
        radioButton1 = robot.lookup ("#english").queryAs (RadioButton.class);
        radioButton2 = robot.lookup ("#bosnian").queryAs (RadioButton.class);
        assertTrue (radioButton1.isSelected ());
        assertFalse (radioButton2.isSelected ());
        title = robot.lookup ("#title").queryAs (Label.class);
        assertEquals ("SIGN UP", title.getText ());
    }

    @Test
    void createAccount1(FxRobot robot) {
        robot.clickOn ("#firstNameSignUpTextField").write ("");
        robot.clickOn ("#lastNameSignUpTextField").write ("");
        robot.clickOn ("#usernameSignUpTextField").write (account.getUserName ());
        robot.clickOn ("#emailAdressSignUpTextField").write (account.getEmailAdress ());
        robot.clickOn ("#passwordSignUpPasswordField").write (account.getPassword ());
        robot.clickOn ("#repeatPasswordSignUpPasswordField").write (account.getPassword ());
        robot.clickOn ("#signupbutton");

        robot.clickOn ("OK");

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#firstNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#firstNameErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#lastNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#lastNameErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#usernameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#usernameErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#emailAdressSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#emailErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#passwordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#passwordErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#repeatPasswordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#repeatPasswordErrorLabel")
                .queryAs (Label.class), "errorLabel"));

    }

    @Test
    void createAccount2(FxRobot robot) {
        robot.clickOn ("#firstNameSignUpTextField").write (account.getFirstName ());
        robot.clickOn ("#lastNameSignUpTextField").write (account.getLastName ());
        robot.clickOn ("#usernameSignUpTextField").write ("user");
        robot.clickOn ("#emailAdressSignUpTextField").write ("email");
        robot.clickOn ("#passwordSignUpPasswordField").write (account.getPassword ());
        robot.clickOn ("#repeatPasswordSignUpPasswordField").write (account.getPassword ());
        robot.clickOn ("#signupbutton");

        robot.clickOn ("OK");

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#firstNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#firstNameErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#lastNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#lastNameErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#usernameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#usernameErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#emailAdressSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#emailErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#passwordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#passwordErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#repeatPasswordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#repeatPasswordErrorLabel")
                .queryAs (Label.class), "errorLabel"));

    }


    @Test
    void createAccount3(FxRobot robot) {
        robot.clickOn ("#firstNameSignUpTextField").write (account.getFirstName ());
        robot.clickOn ("#lastNameSignUpTextField").write (account.getLastName ());
        robot.clickOn ("#usernameSignUpTextField").write (account.getUserName ());
        robot.clickOn ("#emailAdressSignUpTextField").write (account.getEmailAdress ());
        robot.clickOn ("#passwordSignUpPasswordField").write ("password");
        robot.clickOn ("#repeatPasswordSignUpPasswordField").write ("password1");
        robot.clickOn ("#signupbutton");

        robot.clickOn ("OK");

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#firstNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#firstNameErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#lastNameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#lastNameErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#usernameSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#usernameErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertFalse (Utility.checkTextFieldStyle (robot.lookup ("#emailAdressSignUpTextField")
                .queryAs (TextField.class), "turnRed"));
        assertFalse (Utility.checkLabelStyle (robot.lookup ("#emailErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#passwordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#passwordErrorLabel")
                .queryAs (Label.class), "errorLabel"));

        assertTrue (Utility.checkTextFieldStyle (robot.lookup ("#repeatPasswordSignUpPasswordField")
                .queryAs (TextField.class), "turnRed"));
        assertTrue (Utility.checkLabelStyle (robot.lookup ("#repeatPasswordErrorLabel")
                .queryAs (Label.class), "errorLabel"));

    }

    @Test
    void createAccount4(FxRobot robot) {
        robot.clickOn ("#firstNameSignUpTextField").write (account.getFirstName ());
        robot.clickOn ("#lastNameSignUpTextField").write (account.getLastName ());
        robot.clickOn ("#usernameSignUpTextField").write (account.getUserName ());
        robot.clickOn ("#emailAdressSignUpTextField").write (account.getEmailAdress ());
        robot.clickOn ("#passwordSignUpPasswordField").write (account.getPassword ());
        robot.clickOn ("#repeatPasswordSignUpPasswordField").write (account.getPassword ());
        robot.clickOn ("#signupbutton");

        robot.lookup ("NOTA");
    }

}
