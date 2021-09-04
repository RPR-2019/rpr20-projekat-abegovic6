package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.model.AccountModel;
import ba.unsa.etf.rpr.projekat.ProjectDAO;
import ba.unsa.etf.rpr.projekat.javabean.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;



public class LoginController {


    @FXML
    public PasswordField passwordLoginPasswordField;
    @FXML
    public TextField usernameLoginTextField;
    @FXML
    public RadioButton english;
    @FXML
    public RadioButton bosnian;


    public LoginController() {
    }

    @FXML
    public void initialize () {
        if(Locale.getDefault ().getLanguage ().equals ("en"))
            english.setSelected (true);
        else bosnian.setSelected (true);


    }

    public void logIn(ActionEvent actionEvent) {
        List<Account> accounts = ProjectDAO.getInstance ().getAccountModel ().getAllAccounts ();
        boolean isPasswordCorrect = false;
        boolean isUsernameCorrect = false;

        AccountModel.getCurrentUser ().setId(-1);

        if(accounts != null) {
            for (Account account : accounts) {
                if(account.getPassword().equals(passwordLoginPasswordField.getText())
                        && account.getUserName().equals(usernameLoginTextField.getText())) {
                    isUsernameCorrect = true;
                    isPasswordCorrect = true;

                    AccountModel.getCurrentUser ().setId(account.getId());
                    AccountModel.getCurrentUser ().setFirstName(account.getFirstName());
                    AccountModel.getCurrentUser ().setLastName(account.getLastName());
                    AccountModel.getCurrentUser ().setEmailAdress(account.getEmailAdress());
                    AccountModel.getCurrentUser ().setUserName(account.getUserName());
                    AccountModel.getCurrentUser ().setPassword(account.getPassword());
                    break;
                }
                if(account.getUserName().equals(usernameLoginTextField.getText())) {
                    isUsernameCorrect = true;
                    account.setId(-1);
                }
            }
        }

        if(isUsernameCorrect && !isPasswordCorrect) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(MyResourceBundle.getString("PasswordError"));
            alert.setHeaderText(MyResourceBundle.getString("PasswordNotCorrectHeaderError"));
            alert.setContentText(MyResourceBundle.getString("PleaseTryAgain"));

            passwordLoginPasswordField.setText ("");

            alert.showAndWait();
        }

        if(!isUsernameCorrect) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(MyResourceBundle.getString("UssernameError"));
            alert.setHeaderText(MyResourceBundle.getString("UssernameDoesntExistHeaderError"));
            alert.setContentText(MyResourceBundle.getString("UssernameDoesntExistContentError"));

            passwordLoginPasswordField.setText ("");

            alert.showAndWait();
        }

        if (AccountModel.getCurrentUser ().getId() != -1) {
            opetNotesPage(actionEvent);
        }

    }

    private void opetNotesPage(ActionEvent actionEvent) {
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

            oldStage.close();
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void openSignUpPage(ActionEvent actionEvent) {
        try {
            Node source = (Node)  actionEvent.getSource();
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            SignupController signupController = new SignupController();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(signupController);

            newStage.setTitle(MyResourceBundle.getString("SignUpTitle"));
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
            Stage stage = (Stage) usernameLoginTextField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(new LoginController ());
            Parent root = loader.load();
            stage.setTitle(MyResourceBundle.getString ("LogInTitle"));
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
            Stage stage = (Stage) usernameLoginTextField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(new LoginController ());
            Parent root = loader.load();
            stage.setTitle(MyResourceBundle.getString ("LogInTitle"));
            stage.setScene(new Scene(root, 1100, 600));
            stage.setMinHeight(600);
            stage.setMinWidth(1100);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
