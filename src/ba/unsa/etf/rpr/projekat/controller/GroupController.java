package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.model.*;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class GroupController {


    private final List<Note> notes;
    private final HostServices hostServices;
    private Group group;
    private final List<Group> groups;
    private final ResourceBundle resourceBundle;
    private GroupColorModel groupColorModel;
    private String color = null;

    @FXML
    public Label groupsTitleLabel;
    @FXML
    public TextField groupNameTextField;
    @FXML
    public TextArea groupDescriptionTextField;
    @FXML
    public ChoiceBox<String> groupColorChoiceBox;
    @FXML
    public GridPane groupGridPane;
    @FXML
    public Label groupNameErrorLabel;
    @FXML
    public Label groupColorErrorLabel;
    @FXML
    public Button groupCancelButton;
    @FXML
    public Button groupOkButton;
    @FXML
    public MenuBar groupMenuBar;

    public GroupController(Group group, List<Group> groups, List<Note> notes, ResourceBundle resourceBundle, HostServices hostServices) {
        this.group = group;
        this.groups = groups;
        this.notes = notes;
        this.resourceBundle = resourceBundle;
        this.hostServices = hostServices;

    }

    @FXML
    public void initialize() {
        groupColorModel = new GroupColorModel();
        groupColorChoiceBox.setItems(groupColorModel.getColors());

        groupColorModel.currentColorProperty().addListener((obp, oldColor, newColor) -> {
            String hex = GroupColor.valueOf(newColor).getHexCode();
            changeColor(hex);
            color = newColor;
        });

        if(group.getId() == -2) {
            groupsTitleLabel.setText(resourceBundle.getString("CreateANewGroup"));
            groupColorChoiceBox.getSelectionModel().selectFirst();
            groupMenuBar.setVisible (false);
        } else {
            groupNameTextField.setText(group.getGroupName());
            groupDescriptionTextField.setText(group.getDescription());
            groupColorModel.setCurrentColor(group.getGroupColor().toString());
            groupColorChoiceBox.getSelectionModel().select(group.getGroupColor().toString());

            setEditFalse ();
        }

    }

    private void changeColor(String hex) {
        groupsTitleLabel.setStyle("-fx-text-fill: " + hex);
        groupColorChoiceBox.setStyle("-fx-background-color: "+ hex);
        groupCancelButton.setStyle("-fx-border-color: " + hex);
        groupOkButton.setStyle("-fx-border-color: " + hex);
    }

    public void changeCurrentColor() {
        groupColorModel.setCurrentColor(groupColorChoiceBox.getValue());

    }

    public void cancelGroupChanges(ActionEvent actionEvent) {
        group.setUpdatedNeeded(false);
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void confirmGroupChanges(ActionEvent actionEvent) {
        boolean isAlertNeeded = false;

        groupNameTextField.getStyleClass().remove("turnRed");
        groupNameErrorLabel.getStyleClass().remove("errorLabel");
        groupNameErrorLabel.setText("");
        groupColorErrorLabel.getStyleClass().remove("errorLabel");
        groupColorErrorLabel.setText("");

        String groupName = groupNameTextField.getText();
        if(groupName.length() == 0) {
            isAlertNeeded = true;
            groupNameErrorLabel.setText(resourceBundle.getString("ThisCantBeEmpty"));
            groupNameErrorLabel.getStyleClass().add("errorLabel");
            groupNameTextField.getStyleClass().add("turnRed");
        } else if(groups.stream().anyMatch(g -> g.getGroupName().equals(groupName) && group.getId () != g.getId ())) {
            isAlertNeeded = true;
            groupNameErrorLabel.setText(resourceBundle.getString("NewGroupNameError"));
            groupNameErrorLabel.getStyleClass().add("errorLabel");
            groupNameTextField.getStyleClass().add("turnRed");
        }

        if(color == null) {
            isAlertNeeded = true;
            groupColorErrorLabel.getStyleClass().add("errorLabel");
            groupColorErrorLabel.setText(resourceBundle.getString("GroupColorNotChosen"));
        }

        if(isAlertNeeded) {
            openAlertMessage();
        } else {
            if(group.getId() == -2){
                group.setId(-1);
                group.setGroupName(groupName);
                group.setDescription(groupDescriptionTextField.getText());
                group.setGroupColor(GroupColor.valueOf(color));


                Node n = (Node) actionEvent.getSource();
                Stage stage = (Stage) n.getScene().getWindow();
                stage.close();
            } else {
                group.setGroupName(groupName);
                group.setDescription(groupDescriptionTextField.getText());
                group.setGroupColor(GroupColor.valueOf(color));
                group.setUpdatedNeeded(true);

                Node n = (Node) actionEvent.getSource();
                Stage stage = (Stage) n.getScene().getWindow();
                stage.close();
            }
        }
    }

    private void setEditFalse() {
        groupsTitleLabel.setText(resourceBundle.getString("GroupInformation"));
        groupOkButton.setVisible (false);
        groupOkButton.setDisable (true);
        groupColorChoiceBox.setDisable (true);
        groupDescriptionTextField.setDisable (true);
        groupNameTextField.setDisable (true);
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
        File file = izbornik.showSaveDialog(groupColorChoiceBox.getScene().getWindow());

        if(file == null) return;
        try {
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            fileWriter.write(group.writeInFile(notes, resourceBundle));
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
        group.setUpdatedNeeded (false);
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void editEditNote() {
        groupsTitleLabel.setText(resourceBundle.getString("UpdateGroup"));
        groupOkButton.setVisible (true);
        groupOkButton.setDisable (false);
        groupColorChoiceBox.setDisable (false);
        groupDescriptionTextField.setDisable (false);
        groupNameTextField.setDisable (false);

    }

    public void editDelete() {

    }

    public void helpUserGuide() {
        try {
            Stage newUserGuideStage = new Stage();

            HelpController helpController = new HelpController (resourceBundle);


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help.fxml"), resourceBundle);
            loader.setController(helpController);

            newUserGuideStage.setTitle(resourceBundle.getString("UserGuideTitle"));
            newUserGuideStage.setScene(new Scene(loader.load(), 700, 500));
            newUserGuideStage.setMinHeight(500);
            newUserGuideStage.setMinWidth(700);

            newUserGuideStage.show();
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
