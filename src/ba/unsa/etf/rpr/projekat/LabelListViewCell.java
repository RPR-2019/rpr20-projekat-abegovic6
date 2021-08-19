package ba.unsa.etf.rpr.projekat;

import ba.unsa.etf.rpr.projekat.controller.GroupController;
import ba.unsa.etf.rpr.projekat.controller.LabelController;
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

public class LabelListViewCell extends ListCell<ba.unsa.etf.rpr.projekat.model.Label> {
    private final ResourceBundle resourceBundle;
    private final ProjectDAO projectDAO;
    private final List<ba.unsa.etf.rpr.projekat.model.Label> labels;
    @FXML
    public Label groupItemDescriptionLabel;
    @FXML
    public Label groupItemNameLabel;
    @FXML
    public VBox groupItemVbox;

    ba.unsa.etf.rpr.projekat.model.Label label;

    private FXMLLoader mLLoader;

    public LabelListViewCell(List<ba.unsa.etf.rpr.projekat.model.Label> labels, ProjectDAO projectDAO, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.projectDAO = projectDAO;
        this.labels = labels;
    }

    @Override
    protected void updateItem(ba.unsa.etf.rpr.projekat.model.Label label, boolean empty) {
        super.updateItem(label, empty);

        this.label = label;


        if(empty || label == null) {

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

            groupItemNameLabel.setText(String.valueOf(label.getLabelName()));
            groupItemDescriptionLabel.setText(String.valueOf(label.getDescription()));
            groupItemVbox.setStyle("-fx-background-color: " + label.getLabelColor().getHexCode());

            setText(null);
            setGraphic(groupItemVbox);
        }

    }

    public void openInformation(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();

            label.setUpdateNeeded(false);

            LabelController labelController = new LabelController(label, labels, null, resourceBundle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/label.fxml"), resourceBundle);
            loader.setController(labelController);

            newStage.setTitle(resourceBundle.getString("LabelInformation"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(label.isUpdateNeeded()) {
                    projectDAO.updateLabel(label);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
