package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dal.LabelModel;
import ba.unsa.etf.rpr.projekat.dal.NoteModel;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dal.LabelColorModel;
import ba.unsa.etf.rpr.projekat.dto.LabelColor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class LabelController {

    private final NoteModel noteModel;
    private final LabelModel labelModel;
    private final ba.unsa.etf.rpr.projekat.dto.Label label;

    private LabelColorModel labelColorModel;
    private String color = null;
    @FXML
    public MenuBar labelMenuBar;
    @FXML
    public Label labelTitleLabel;
    @FXML
    public TextField labelNameTextField;
    @FXML
    public TextArea labelDescriptionTextField;
    @FXML
    public ChoiceBox<String> labelColorChoiceBox;
    @FXML
    public Label labelNameErrorLabel;
    @FXML
    public Label labelColorErrorLabel;
    @FXML
    public Button labelOkButton;
    @FXML
    public Button labelCancelButton;

    public LabelController(ba.unsa.etf.rpr.projekat.dto.Label label) {
        this.label = label;
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance ();
        this.noteModel = databaseConnection.getNoteModel ();
        this.labelModel = databaseConnection.getLabelModel ();

    }

    @FXML
    public void initialize() {
        labelColorModel = new LabelColorModel();
        labelColorChoiceBox.setItems(labelColorModel.getColors());

        labelColorModel.currentColorProperty().addListener((obp, oldColor, newColor) -> {
            String hex = LabelColor.valueOf(newColor).getHexCode();
            changeColor (hex);
            color = newColor;
        });

        if(label.getId() == -2) {
            labelTitleLabel.setText(MyResourceBundle.getString("CreateANewLabel"));
            labelColorChoiceBox.getSelectionModel ().selectFirst ();
            labelMenuBar.setVisible (false);
        } else {
            labelNameTextField.setText(label.getLabelName());
            labelDescriptionTextField.setText(label.getDescription());
            labelColorModel.setCurrentColor(label.getLabelColor().toString());
            labelColorChoiceBox.getSelectionModel ().select (label.getLabelColor ().name ());
            setEditFalse ();
        }
    }

    private void changeColor(String hex) {
        labelTitleLabel.setStyle("-fx-text-fill: " + hex);
        labelColorChoiceBox.setStyle("-fx-background-color: "+ hex);
        labelOkButton.setStyle("-fx-border-color: " + hex);
        labelCancelButton.setStyle("-fx-border-color: " + hex);
    }

    private void setEditFalse() {
        labelTitleLabel.setText(MyResourceBundle.getString("LabelInformation"));
        labelOkButton.setVisible (false);
        labelOkButton.setDisable (true);
        labelColorChoiceBox.setDisable (true);
        labelDescriptionTextField.setDisable (true);
        labelNameTextField.setDisable (true);
    }

    public void changeCurrentColor() {
        labelColorModel.setCurrentColor(labelColorChoiceBox.getValue());

    }

    public void cancelLabelChanges(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void confirmLabelChanges(ActionEvent actionEvent) {
        boolean isAlertNeeded = false;

        labelNameTextField.getStyleClass().remove("turnRed");
        labelNameErrorLabel.getStyleClass().remove("errorLabel");
        labelNameErrorLabel.setText("");
        labelColorErrorLabel.getStyleClass().remove("errorLabel");
        labelColorErrorLabel.setText("");

        String groupName = labelNameTextField.getText();
        if(groupName.length() == 0) {
            isAlertNeeded = true;
            labelNameErrorLabel.setText(MyResourceBundle.getString("ThisCantBeEmpty"));
            labelNameErrorLabel.getStyleClass().add("errorLabel");
            labelNameTextField.getStyleClass().add("turnRed");
        } else if(labelModel.getAllLabels ().stream()
                .anyMatch(g -> g.getLabelName().equals(groupName) && g.getId () != label.getId ())) {
            isAlertNeeded = true;
            labelNameErrorLabel.setText(MyResourceBundle.getString("NewLabelNameError"));
            labelNameErrorLabel.getStyleClass().add("errorLabel");
            labelNameTextField.getStyleClass().add("turnRed");
        }

        if(color == null) {
            isAlertNeeded = true;
            labelColorErrorLabel.getStyleClass().add("errorLabel");
            labelColorErrorLabel.setText(MyResourceBundle.getString("LabelColorNotChosen"));
        }

        if(isAlertNeeded) {
            openAlertMessage();
        } else if(label.getId () == -2){
            label.setId(-1);
            label.setLabelName(groupName);
            label.setDescription(labelDescriptionTextField.getText());
            label.setLabelColor(LabelColor.valueOf(color));

            Node n = (Node) actionEvent.getSource();
            Stage stage = (Stage) n.getScene().getWindow();
            stage.close();
        } else {
            label.setUpdateNeeded (true);
            label.setLabelName(groupName);
            label.setDescription(labelDescriptionTextField.getText());
            label.setLabelColor(LabelColor.valueOf(color));

            Node n = (Node) actionEvent.getSource();
            Stage stage = (Stage) n.getScene().getWindow();
            stage.close();

        }
    }

    private void openAlertMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(MyResourceBundle.getString("Error"));
        alert.setHeaderText(MyResourceBundle.getString("ProblemPreformingTheAction"));
        alert.setContentText(MyResourceBundle.getString("PleaseTryAgain"));

        alert.showAndWait();

    }

    public void fileSave() {
        FileChooser izbornik = new FileChooser();
        izbornik.setTitle(MyResourceBundle.getString ("ChooseFile"));
        izbornik.getExtensionFilters().add(new FileChooser.ExtensionFilter(MyResourceBundle.getString ("TextFile"), "*.txt"));
        File file = izbornik.showSaveDialog(labelColorChoiceBox.getScene().getWindow());

        if(file == null) return;
        try {
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            fileWriter.write(label.writeInFile(noteModel.getNotesForLabel (label.getId ())));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void fileExit() {
        label.setUpdateNeeded (false);
        Node n = labelOkButton;
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void editEditNote() {
        labelTitleLabel.setText(MyResourceBundle.getString("UpdateLabel"));
        labelOkButton.setVisible (true);
        labelOkButton.setDisable (false);
        labelColorChoiceBox.setDisable (false);
        labelDescriptionTextField.setDisable (false);
        labelNameTextField.setDisable (false);

    }

    public void editDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(labelOkButton.getContextMenu ());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(MyResourceBundle.getString ("DeleteThis"));
        alert.setHeaderText(MyResourceBundle.getString ("DeleteThis"));
        alert.setContentText(MyResourceBundle.getString ("AreYouSure"));

        label.setDelete (false);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            label.setDelete (true);
        }

        Node n = labelOkButton;
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void helpUserGuide() {
        try {
            Stage newStage = new Stage();

            HelpController helpController = new HelpController ();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(helpController);

            newStage.setTitle(MyResourceBundle.getString("UserGuideTitle"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void helpAbout() {
        try {
            Stage newStage = new Stage();

            AboutController aboutController = new AboutController ();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/about.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(aboutController);

            newStage.setTitle(MyResourceBundle.getString("about"));
            newStage.setScene(new Scene (loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
