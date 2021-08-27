package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.model.LabelColorModel;
import ba.unsa.etf.rpr.projekat.model.Account;
import ba.unsa.etf.rpr.projekat.model.LabelColor;
import ba.unsa.etf.rpr.projekat.model.Note;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class LabelController {

    private final List<Note> notes;
    private final HostServices hostServices;
    private ba.unsa.etf.rpr.projekat.model.Label label;
    private final List<ba.unsa.etf.rpr.projekat.model.Label> labels;
    private final ResourceBundle resourceBundle;
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
    public GridPane labelGridPane;
    @FXML
    public Label labelNameErrorLabel;
    @FXML
    public Label labelColorErrorLabel;
    @FXML
    public Button labelOkButton;
    @FXML
    public Button labelCancelButton;

    public LabelController(ba.unsa.etf.rpr.projekat.model.Label label, List<ba.unsa.etf.rpr.projekat.model.Label> labels,
                           List<Note> notes, ResourceBundle resourceBundle, HostServices hostServices) {
        this.label = label;
        this.labels = labels;
        this.notes = notes;
        this.resourceBundle = resourceBundle;
        this.hostServices = hostServices;

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
            labelTitleLabel.setText(resourceBundle.getString("CreateANewLabel"));
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
        labelTitleLabel.setText(resourceBundle.getString("LabelInformation"));
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
            labelNameErrorLabel.setText(resourceBundle.getString("ThisCantBeEmpty"));
            labelNameErrorLabel.getStyleClass().add("errorLabel");
            labelNameTextField.getStyleClass().add("turnRed");
        } else if(labels.stream().anyMatch(g -> g.getLabelName().equals(groupName) && g.getId () != label.getId ())) {
            isAlertNeeded = true;
            labelNameErrorLabel.setText(resourceBundle.getString("NewLabelNameError"));
            labelNameErrorLabel.getStyleClass().add("errorLabel");
            labelNameTextField.getStyleClass().add("turnRed");
        }

        if(color == null) {
            isAlertNeeded = true;
            labelColorErrorLabel.getStyleClass().add("errorLabel");
            labelColorErrorLabel.setText(resourceBundle.getString("LabelColorNotChosen"));
        }

        if(isAlertNeeded) {
            openAlertMessage();
        } else {
            label.setId(-1);
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
        alert.setTitle(resourceBundle.getString("Error"));
        alert.setHeaderText(resourceBundle.getString("ProblemPreformingTheAction"));
        alert.setContentText(resourceBundle.getString("PleaseTryAgain"));

        alert.showAndWait();

    }

    public void fileSave() {
        FileChooser izbornik = new FileChooser();
        izbornik.setTitle(resourceBundle.getString ("ChooseFile"));
        izbornik.getExtensionFilters().add(new FileChooser.ExtensionFilter(resourceBundle.getString ("TextFile"), "*.txt"));
        File file = izbornik.showSaveDialog(labelColorChoiceBox.getScene().getWindow());

        if(file == null) return;
        try {
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            fileWriter.write(label.writeInFile(notes, resourceBundle));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void filePrint() {

    }

    public void fileSettings() {

    }

    public void fileExit(ActionEvent actionEvent) {
        label.setUpdateNeeded (false);
        Node n = (Node) labelOkButton;
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void editEditNote() {
        labelTitleLabel.setText(resourceBundle.getString("UpdateLabel"));
        labelOkButton.setVisible (true);
        labelOkButton.setDisable (false);
        labelColorChoiceBox.setDisable (false);
        labelDescriptionTextField.setDisable (false);
        labelNameTextField.setDisable (false);

    }

    public void editDelete(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(labelOkButton.getContextMenu ());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(resourceBundle.getString ("DeleteThis"));
        alert.setHeaderText(resourceBundle.getString ("DeleteThis"));
        alert.setContentText(resourceBundle.getString ("AreYouSure"));

        label.setDelete (false);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            label.setDelete (true);
        }

        Node n = (Node) labelOkButton;
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void helpUserGuide() {
        try {
            Stage newStage = new Stage();

            HelpController helpController = new HelpController (resourceBundle);


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help.fxml"), resourceBundle);
            loader.setController(helpController);

            newStage.setTitle(resourceBundle.getString("UserGuideTitle"));
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

            aboutController.setHostServices (hostServices);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/about.fxml"), resourceBundle);
            loader.setController(aboutController);

            newStage.setTitle(resourceBundle.getString("about"));
            newStage.setScene(new Scene (loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
