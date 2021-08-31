package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dao.GroupModel;
import ba.unsa.etf.rpr.projekat.dao.NoteModel;
import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Group;
import ba.unsa.etf.rpr.projekat.model.GroupColor;
import ba.unsa.etf.rpr.projekat.model.Note;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class GroupListCellController extends ListCell<Group> {
    private final ProjectDAO projectDAO;
    private final HostServices hostServices;
    private final NoteModel noteModel;
    private final GroupModel groupModel;

    @FXML
    public Label groupItemDescriptionLabel;
    @FXML
    public Label groupItemNameLabel;
    @FXML
    public VBox groupItemVbox;

    private Group group;
    private FXMLLoader mLLoader;

    public GroupListCellController (ProjectDAO projectDAO,
                                    HostServices hostServices) {
        this.projectDAO = projectDAO;
        this.noteModel = this.projectDAO.getNoteModel ();
        this.groupModel = this.projectDAO.getGroupDAO ();
        this.hostServices = hostServices;
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

            var notesForGroup = noteModel.getNotesForGroup (group.getId ());

            GroupController groupController = new GroupController(group, groupModel.getAllGroups (), notesForGroup, hostServices);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/group.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(groupController);

            newStage.setTitle(MyResourceBundle.getString("GroupInformations"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(group.isUpdatedNeeded()) {
                    projectDAO.updateGroup(group);
                }
                if(group.isDelete ()) {
                    projectDAO.deleteGroup (group.getId ());
                    for(Note note : notesForGroup) {
                        noteModel.getAllNotes ().remove (note);
                        if(noteModel.getCurrentNotes ().contains (note)) {
                            noteModel.getCurrentNotes ().remove (note);
                        }
                    }
                    noteModel.getAllNotes ().remove (group);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
