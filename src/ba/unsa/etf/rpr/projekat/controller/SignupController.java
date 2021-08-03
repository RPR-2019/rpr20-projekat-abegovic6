package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.database.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import javafx.fxml.FXML;

import java.util.ResourceBundle;

public class SignupController {
    private Account user;
    private ResourceBundle resourceBundle;
    private ProjectDAO projectDAO;


    public SignupController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;
    }

    @FXML
    public void initialize () {


    }


}
