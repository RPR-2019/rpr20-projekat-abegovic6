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

    private final ProjectDAO projectDAO;
    private final Account user;
    private final ResourceBundle resourceBundle;


    public LoginController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;
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

        if (user.getId() != -1) {
            opetNotesPage(actionEvent);
        }

    }

    private void opetNotesPage(ActionEvent actionEvent) {
        try {
            Node source = (Node)  actionEvent.getSource();
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            NotesController notesController = new NotesController(projectDAO, user, resourceBundle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/notes.fxml"), resourceBundle);
            loader.setController(notesController);

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



    public void openSignUpPage(ActionEvent actionEvent) {
        try {
            Node source = (Node)  actionEvent.getSource();
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            SignupController signupController = new SignupController(projectDAO, user, resourceBundle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"), resourceBundle);
            loader.setController(signupController);

            newStage.setTitle(resourceBundle.getString("SignUpTitle"));
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
