package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.database.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
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
    @FXML
    public PasswordField passwordLoginPasswordField;
    @FXML
    public TextField usernameLoginTextField;

    private SignupController signupController;
    private final ProjectDAO projectDAO;
    private final Account user;
    private ResourceBundle resourceBundle;


    public LoginController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;
    }

    @FXML
    public void initialize () {


    }

    public void logInNota() {
        List<Account> accounts = projectDAO.getAllAccounts();
        boolean isPasswordCorrect = false;
        boolean isUsernameCorrect = false;

        if(accounts != null) {
            for (Account account : accounts) {
                if(account.getPassword().equals(passwordLoginPasswordField.getText())
                        && account.getUserName().equals(usernameLoginTextField.getText())) {
                    isUsernameCorrect = true;
                    isPasswordCorrect = true;

                    user.setFirstName(account.getFirstName());
                    user.setLastName(account.getLastName());
                    user.setEmailAdress(account.getEmailAdress());
                    user.setUserName(account.getUserName());
                    user.setPassword(account.getPassword());
                }
                if(account.getUserName().equals(usernameLoginTextField.getText())) {
                    isUsernameCorrect = true;
                }
            }
        }

        if(isUsernameCorrect && !isPasswordCorrect) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resourceBundle.getString("PasswordError"));
            alert.setHeaderText(resourceBundle.getString("PasswordNotCorrectHeaderError"));
            alert.setContentText(resourceBundle.getString("PasswordNotCorrectContentError"));

            alert.showAndWait();
        }

        if(!isUsernameCorrect) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resourceBundle.getString("UssernameError"));
            alert.setHeaderText(resourceBundle.getString("UssernameDoesntExistHeaderError"));
            alert.setContentText(resourceBundle.getString("UssernameDoesntExistContentError"));

            alert.showAndWait();
        }

    }

    public void openSignUpPage(ActionEvent actionEvent) {
        try {
            Node source = (Node)  actionEvent.getSource();
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            signupController = new SignupController(projectDAO, user, resourceBundle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"), resourceBundle);
            loader.setController(signupController);

            newStage.setTitle("SIGN UP");
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
