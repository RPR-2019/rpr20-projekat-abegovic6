package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.model.GroupColorModel;
import ba.unsa.etf.rpr.projekat.model.GroupModel;
import ba.unsa.etf.rpr.projekat.model.NoteModel;
import ba.unsa.etf.rpr.projekat.ProjectDAO;
import ba.unsa.etf.rpr.projekat.javabean.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

public class GroupController {


    private final ProjectDAO projectDAO;
    private final NoteModel noteModel;
    private final GroupModel groupModel;
    private final Group group;
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

    public GroupController(Group group) {
        this.group = group;

        this.projectDAO = ProjectDAO.getInstance ();
        this.noteModel = projectDAO.getNoteModel ();
        this.groupModel = projectDAO.getGroupModel ();
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
            groupsTitleLabel.setText(MyResourceBundle.getString("CreateANewGroup"));
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
            groupNameErrorLabel.setText(MyResourceBundle.getString("ThisCantBeEmpty"));
            groupNameErrorLabel.getStyleClass().add("errorLabel");
            groupNameTextField.getStyleClass().add("turnRed");
        } else if(groupModel.getAllGroups ().stream()
                .anyMatch(g -> g.getGroupName().equals(groupName) && group.getId () != g.getId ())) {
            isAlertNeeded = true;
            groupNameErrorLabel.setText(MyResourceBundle.getString("NewGroupNameError"));
            groupNameErrorLabel.getStyleClass().add("errorLabel");
            groupNameTextField.getStyleClass().add("turnRed");
        }

        if(color == null) {
            isAlertNeeded = true;
            groupColorErrorLabel.getStyleClass().add("errorLabel");
            groupColorErrorLabel.setText(MyResourceBundle.getString("GroupColorNotChosen"));
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
        groupsTitleLabel.setText(MyResourceBundle.getString("GroupInformation"));
        groupOkButton.setVisible (false);
        groupOkButton.setDisable (true);
        groupColorChoiceBox.setDisable (true);
        groupDescriptionTextField.setDisable (true);
        groupNameTextField.setDisable (true);
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
        File file = izbornik.showSaveDialog(groupColorChoiceBox.getScene().getWindow());

        if(file == null) return;
        try {
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            fileWriter.write(group.writeInFile(noteModel.getNotesForGroup (group.getId ())));
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
        Node n = (Node) groupNameTextField;
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void editEditNote() {
        groupsTitleLabel.setText(MyResourceBundle.getString("UpdateGroup"));
        groupOkButton.setVisible (true);
        groupOkButton.setDisable (false);
        groupColorChoiceBox.setDisable (false);
        groupDescriptionTextField.setDisable (false);
        groupNameTextField.setDisable (false);

    }

    public void editDelete(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(groupNameTextField.getContextMenu ());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(MyResourceBundle.getString ("DeleteThis"));
        alert.setHeaderText(MyResourceBundle.getString ("DeleteThis"));
        alert.setContentText(MyResourceBundle.getString ("AreYouSure"));

        group.setDelete (false);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            group.setDelete (true);
        }

        Node n = (Node) groupNameTextField;
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();

    }

    public void helpUserGuide() {
        try {
            Stage newUserGuideStage = new Stage();

            HelpController helpController = new HelpController ();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help.fxml"),
                    ResourceBundle.getBundle ("Translation", MyResourceBundle.getLocale ()));
            loader.setController(helpController);

            newUserGuideStage.setTitle(MyResourceBundle.getString("UserGuideTitle"));
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/about.fxml"),
                    ResourceBundle.getBundle ("Translation", MyResourceBundle.getLocale ()));
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
