package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.model.GroupModel;
import ba.unsa.etf.rpr.projekat.model.NoteColorModel;
import ba.unsa.etf.rpr.projekat.model.*;
import ba.unsa.etf.rpr.projekat.model.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NoteController {
    private final Note note;
    private final List<Label> labels;
    private final List<Group> groups;
    private final ResourceBundle resourceBundle;
    private NoteColorModel noteColorModel;
    private String color = null;
    private String groupName = null;

    private GroupModel groupModel;

    @FXML
    public MenuBar notesMenuBar;
    @FXML
    public FlowPane noteFlowPane;
    @FXML
    public ChoiceBox<String> noteColorChoiceBox;
    @FXML
    public ChoiceBox<String> noteGroupChoiceBox;
    @FXML
    public Button noteOkButton;
    @FXML
    public Button noteCancelButton;
    @FXML
    public javafx.scene.control.Label noteTitleLabel;
    @FXML
    public TextField noteNameTextField;
    @FXML
    public TextArea noteDescriptionTextArea;
    @FXML
    public javafx.scene.control.Label noteNameErrorLabel;
    @FXML
    public javafx.scene.control.Label noteGroupErrorLabel;
    @FXML
    public javafx.scene.control.Label noteColorErrorLabel;
    @FXML
    public ImageView noteImage;
    @FXML
    public Button imageButton;

    public NoteController(Note note, List<Label> labels, List<Group> groups, ResourceBundle resourceBundle) {
        this.note = note;
        this.labels = labels;
        this.groups = groups;
        this.resourceBundle = resourceBundle;
    }

    @FXML
    public void initialize () {

        for(Label label : labels) {
            CheckBox checkBox = new CheckBox();
            checkBox.setText(label.getLabelName());
            checkBox.setStyle("-fx-background-color: " + label.getLabelColor().getHexCode());
            checkBox.getStyleClass().add("checkbox");
            checkBox.setCursor(Cursor.HAND);
            checkBox.setId ("labelId" + label.getId ());
            noteFlowPane.getChildren().add(checkBox);
        }

        if(labels.isEmpty ()) {
            javafx.scene.control.Label label = new javafx.scene.control.Label ();
            label.setText (resourceBundle.getString ("DontHaveAnyLabels"));
            label.getStyleClass ().add ("paragraph");
            noteFlowPane.getChildren ().add (label);
        }

        groupModel = new GroupModel(groups);
        noteGroupChoiceBox.setItems(groupModel.getGroups());

        groupModel.currentGroupProperty().addListener((obp, oldName, newName) -> {
            groupName = newName;
        });

        noteColorModel = new NoteColorModel();
        noteColorChoiceBox.setItems(noteColorModel.getColors());

        noteColorModel.currentColorProperty().addListener((obp, oldColor, newColor) -> {
            String hex = NoteColor.valueOf(newColor).getHexCode();
            changeColor(hex);
            color = newColor;
        });

        if(note.getId() == -2) {
            noteTitleLabel.setText(resourceBundle.getString("CreateANewNote"));
            noteColorChoiceBox.getSelectionModel().selectFirst();
            noteGroupChoiceBox.getSelectionModel().selectFirst();
            notesMenuBar.setVisible (false);

        } else {
            noteNameTextField.setText(note.getNoteTitle());
            noteDescriptionTextArea.setText(note.getDescription());
            noteColorChoiceBox.getSelectionModel().select(note.getNoteColor().name());
            noteGroupChoiceBox.getSelectionModel().select(groupModel.getNameFromId(note.getGroupId ()));
            for(Label label : note.getLabels ()) {
                var node = noteFlowPane.getChildren ().stream ().filter (n -> n.getId ().equals ("labelId" + label.getId ())).findAny ();
                node.ifPresent (value -> ((CheckBox) value).setSelected (true));
            }

            setEditFalse ();
        }

    }

    private void setEditFalse() {
        noteTitleLabel.setText(resourceBundle.getString("NoteInformations"));
        noteOkButton.setVisible (false);
        noteOkButton.setDisable (true);
        imageButton.setDisable (true);
        noteColorChoiceBox.setDisable (true);
        noteGroupChoiceBox.setDisable (true);
        noteDescriptionTextArea.setDisable (true);
        noteNameTextField.setDisable (true);
        noteFlowPane.getChildren ().forEach (node1 -> node1.setDisable (true));
    }



    public void getPictureForNote(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resourceBundle.getString ("GetImage"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File file = fileChooser.showOpenDialog(noteTitleLabel.getContextMenu());
        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                noteImage.setImage(image);
                FileInputStream fileInputStream = new FileInputStream (file);


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                for (int readNum; (readNum = fileInputStream.read(buf)) != -1;){
                    byteArrayOutputStream.write(buf, 0, readNum);
                }
                fileInputStream.close();

                note.setImage (buf);

            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
    }

    public void changeCurrentColor() {
        noteColorModel.setCurrentColor(noteColorChoiceBox.getValue());
    }

    public void changeCurrentGroup() {
        groupModel.setCurrentGroup (noteGroupChoiceBox.getValue());
    }

    private void changeColor(String hex) {
        noteTitleLabel.setStyle("-fx-text-fill: " + hex);
        noteColorChoiceBox.setStyle("-fx-background-color: "+ hex);
        noteGroupChoiceBox.setStyle("-fx-background-color: "+ hex);
        noteCancelButton.setStyle("-fx-border-color: " + hex);
        noteOkButton.setStyle("-fx-border-color: " + hex);
        noteDescriptionTextArea.setStyle("-fx-background-color: "+ hex);
    }

    public void confirmNoteChanges(ActionEvent actionEvent) {
        boolean isAlertNeeded = false;

        noteNameTextField.getStyleClass().remove("turnRed");
        noteNameErrorLabel.getStyleClass().remove("errorLabel");
        noteNameErrorLabel.setText("");
        noteColorErrorLabel.getStyleClass().remove("errorLabel");
        noteColorErrorLabel.setText("");
        noteGroupErrorLabel.getStyleClass ().remove ("errorLabel");
        noteGroupErrorLabel.setText ("");

        String noteName = noteNameTextField.getText();
        if(noteName.length() == 0) {
            isAlertNeeded = true;
            noteNameErrorLabel.setText(resourceBundle.getString("ThisCantBeEmpty"));
            noteNameErrorLabel.getStyleClass().add("errorLabel");
            noteNameTextField.getStyleClass().add("turnRed");
        } else if(groups.stream().anyMatch(g -> g.getGroupName().equals(noteName))) {
            noteNameErrorLabel.setText(resourceBundle.getString("NewGroupNameError"));
            noteNameErrorLabel.getStyleClass().add("errorLabel");
            noteNameTextField.getStyleClass().add("turnRed");
        }

        if(color == null) {
            isAlertNeeded = true;
            noteColorErrorLabel.getStyleClass().add("errorLabel");
            noteColorErrorLabel.setText(resourceBundle.getString("NoteColorNotChosen"));
        }

        if(groupName == null) {
            isAlertNeeded = true;
            noteGroupErrorLabel.getStyleClass().add("errorLabel");
            noteGroupErrorLabel.setText(resourceBundle.getString("NoteGroupNotChosen"));
        }

        if(isAlertNeeded) {
            openAlertMessage();
        } else if(note.getId() == -2){

            setTheNoteatributes (noteName);
            note.setId(-1);
            note.setUpdateNeeded(false);

            Node n = (Node) actionEvent.getSource();
            Stage stage = (Stage) n.getScene().getWindow();
            stage.close();
        } else {
            note.setUpdateNeeded (true);
            setTheNoteatributes (noteName);

            Node n = (Node) actionEvent.getSource();
            Stage stage = (Stage) n.getScene().getWindow();
            stage.close();

        }

    }

    private void setTheNoteatributes(String noteName) {
        note.setNoteTitle (noteName);
        note.setDescription(noteDescriptionTextArea.getText());
        note.setNoteColor (NoteColor.valueOf(color));
        note.setGroupId (groupModel.getIdFromName (groupName));

        List<Label> labelsList = new ArrayList<> ();
        if(!labels.isEmpty ())
            for(Node node : noteFlowPane.getChildren ()){
                CheckBox checkBox = (CheckBox) node;
                if(checkBox.isSelected ()) {
                    labelsList.add (labels.stream().filter(l -> l.getLabelName().equals (checkBox.getText())).findFirst().get());
                }
            }

        note.setLabels (labelsList);

    }

    private void openAlertMessage () {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resourceBundle.getString("Error"));
        alert.setHeaderText(resourceBundle.getString("ProblemPreformingTheAction"));
        alert.setContentText(resourceBundle.getString("PleaseTryAgain"));

        alert.showAndWait();
    }

    public void cancelNoteChanges(ActionEvent actionEvent) {
        note.setUpdateNeeded(false);
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void fileSave() {
        FileChooser izbornik = new FileChooser();
        izbornik.setTitle(resourceBundle.getString ("ChooseFile"));
        izbornik.getExtensionFilters().add(new FileChooser.ExtensionFilter(resourceBundle.getString ("TextFile"), "*.txt"));
        File file = izbornik.showSaveDialog(noteFlowPane.getScene().getWindow());

        if(file == null) return;
        try {
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            fileWriter.write(note.writeInFile(resourceBundle));
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
        note.setUpdateNeeded (false);
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void editEditNote() {
        noteTitleLabel.setText(resourceBundle.getString("NoteInformations"));
        noteOkButton.setVisible (true);
        noteOkButton.setDisable (false);
        imageButton.setDisable (false);
        noteColorChoiceBox.setDisable (false);
        noteGroupChoiceBox.setDisable (false);
        noteDescriptionTextArea.setDisable (false);
        noteNameTextField.setDisable (false);
        noteFlowPane.getChildren ().forEach (node1 -> node1.setDisable (false));

    }

    public void editDelete() {

    }

    public void helpUserGuide() {

    }

    public void helpAbout() {

    }




}
