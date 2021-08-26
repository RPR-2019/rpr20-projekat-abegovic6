package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.controller.GroupController;
import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Group;
import ba.unsa.etf.rpr.projekat.model.GroupColor;
import ba.unsa.etf.rpr.projekat.model.Note;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GroupListCellController extends ListCell<Group> {
    private final List<Group> groups;
    private final ResourceBundle resourceBundle;
    private final ProjectDAO projectDAO;
    private final List<Note> notes;
    @FXML
    public Label groupItemDescriptionLabel;
    @FXML
    public Label groupItemNameLabel;
    @FXML
    public VBox groupItemVbox;

    Group group;

    private FXMLLoader mLLoader;

    public GroupListCellController (List<Group> groups, ProjectDAO projectDAO, List<Note> notes, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.projectDAO = projectDAO;
        this.groups = groups;
        this.notes = notes;
    }

    @Override
    protected void updateItem(Group group, boolean empty) {
        super.updateItem(group, empty);

        this.group = group;
        if(empty || group == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/fxml/listviewitem.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            groupItemNameLabel.setText(group.getGroupName());
            group.groupNameProperty().addListener((obs, oldName, newName) -> {
                if(oldName != null) {
                    groupItemNameLabel.setText(oldName);
                }
                if(newName != null) {
                    groupItemNameLabel.setText(newName);
                }
            });
            groupItemDescriptionLabel.setText(group.getDescription());
            group.descriptionProperty().addListener((obs, oldName, newName) -> {
                if(oldName != null) {
                    groupItemDescriptionLabel.setText(oldName);
                }
                if(newName != null) {
                    groupItemDescriptionLabel.setText(newName);
                }
            });
            groupItemVbox.setStyle("-fx-background-color: " + group.getGroupColor().getHexCode());
            group.groupColorProperty().addListener((obs, oldColor, newColor) -> {
                if(oldColor != null) {
                    groupItemVbox.setStyle("-fx-background-color: " + GroupColor.valueOf(oldColor).getHexCode());
                }
                if(newColor != null) {
                    groupItemVbox.setStyle("-fx-background-color: " + GroupColor.valueOf(newColor).getHexCode());
                }


            });

            setText(null);
            setGraphic(groupItemVbox);
        }

    }

    public void openInformation(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();

            group.setUpdatedNeeded(false);

            List<Note> notesForGroup = notes.stream().filter (note -> note.getGroupId () == group.getId ()).collect(Collectors.toList ());

            GroupController groupController = new GroupController(group, groups, notesForGroup, resourceBundle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/group.fxml"), resourceBundle);
            loader.setController(groupController);

            newStage.setTitle(resourceBundle.getString("GroupInformation"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(group.isUpdatedNeeded()) {
                    System.out.println ("udje??");
                    projectDAO.updateGroup(group);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
