package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class SettingsController {

    public Account user;
    public final ProjectDAO projectDAO;
    private final HostServices hostServices;

    private boolean isAlertNeeded;

    @FXML
    public TextField firstNameSignUpTextField;
    @FXML
    public TextField lastNameSignUpTextField;
    @FXML
    public TextField usernameSignUpTextField;
    @FXML
    public TextField emailAdressSignUpTextField;
    @FXML
    public PasswordField passwordSignUpPasswordField;
    @FXML
    public PasswordField repeatPasswordSignUpPasswordField;
    @FXML
    public Label usernameErrorLabel;
    @FXML
    public Label emailErrorLabel;
    @FXML
    public Label passwordErrorLabel;
    @FXML
    public Label repeatPasswordErrorLabel;
    @FXML
    public Label firstNameErrorLabel;
    @FXML
    public Label lastNameErrorLabel;
    @FXML
    public RadioButton english;
    @FXML
    public RadioButton bosnian;

    public SettingsController (Account user, ProjectDAO projectDAO, HostServices hostServices) {
        this.user = user;
        this.projectDAO = projectDAO;
        this.hostServices = hostServices;
    }

    @FXML
    public void initialize() {

        if(Locale.getDefault ().getLanguage ().equals ("en"))
            english.setSelected (true);
        else bosnian.setSelected (true);

        firstNameSignUpTextField.setText (user.getFirstName ());
        lastNameSignUpTextField.setText (user.getLastName ());
        emailAdressSignUpTextField.setText (user.getEmailAdress ());
        usernameSignUpTextField.setText (user.getUserName ());
        passwordSignUpPasswordField.setText (user.getPassword ());
        repeatPasswordSignUpPasswordField.setText (user.getPassword ());

    }

    public void deleteUser(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(firstNameSignUpTextField.getContextMenu ());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(MyResourceBundle.getString ("DeleteThis"));
        alert.setHeaderText(MyResourceBundle.getString ("DeleteAccount"));
        alert.setContentText(MyResourceBundle.getString ("AreYouSure"));


        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            projectDAO.deleteAccount (user);
        }

        try {
            Node n = (Node) actionEvent.getSource();
            Stage oldStage = (Stage) n.getScene().getWindow();
            user = new Account ();
            LoginController loginController = new LoginController (projectDAO, user, hostServices);

            Stage stage = new Stage ();
            FXMLLoader loader = new FXMLLoader (getClass ().getResource ("/fxml/login.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(loginController);
            stage.setTitle(MyResourceBundle.getString("LogInTitle"));
            stage.setScene(new Scene(loader.load(), 1100, 600));
            stage.setMinHeight(600);
            stage.setMinWidth(1100);

            stage.show ();
            oldStage.close ();

        } catch (IOException e) {
            e.printStackTrace ();
        }


    }

    public void toEnglish() {
        try {
            Locale.setDefault(new Locale("en", "US"));
            MyResourceBundle.setLocale (Locale.getDefault ());
            Stage stage = (Stage) emailAdressSignUpTextField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(new SettingsController (user, projectDAO, hostServices));
            Parent root = loader.load();
            stage.setTitle("Korisnici");
            stage.setScene(new Scene (root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toBosnian() {
        try {
            Locale.setDefault(new Locale("bs", "BA"));
            MyResourceBundle.setLocale (Locale.getDefault ());
            Stage stage = (Stage) emailAdressSignUpTextField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(new SettingsController (user, projectDAO, hostServices));
            Parent root = loader.load();
            stage.setTitle("Korisnici");
            stage.setScene(new Scene (root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelSettings(ActionEvent actionEvent) {
        try {

            Node n = (Node) actionEvent.getSource();
            Stage oldStage = (Stage) n.getScene().getWindow();
            MainController mainController = new MainController(projectDAO, user, hostServices);

            Stage stage = new Stage ();
            FXMLLoader loader = new FXMLLoader (getClass ().getResource ("/fxml/main.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(mainController);
            stage.setTitle(MyResourceBundle.getString("NotesTitle"));
            stage.setScene(new Scene(loader.load(), 1100, 600));
            stage.setMinHeight(600);
            stage.setMinWidth(1100);

            stage.show ();
            oldStage.close ();

        } catch (IOException e) {
            e.printStackTrace ();
        }


    }

    public void changeSettings(ActionEvent actionEvent) {
        usernameSignUpTextField.getStyleClass().remove("turnRed");
        emailAdressSignUpTextField.getStyleClass().remove("turnRed");
        passwordSignUpPasswordField.getStyleClass().remove("turnRed");
        repeatPasswordSignUpPasswordField.getStyleClass().remove("turnRed");
        firstNameSignUpTextField.getStyleClass ().remove ("turnRed");
        lastNameSignUpTextField.getStyleClass ().remove ("turnRed");

        usernameErrorLabel.getStyleClass().remove("errorLabel");
        emailErrorLabel.getStyleClass().remove("errorLabel");
        passwordErrorLabel.getStyleClass().remove("errorLabel");
        repeatPasswordErrorLabel.getStyleClass().remove("errorLabel");
        lastNameErrorLabel.getStyleClass ().remove ("errorLabel");
        firstNameErrorLabel.getStyleClass ().remove ("errorLabel");

        usernameErrorLabel.setText("");
        passwordErrorLabel.setText("");
        emailErrorLabel.setText("");
        repeatPasswordErrorLabel.setText("");
        lastNameErrorLabel.setText ("");
        firstNameErrorLabel.setText ("");

        isAlertNeeded = false;

        checkFirstName ();
        checkLastName ();
        checkUsername();
        checkEmail();
        checkPassword();
        checkRepeatPassword();

        if(isAlertNeeded) openAlertMessage();
        else updateUser(actionEvent);


        isAlertNeeded = false;
    }

    private void updateUser (ActionEvent actionEvent) {
        user.setFirstName(firstNameSignUpTextField.getText());
        user.setLastName(lastNameSignUpTextField.getText());
        user.setUserName(usernameSignUpTextField.getText());
        user.setEmailAdress(emailAdressSignUpTextField.getText());
        user.setPassword(passwordSignUpPasswordField.getText());

        projectDAO.updateUser(user);

        cancelSettings (actionEvent);
    }

    private void openAlertMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(MyResourceBundle.getString("Error"));
        alert.setHeaderText(MyResourceBundle.getString("ProblemPreformingTheAction"));
        alert.setContentText(MyResourceBundle.getString("PleaseTryAgain"));

        alert.showAndWait();

    }

    private void checkFirstName() {
        if(firstNameSignUpTextField.getText ().equals ("")) {
            isAlertNeeded = true;
            firstNameErrorLabel.setText ("*" + MyResourceBundle.getString ("ThisCantBeEmpty"));
            firstNameErrorLabel.getStyleClass ().add ("errorLabel");
            firstNameSignUpTextField.getStyleClass ().add ("turnRed");
        }
    }

    private void checkLastName() {
        if(lastNameSignUpTextField.getText ().equals ("")) {
            isAlertNeeded = true;
            lastNameErrorLabel.setText ("*" + MyResourceBundle.getString ("ThisCantBeEmpty"));
            lastNameErrorLabel.getStyleClass ().add ("errorLabel");
            lastNameSignUpTextField.getStyleClass ().add ("turnRed");
        }
    }

    private void checkEmail() {
        try {
            user.setEmailAdress(emailAdressSignUpTextField.getText());
            if(!emailAdressSignUpTextField.getText ().equals (user.getEmailAdress ()))
                projectDAO.isEmailUnique(emailAdressSignUpTextField.getText());

        } catch (IllegalArgumentException exception) {
            isAlertNeeded = true;
            emailAdressSignUpTextField.getStyleClass().add("turnRed");
            emailErrorLabel.getStyleClass().add("errorLabel");
            emailErrorLabel.setText("*" + exception.getMessage());

        }


    }

    private void checkUsername() {
        try {
            user.setUserName(usernameSignUpTextField.getText());
            if(!usernameSignUpTextField.getText ().equals (user.getUserName ()))
                projectDAO.isUsernameUnique(usernameSignUpTextField.getText());

        } catch (IllegalArgumentException exception) {
            isAlertNeeded = true;
            usernameSignUpTextField.getStyleClass().add("turnRed");
            usernameErrorLabel.getStyleClass().add("errorLabel");
            usernameErrorLabel.setText("*" + exception.getMessage());
        }

    }

    private void checkPassword() {
        try {
            user.setPassword(passwordSignUpPasswordField.getText());

        } catch (IllegalArgumentException exception) {
            isAlertNeeded = true;
            passwordSignUpPasswordField.getStyleClass().add("turnRed");
            passwordErrorLabel.getStyleClass().add("errorLabel");
            passwordErrorLabel.setText("*" + exception.getMessage());
            repeatPasswordSignUpPasswordField.setText("");
            passwordSignUpPasswordField.setText("");
        }
    }

    private void checkRepeatPassword() {
        if(!repeatPasswordSignUpPasswordField.getText().equals(passwordSignUpPasswordField.getText())) {
            isAlertNeeded = true;
            repeatPasswordSignUpPasswordField.getStyleClass().add("turnRed");
            repeatPasswordErrorLabel.getStyleClass().add("errorLabel");
            repeatPasswordErrorLabel.setText("*" + MyResourceBundle.getString("PasswordsDontMatchHeaderError"));
            repeatPasswordSignUpPasswordField.setText("");
            passwordSignUpPasswordField.setText("");

        }
    }


}
