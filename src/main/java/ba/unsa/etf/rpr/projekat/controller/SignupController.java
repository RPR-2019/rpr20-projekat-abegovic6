package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dal.AccountModel;
import ba.unsa.etf.rpr.projekat.utilities.AccountValidationFailedException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class SignupController {
    private final AccountModel accountModel;

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


    public SignupController() {
        this.accountModel = DatabaseConnection.getInstance ().getAccountModel ();
    }

    @FXML
    public void initialize () {
        if(Locale.getDefault ().getLanguage ().equals ("en"))
            english.setSelected (true);
        else bosnian.setSelected (true);
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

        if(accountModel.createAccount(AccountModel.getCurrentUser ())) {
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
                newUserGuideStage.setScene(new Scene(userGuideLoader.load(), 900, 650));
                newUserGuideStage.setMinHeight(650);
                newUserGuideStage.setMinWidth(900);



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
            accountModel.isEmailUnique(emailAdressSignUpTextField.getText());

        } catch (AccountValidationFailedException exception) {
            isAlertNeeded = true;
            emailAdressSignUpTextField.getStyleClass().add("turnRed");
            emailErrorLabel.getStyleClass().add("errorLabel");
            emailErrorLabel.setText("*" + exception.getMessage());

        }


    }

    private void checkUsername() {
        try {
            AccountModel.getCurrentUser ().setUserName(usernameSignUpTextField.getText());
            accountModel.isUsernameUnique(usernameSignUpTextField.getText());

        } catch (AccountValidationFailedException exception) {
            isAlertNeeded = true;
            usernameSignUpTextField.getStyleClass().add("turnRed");
            usernameErrorLabel.getStyleClass().add("errorLabel");
            usernameErrorLabel.setText("*" + exception.getMessage());
        }

    }

    private void checkPassword() {
        try {
            AccountModel.getCurrentUser ().setPassword(passwordSignUpPasswordField.getText());

        } catch (AccountValidationFailedException exception) {
            isAlertNeeded = true;
            passwordSignUpPasswordField.getStyleClass().add("turnRed");
            passwordErrorLabel.getStyleClass().add("errorLabel");
            passwordErrorLabel.setText("*" + exception.getMessage());
        }
    }

    private void checkRepeatPassword() {
        if(!repeatPasswordSignUpPasswordField.getText().equals(passwordSignUpPasswordField.getText())) {
            isAlertNeeded = true;
            repeatPasswordSignUpPasswordField.getStyleClass().add("turnRed");
            repeatPasswordErrorLabel.getStyleClass().add("errorLabel");
            repeatPasswordErrorLabel.setText("*" + MyResourceBundle.getString("PasswordsDontMatchHeaderError"));

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

    public void toEnglish() {
        try {
            Locale.setDefault(new Locale("en", "US"));
            MyResourceBundle.setLocale (Locale.getDefault ());
            Stage stage = (Stage) usernameErrorLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(new SignupController ());
            Parent root = loader.load();
            stage.setTitle(MyResourceBundle.getString ("SignUpTitle"));
            stage.setScene(new Scene(root, 1100, 600));
            stage.setMinHeight(600);
            stage.setMinWidth(1100);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void toBosnian() {
        try {
            Locale.setDefault(new Locale("bs", "BA"));
            MyResourceBundle.setLocale (Locale.getDefault ());
            Stage stage = (Stage) usernameErrorLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(new SignupController ());
            Parent root = loader.load();
            stage.setTitle(MyResourceBundle.getString ("SignUpTitle"));
            stage.setScene(new Scene(root, 1100, 600));
            stage.setMinHeight(600);
            stage.setMinWidth(1100);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
