package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dal.LabelModel;
import ba.unsa.etf.rpr.projekat.dal.NoteModel;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dto.LabelColor;
import ba.unsa.etf.rpr.projekat.dto.Note;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LabelListCellController extends ListCell<ba.unsa.etf.rpr.projekat.dto.Label> {

    private final LabelModel labelModel;
    private final NoteModel noteModel;
    private final List<ba.unsa.etf.rpr.projekat.dto.Label> labels;
    @FXML
    public Label groupItemDescriptionLabel;
    @FXML
    public Label groupItemNameLabel;
    @FXML
    public VBox groupItemVbox;
    @FXML
    public HBox box;

    ba.unsa.etf.rpr.projekat.dto.Label label;

    private FXMLLoader mLLoader;

    public LabelListCellController (List<ba.unsa.etf.rpr.projekat.dto.Label> labels) {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance ();
        this.labelModel = databaseConnection.getLabelModel ();
        this.noteModel = databaseConnection.getNoteModel ();
        this.labels = labels;
    }

    @Override
    protected void updateItem(ba.unsa.etf.rpr.projekat.dto.Label label, boolean empty) {
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

            groupItemNameLabel.setText(label.getLabelName());
            label.labelNameProperty().addListener((obs, oldName, newName) -> {
                if(oldName != null) {
                    groupItemNameLabel.setText(oldName);
                }
                if(newName != null) {
                    groupItemNameLabel.setText(newName);
                }
            });
            groupItemDescriptionLabel.setText(label.getDescription());
            label.descriptionProperty().addListener((obs, oldName, newName) -> {
                if(oldName != null) {
                    groupItemDescriptionLabel.setText(oldName);
                }
                if(newName != null) {
                    groupItemDescriptionLabel.setText(newName);
                }
            });
            groupItemVbox.setStyle("-fx-background-color: " + label.getLabelColor().getHexCode());
            label.labelColorProperty().addListener((obs, oldColor, newColor) -> {
                if(oldColor != null) {
                    groupItemVbox.setStyle("-fx-background-color: " + LabelColor.valueOf(oldColor).getHexCode());
                }
                if(newColor != null) {
                    groupItemVbox.setStyle("-fx-background-color: " + LabelColor.valueOf(newColor).getHexCode());
                }


            });

            Button button = new Button ();
            button.setId ("label" + label.getId ());
            button.setOnAction (e -> openInformation ());
            button.getStyleClass ().add ("infoButton");
            box.getChildren ().add (button);

            setText(null);
            setGraphic(groupItemVbox);
        }

    }

    public void openInformation() {
        try {
            Stage newStage = new Stage();

            label.setUpdateNeeded(false);

            LabelController labelController = new LabelController(label);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/label.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(labelController);

            newStage.setTitle(MyResourceBundle.getString("LabelInformations"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(label.isUpdateNeeded ()) {
                    labelModel.updateLabel (label);
                }
                if(label.isDelete ()) {
                    labelModel.deleteLabel (label.getId ());
                    for(Note note : noteModel.getNotesForLabel (label.getId ())) {
                        noteModel.getCurrentNotes ().remove (note);
                    }
                    labels.remove (label);
                }

            });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
