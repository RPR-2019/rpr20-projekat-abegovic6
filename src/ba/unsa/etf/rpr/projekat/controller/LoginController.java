package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;


public class LoginController {
    private final HostServices hostServices;
    private final ProjectDAO projectDAO;
    private final Account user;

    @FXML
    public PasswordField passwordLoginPasswordField;
    @FXML
    public TextField usernameLoginTextField;


    public LoginController(ProjectDAO projectDAO, Account user, HostServices hostServices) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.hostServices = hostServices;
    }

    @FXML
    public void initialize () {


    }

    public void logIn(ActionEvent actionEvent) {
        List<Account> accounts = projectDAO.getAllAccounts();
        boolean isPasswordCorrect = false;
        boolean isUsernameCorrect = false;

        user.setId(-1);

        if(accounts != null) {
            for (Account account : accounts) {
                if(account.getPassword().equals(passwordLoginPasswordField.getText())
                        && account.getUserName().equals(usernameLoginTextField.getText())) {
                    isUsernameCorrect = true;
                    isPasswordCorrect = true;

                    user.setId(account.getId());
                    user.setFirstName(account.getFirstName());
                    user.setLastName(account.getLastName());
                    user.setEmailAdress(account.getEmailAdress());
                    user.setUserName(account.getUserName());
                    user.setPassword(account.getPassword());
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

        if (user.getId() != -1) {
            opetNotesPage(actionEvent);
        }

    }

    private void opetNotesPage(ActionEvent actionEvent) {
        try {
            Node source = (Node)  actionEvent.getSource();
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            MainController mainController = new MainController(projectDAO, user, hostServices);

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

            SignupController signupController = new SignupController(projectDAO, user, hostServices);

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

}
