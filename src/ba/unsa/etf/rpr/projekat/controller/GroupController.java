package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.database.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;

import java.util.ResourceBundle;

public class GroupController {


    private final ProjectDAO projectDAO;
    private final Account user;
    private final ResourceBundle resourceBundle;

    public GroupController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;
    }
}
