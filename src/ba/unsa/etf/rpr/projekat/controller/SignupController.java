package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.database.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.ResourceBundle;

public class SignupController {
    private final Account user;
    private final ResourceBundle resourceBundle;
    private final ProjectDAO projectDAO;

    private boolean isAlertNeeded;
    private String alertTitle;
    private String alertHeading;
    private String alertContent;

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


    public SignupController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;
    }

    @FXML
    public void initialize () {


    }

    public void signUp() {
        usernameSignUpTextField.getStyleClass().remove("turnRed");
        emailAdressSignUpTextField.getStyleClass().remove("turnRed");

        isAlertNeeded = false;

        checkUsername();
        checkEmail();
        checkPassword();
        checkRepeatPassword();

        createANewUser();

    }

    private void createANewUser() {

    }

    private void open() {

    }

    private void alert() {
        if (isAlertNeeded) {
            passwordSignUpPasswordField.setText("");
            repeatPasswordSignUpPasswordField.setText("");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(alertTitle);
            alert.setHeaderText(alertHeading);
            alert.setContentText(alertContent);

            isAlertNeeded = false;

            alert.showAndWait();
        }
    }

    private void checkEmail() {
        try {
            user.setEmailAdress(emailAdressSignUpTextField.getText());
            if(projectDAO.isEmailUnique(emailAdressSignUpTextField.getText())) {
                isAlertNeeded = true;
                alertTitle = resourceBundle.getString("EmailNotUniqueError");
                alertHeading = resourceBundle.getString("EmailNotUniqueHeaderError");
                alertContent = resourceBundle.getString("PleaseTryAgain");
            }

        } catch (IllegalArgumentException exception) {
            isAlertNeeded = true;
            alertTitle = resourceBundle.getString("InvalidEmailAddress");
            alertHeading = exception.getMessage();
            alertContent = resourceBundle.getString("PleaseTryAgain");

        }

        if(isAlertNeeded) {
            emailAdressSignUpTextField.getStyleClass().add("turnRed");
        }

        alert();

    }

    private void checkUsername() {
        try {
            user.setUserName(usernameSignUpTextField.getText());
            if(projectDAO.isUsernameUnique(usernameSignUpTextField.getText())) {
                isAlertNeeded = true;
                alertTitle = resourceBundle.getString("UsernameNotUniqueError");
                alertHeading = resourceBundle.getString("UsernameNotUniqueHeaderError");
                alertContent = resourceBundle.getString("PleaseTryAgain");
            }

        } catch (IllegalArgumentException exception) {
            isAlertNeeded = true;
            alertTitle = resourceBundle.getString("UssernameError");
            alertHeading = exception.getMessage();
            alertContent = resourceBundle.getString("PleaseTryAgain");
        }

        if(isAlertNeeded) {
            usernameSignUpTextField.getStyleClass().add("turnRed");
        }

        alert();

    }

    private void checkPassword() {
        try {
            user.setPassword(passwordSignUpPasswordField.getText());

        } catch (IllegalArgumentException exception) {
            isAlertNeeded = true;
            alertTitle = resourceBundle.getString("PasswordError");
            alertHeading = exception.getMessage();
            alertContent = resourceBundle.getString("PleaseTryAgain");
        }

        alert();


    }

    private void checkRepeatPassword() {
        if(!repeatPasswordSignUpPasswordField.getText().equals(passwordSignUpPasswordField.getText())) {
            isAlertNeeded = true;
            alertTitle = resourceBundle.getString("PasswordsDontMatchError");
            alertHeading = resourceBundle.getString("PasswordsDontMatchHeaderError");
            alertContent = resourceBundle.getString("PleaseTryAgain");
        }

        alert();

    }


}
