package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.database.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class NotesController {
    private final Account user;
    private final ResourceBundle resourceBundle;
    private final ProjectDAO projectDAO;

//    @FXML
//    public Label userid;
    @FXML
    public Label userEmailLabel;
    @FXML
    public Label userNameLabel;
    @FXML
//    public Label userpassword;
//    @FXML
    public Label userUsernameLabel;
//    @FXML
//    public Label userlastname;


    public NotesController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;
    }

    @FXML
    public void initialize () {
        userEmailLabel.setText(resourceBundle.getString("EmailAdress") + " " + user.getEmailAdress());
        userNameLabel.setText(resourceBundle.getString("Name") + " " +  user.getFirstName() + " " + user.getLastName());
        userUsernameLabel.setText(resourceBundle.getString("Username") + " " +user.getUserName());



    }

    public void createNewGroup() {
        try {
            Stage newStage = new Stage();

            GroupController groupController = new GroupController(projectDAO, user, resourceBundle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/group.fxml"), resourceBundle);
            loader.setController(groupController);

            newStage.setTitle(resourceBundle.getString("CreateGroup"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void createNewLabel() {
        try {
            Stage newStage = new Stage();

            LabelController labelController = new LabelController(projectDAO, user, resourceBundle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/label.fxml"), resourceBundle);
            loader.setController(labelController);

            newStage.setTitle(resourceBundle.getString("CreateLabel"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
