package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.database.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ResourceBundle;

public class NotesController {
    private final Account user;
    private final ResourceBundle resourceBundle;
    private final ProjectDAO projectDAO;

    @FXML
    public Label userid;
    @FXML
    public Label useremail;
    @FXML
    public Label username;
    @FXML
    public Label userpassword;
    @FXML
    public Label userusername;
    @FXML
    public Label userlastname;


    public NotesController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;
    }

    @FXML
    public void initialize () {
        userid.setText(String.valueOf(user.getId()));
        useremail.setText(user.getEmailAdress());
        username.setText(user.getFirstName());
        userlastname.setText(user.getLastName());
        userusername.setText(user.getUserName());
        userpassword.setText(user.getPassword());



    }
}
