package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.GroupListViewCell;
import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import ba.unsa.etf.rpr.projekat.model.Group;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class NotesController {
    private final Account user;
    private final ResourceBundle resourceBundle;
    private final ProjectDAO projectDAO;

    private ObservableList<Group> groupsObservableList;
    private ObservableList<ba.unsa.etf.rpr.projekat.model.Label> labelObservableList;

    @FXML
    public Label userEmailLabel;
    @FXML
    public Label userNameLabel;
    @FXML
    public Label userUsernameLabel;

    @FXML
    public ListView notaListView;



    public NotesController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;

        this.groupsObservableList = FXCollections.observableArrayList();
        this.labelObservableList = FXCollections.observableArrayList();

        this.groupsObservableList.addAll(this.projectDAO.getAllGroupsForAccount(this.user));
        this.labelObservableList.addAll(this.projectDAO.getAllLabelsForAccount(this.user));
    }

    @FXML
    public void initialize () {
        userEmailLabel.setText(resourceBundle.getString("EmailAdress") + " " + user.getEmailAdress());
        userNameLabel.setText(resourceBundle.getString("Name") + " " +  user.getFirstName() + " " + user.getLastName());
        userUsernameLabel.setText(resourceBundle.getString("Username") + " " +user.getUserName());

        notaListView.setItems(groupsObservableList);
        notaListView.setCellFactory(listView -> new GroupListViewCell(groupsObservableList, projectDAO, resourceBundle));



    }

    public void createNewGroup() {
        try {
            Stage newStage = new Stage();

            Group group = new Group();
            group.setId(-2);
            group.setAccountId(user.getId());

            GroupController groupController = new GroupController(group, groupsObservableList, user, resourceBundle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/group.fxml"), resourceBundle);
            loader.setController(groupController);

            newStage.setTitle(resourceBundle.getString("CreateGroup"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(group.getId() == -1) {
                    if(projectDAO.createGroup(group)) {
                        groupsObservableList.add(group);
                    }


                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createNewLabel() {
        try {
            Stage newStage = new Stage();

            ba.unsa.etf.rpr.projekat.model.Label label = new ba.unsa.etf.rpr.projekat.model.Label();
            label.setId(-2);
            label.setAccountId(user.getId());

            LabelController labelController = new LabelController(label, labelObservableList, user, resourceBundle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/label.fxml"), resourceBundle);
            loader.setController(labelController);

            newStage.setTitle(resourceBundle.getString("CreateLabel"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(label.getId() == -1) {
                    if(projectDAO.createLabel(label)) {
                        labelObservableList.add(label);
                    }


                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
