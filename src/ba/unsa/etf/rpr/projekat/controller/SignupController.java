package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class SignupController {
    private final Account user;
    private final ResourceBundle resourceBundle;
    private final ProjectDAO projectDAO;
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


    public SignupController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle, HostServices hostServices) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;
        this.hostServices = hostServices;
    }

    @FXML
    public void initialize () {


    }

    public void signUp(ActionEvent actionEvent) {
        usernameSignUpTextField.getStyleClass().remove("turnRed");
        emailAdressSignUpTextField.getStyleClass().remove("turnRed");
        passwordSignUpPasswordField.getStyleClass().remove("turnRed");
        repeatPasswordSignUpPasswordField.getStyleClass().remove("turnRed");

        usernameErrorLabel.getStyleClass().remove("errorLabel");
        emailErrorLabel.getStyleClass().remove("errorLabel");
        passwordErrorLabel.getStyleClass().remove("errorLabel");
        repeatPasswordErrorLabel.getStyleClass().remove("errorLabel");

        usernameErrorLabel.setText("");
        passwordErrorLabel.setText("");
        emailErrorLabel.setText("");
        repeatPasswordErrorLabel.setText("");

        isAlertNeeded = false;

        checkUsername();
        checkEmail();
        checkPassword();
        checkRepeatPassword();

        if(isAlertNeeded) openAlertMessage();
        else createANewUser(actionEvent);


        isAlertNeeded = false;

    }

    private void createANewUser(ActionEvent actionEvent) {
        user.setFirstName(firstNameSignUpTextField.getText());
        user.setLastName(lastNameSignUpTextField.getText());
        user.setUserName(usernameSignUpTextField.getText());
        user.setEmailAdress(emailAdressSignUpTextField.getText());
        user.setPassword(passwordSignUpPasswordField.getText());

        if(projectDAO.createAccount(user)) {
            try {
                Node source = (Node)  actionEvent.getSource();
                Stage oldStage  = (Stage) source.getScene().getWindow();
                Stage newStage = new Stage();

                MainController mainController = new MainController(projectDAO, user, resourceBundle, hostServices);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"), resourceBundle);
                loader.setController(mainController);

                newStage.setTitle(resourceBundle.getString("NotesTitle"));
                newStage.setScene(new Scene(loader.load(), 1100, 600));
                newStage.setMinHeight(600);
                newStage.setMinWidth(1100);

                oldStage.close();
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    private void openAlertMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resourceBundle.getString("Error"));
        alert.setHeaderText(resourceBundle.getString("ProblemPreformingTheAction"));
        alert.setContentText(resourceBundle.getString("PleaseTryAgain"));

        alert.showAndWait();

    }

    private void checkEmail() {
        try {
            user.setEmailAdress(emailAdressSignUpTextField.getText());
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
            repeatPasswordErrorLabel.setText("*" + resourceBundle.getString("PasswordsDontMatchHeaderError"));
            repeatPasswordSignUpPasswordField.setText("");
            passwordSignUpPasswordField.setText("");

        }
    }

    public void opetLogInPage(ActionEvent actionEvent) {
        try {
            Node source = (Node)  actionEvent.getSource();
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            LoginController loginController = new LoginController(projectDAO, user, resourceBundle, hostServices);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), resourceBundle);
            loader.setController(loginController);

            newStage.setTitle(resourceBundle.getString("LogInTitle"));
            newStage.setScene(new Scene(loader.load(), 1100, 600));
            newStage.setMinHeight(600);
            newStage.setMinWidth(1100);

            oldStage.close();
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
