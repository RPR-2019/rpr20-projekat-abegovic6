package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.AccountModel;
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

public class SignupController {
    private final ProjectDAO projectDAO;

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


    public SignupController() {
        this.projectDAO = ProjectDAO.getInstance ();
    }

    @FXML
    public void initialize () {


    }

    public void signUp(ActionEvent actionEvent) {
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
        else createANewUser(actionEvent);


        isAlertNeeded = false;

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

    private void createANewUser(ActionEvent actionEvent) {
        AccountModel.getCurrentUser ().setFirstName(firstNameSignUpTextField.getText());
        AccountModel.getCurrentUser ().setLastName(lastNameSignUpTextField.getText());
        AccountModel.getCurrentUser ().setUserName(usernameSignUpTextField.getText());
        AccountModel.getCurrentUser ().setEmailAdress(emailAdressSignUpTextField.getText());
        AccountModel.getCurrentUser ().setPassword(passwordSignUpPasswordField.getText());

        if(projectDAO.createAccount(AccountModel.getCurrentUser ())) {
            try {
                Node source = (Node)  actionEvent.getSource();
                Stage oldStage  = (Stage) source.getScene().getWindow();
                Stage newStage = new Stage();

                MainController mainController = new MainController();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"),
                        MyResourceBundle.getResourceBundle ());
                loader.setController(mainController);

                newStage.setTitle(MyResourceBundle.getString("NotesTitle"));
                newStage.setScene(new Scene(loader.load(), 1100, 600));
                newStage.setMinHeight(600);
                newStage.setMinWidth(1100);

                Stage newUserGuideStage = new Stage();

                HelpController helpController = new HelpController ();


                FXMLLoader userGuideLoader = new FXMLLoader(getClass().getResource("/fxml/help.fxml"),
                        MyResourceBundle.getResourceBundle ());
                userGuideLoader.setController(helpController);

                newUserGuideStage.setTitle(MyResourceBundle.getString("UserGuideTitle"));
                newUserGuideStage.setScene(new Scene(userGuideLoader.load(), 700, 500));
                newUserGuideStage.setMinHeight(500);
                newUserGuideStage.setMinWidth(700);



                oldStage.close();
                newStage.show();
                newUserGuideStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    private void openAlertMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(MyResourceBundle.getString("Error"));
        alert.setHeaderText(MyResourceBundle.getString("ProblemPreformingTheAction"));
        alert.setContentText(MyResourceBundle.getString("PleaseTryAgain"));

        alert.showAndWait();

    }

    private void checkEmail() {
        try {
            AccountModel.getCurrentUser ().setEmailAdress(emailAdressSignUpTextField.getText());
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
            AccountModel.getCurrentUser ().setUserName(usernameSignUpTextField.getText());
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
            AccountModel.getCurrentUser ().setPassword(passwordSignUpPasswordField.getText());

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

    public void opetLogInPage(ActionEvent actionEvent) {
        try {
            Node source = (Node)  actionEvent.getSource();
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            LoginController loginController = new LoginController();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(loginController);

            newStage.setTitle(MyResourceBundle.getString("LogInTitle"));
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
