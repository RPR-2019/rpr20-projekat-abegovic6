package ba.unsa.etf.rpr.projekat;

import ba.unsa.etf.rpr.projekat.controller.GroupController;
import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Group;
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

public class GroupListViewCell extends ListCell<Group> {
    private final List<Group> groups;
    private final ResourceBundle resourceBundle;
    private final ProjectDAO projectDAO;
    @FXML
    public Label groupItemDescriptionLabel;
    @FXML
    public Label groupItemNameLabel;
    @FXML
    public VBox groupItemVbox;

    Group group;

    private FXMLLoader mLLoader;

    public GroupListViewCell(List<Group> groups, ProjectDAO projectDAO, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.projectDAO = projectDAO;
        this.groups = groups;
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

            groupItemNameLabel.setText(String.valueOf(group.getGroupName()));
            groupItemDescriptionLabel.setText(String.valueOf(group.getDescription()));
            groupItemVbox.setStyle("-fx-background-color: " + group.getGroupColor().getHexCode());



            setText(null);
            setGraphic(groupItemVbox);
        }

    }

    public void openInformation(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();

            group.setUpdatedNeeded(false);

            GroupController groupController = new GroupController(group, groups, null, resourceBundle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/group.fxml"), resourceBundle);
            loader.setController(groupController);

            newStage.setTitle(resourceBundle.getString("GroupInformation"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(group.isUpdatedNeeded()) {
                    projectDAO.updateGroup(group);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
