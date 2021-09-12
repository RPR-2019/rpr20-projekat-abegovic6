package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.dto.*;
import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dal.GroupModel;
import ba.unsa.etf.rpr.projekat.dal.LabelModel;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dal.NoteColorModel;
import ba.unsa.etf.rpr.projekat.dto.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.text.WordUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoteController {
    private final Note note;
    private final LabelModel labelModel;
    private final GroupModel groupModel;

    private NoteColorModel noteColorModel;
    private String color = null;
    private String groupName = null;
    private List<TextStyle> textStyles = null;

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
    @FXML
    public ToolBar toolBar;

    public NoteController(Note note) {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance ();
        this.note = note;
        this.labelModel = databaseConnection.getLabelModel ();
        this.groupModel = databaseConnection.getGroupModel ();
    }

    @FXML
    public void initialize () {
        for(Label label : labelModel.getAllLabels ()) {
            CheckBox checkBox = new CheckBox();
            checkBox.setText(label.getLabelName());
            checkBox.setStyle("-fx-background-color: " + label.getLabelColor().getHexCode());
            checkBox.getStyleClass().add("checkbox");
            checkBox.setCursor(Cursor.HAND);
            checkBox.setId ("labelId" + label.getId ());
            noteFlowPane.getChildren().add(checkBox);
        }

        if(labelModel.getAllLabels ().isEmpty ()) {
            javafx.scene.control.Label label = new javafx.scene.control.Label ();
            label.setText (MyResourceBundle.getString ("DontHaveAnyLabels"));
            label.getStyleClass ().add ("paragraph");
            noteFlowPane.getChildren ().add (label);
        }

        textStyles = new ArrayList<> ();


        noteGroupChoiceBox.setItems(groupModel.getGroups());

        groupModel.currentGroupProperty().addListener((obp, oldName, newName) -> {
            if(newName != null)
                groupName = newName;
        });

        groupName = groupModel.getGroups ().get (0);

        noteColorModel = new NoteColorModel();
        noteColorChoiceBox.setItems(noteColorModel.getColors());
        noteColorModel.currentColorProperty().addListener((obp, oldColor, newColor) -> {
            String hex = NoteColor.valueOf(newColor).getHexCode();
            changeColor(hex);
            color = newColor;
        });

        if(note.getId() == -2) {
            noteTitleLabel.setText(MyResourceBundle.getString("CreateANewNote"));
            noteColorChoiceBox.getSelectionModel().selectFirst();
            noteGroupChoiceBox.getSelectionModel().selectFirst();
            notesMenuBar.setVisible (false);
        } else {
            noteNameTextField.setText(note.getNoteTitle());
            noteDescriptionTextArea.setText(note.getDescription());
            if(note.getDescription ().length () > 0) {
                if(note.getTextStyles ().contains (TextStyle.BOLD) && note.getTextStyles ().contains (TextStyle.ITALIC)) {
                    noteDescriptionTextArea.setFont (Font.font (noteDescriptionTextArea.getFont ().getFamily (),
                            FontWeight.BOLD, FontPosture.ITALIC, noteDescriptionTextArea.getFont ().getSize ()));
                    textStyles.add(TextStyle.BOLD);
                    textStyles.add (TextStyle.ITALIC);
                } else if(note.getTextStyles ().contains (TextStyle.BOLD)) {
                    noteDescriptionTextArea.setFont (Font.font (noteDescriptionTextArea.getFont ().getFamily (),
                            FontWeight.BOLD, noteDescriptionTextArea.getFont ().getSize ()));
                    textStyles.add(TextStyle.BOLD);
                } else if(note.getTextStyles ().contains (TextStyle.ITALIC)) {
                    noteDescriptionTextArea.setFont (Font.font (noteDescriptionTextArea.getFont ().getFamily (),
                            FontPosture.ITALIC, noteDescriptionTextArea.getFont ().getSize ()));
                    textStyles.add (TextStyle.ITALIC);
                } else {
                    noteDescriptionTextArea.setFont (Font.font (noteDescriptionTextArea.getFont ().getFamily (),
                            noteDescriptionTextArea.getFont ().getSize ()));
                }
            }
            noteColorChoiceBox.getSelectionModel().select(note.getNoteColor().name());
            noteGroupChoiceBox.getSelectionModel().select(groupModel.getNameFromId(note.getGroupId ()));
            groupName = groupModel.getNameFromId(note.getGroupId ());
            for(Label label : note.getLabels ()) {
                var node = noteFlowPane.getChildren ().stream ().filter
                        (n -> n.getId ().equals ("labelId" + label.getId ())).findAny ();
                node.ifPresent (value -> ((CheckBox) value).setSelected (true));
            }
            setEditFalse ();
        }

    }

    private void setEditFalse() {
        noteTitleLabel.setText(MyResourceBundle.getString("NoteInformation"));
        noteOkButton.setVisible (false);
        toolBar.setDisable (true);
        noteOkButton.setDisable (true);
        imageButton.setDisable (true);
        noteColorChoiceBox.setDisable (true);
        noteGroupChoiceBox.setDisable (true);
        noteDescriptionTextArea.setDisable (true);
        noteNameTextField.setDisable (true);
        noteFlowPane.getChildren ().forEach (node1 -> node1.setDisable (true));
    }



    public void getPictureForNote() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(MyResourceBundle.getString ("GetImage"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File file = fileChooser.showOpenDialog(noteTitleLabel.getContextMenu());
        if (file != null) {
            Image img = new Image(file.toURI().toString());
            noteImage.setImage (img);
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
            noteNameErrorLabel.setText(MyResourceBundle.getString("ThisCantBeEmpty"));
            noteNameErrorLabel.getStyleClass().add("errorLabel");
            noteNameTextField.getStyleClass().add("turnRed");
        } else if(groupModel.getAllGroups ().stream().anyMatch(g -> g.getGroupName().equals(noteName))) {
            isAlertNeeded = true;
            noteNameErrorLabel.setText(MyResourceBundle.getString("NewGroupNameError"));
            noteNameErrorLabel.getStyleClass().add("errorLabel");
            noteNameTextField.getStyleClass().add("turnRed");
        }

        if(color == null) {
            isAlertNeeded = true;
            noteColorErrorLabel.getStyleClass().add("errorLabel");
            noteColorErrorLabel.setText(MyResourceBundle.getString("NoteColorNotChosen"));
        }

        if(groupName == null) {
            isAlertNeeded = true;
            noteGroupErrorLabel.getStyleClass().add("errorLabel");
            noteGroupErrorLabel.setText(MyResourceBundle.getString("NoteGroupNotChosen"));
        }

        if(isAlertNeeded) {
            openAlertMessage();
        } else if(note.getId() == -2){
            note.setId(-1);
            note.setUpdateNeeded(false);
            setTheNoteatributes (noteName);
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
        if(!labelModel.getAllLabels ().isEmpty ())
            for(Node node : noteFlowPane.getChildren ()){
                CheckBox checkBox = (CheckBox) node;
                if(checkBox.isSelected ()) {
                    var optional = labelModel.getAllLabels ().stream()
                            .filter(l -> l.getLabelName().equals (checkBox.getText())).findFirst();
                    optional.ifPresent (labelsList::add);
                }
            }

        note.getTextStyles ().clear ();
        note.getTextStyles ().addAll (textStyles);
        note.getLabels ().clear ();
        note.setLabels (labelsList);

    }

    private void openAlertMessage () {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(MyResourceBundle.getString("Error"));
        alert.setHeaderText(MyResourceBundle.getString("ProblemPreformingTheAction"));
        alert.setContentText(MyResourceBundle.getString("PleaseTryAgain"));

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
        izbornik.setTitle(MyResourceBundle.getString ("ChooseFile"));
        izbornik.getExtensionFilters().add(new FileChooser.ExtensionFilter(MyResourceBundle.getString ("TextFile"), "*.txt"));
        File file = izbornik.showSaveDialog(noteFlowPane.getScene().getWindow());

        if(file == null) return;
        try {
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            fileWriter.write(note.writeInFile());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void fileExit() {
        note.setUpdateNeeded (false);
        Node n =  noteTitleLabel;
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void editEditNote() {
        noteTitleLabel.setText(MyResourceBundle.getString("UpdateNote"));
        toolBar.setDisable (false);
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(noteTitleLabel.getContextMenu ());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(MyResourceBundle.getString ("DeleteThis"));
        alert.setHeaderText(MyResourceBundle.getString ("DeleteThis"));
        alert.setContentText(MyResourceBundle.getString ("AreYouSure"));

        note.setDelete (false);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            note.setDelete (true);
        }

        Node n = noteTitleLabel;
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();

    }



    public void boldAction() {
        Font font;
        if(textStyles.contains (TextStyle.BOLD)) {
            textStyles.remove (TextStyle.BOLD);
            if(textStyles.contains (TextStyle.ITALIC)) {
                font = Font.font (noteDescriptionTextArea.getFont ().getFamily (), FontPosture.ITALIC,
                        noteDescriptionTextArea.getFont ().getSize ());
            } else {
                font = Font.font (noteDescriptionTextArea.getFont ().getFamily (),
                        noteDescriptionTextArea.getFont ().getSize ());
            }
        } else {
            textStyles.add (TextStyle.BOLD);
            if(textStyles.contains (TextStyle.ITALIC)) {
                font = Font.font (noteDescriptionTextArea.getFont ().getFamily (),FontWeight.BOLD, FontPosture.ITALIC,
                        noteDescriptionTextArea.getFont ().getSize ());
            } else {
                font = Font.font (noteDescriptionTextArea.getFont ().getFamily (), FontWeight.BOLD,
                        noteDescriptionTextArea.getFont ().getSize ());
            }
        }
        noteDescriptionTextArea.setFont (font);
     }

    public void italicAction() {
        Font font;
        if(textStyles.contains (TextStyle.ITALIC)) {
            textStyles.remove (TextStyle.ITALIC);
            if(textStyles.contains (TextStyle.BOLD)) {
                font = Font.font (noteDescriptionTextArea.getFont ().getFamily (), FontWeight.BOLD,
                        noteDescriptionTextArea.getFont ().getSize ());
            } else {
                font = Font.font (noteDescriptionTextArea.getFont ().getFamily (),
                        noteDescriptionTextArea.getFont ().getSize ());
            }
        } else {
            textStyles.add (TextStyle.ITALIC);
            if(textStyles.contains (TextStyle.BOLD)) {
                font = Font.font (noteDescriptionTextArea.getFont ().getFamily (),FontWeight.BOLD, FontPosture.ITALIC,
                        noteDescriptionTextArea.getFont ().getSize ());
            } else {
                font = Font.font (noteDescriptionTextArea.getFont ().getFamily (), FontPosture.ITALIC,
                        noteDescriptionTextArea.getFont ().getSize ());
            }
        }
        noteDescriptionTextArea.setFont (font);

    }

    public void uppercaseAction() {
        noteDescriptionTextArea.setText (noteDescriptionTextArea.getText ().toUpperCase ());
    }

    public void lowercaseAction() {
        noteDescriptionTextArea.setText (noteDescriptionTextArea.getText ().toLowerCase ());
    }

    public void capitalizeAction() {
        noteDescriptionTextArea.setText (noteDescriptionTextArea.getText ().toLowerCase ());
        noteDescriptionTextArea.setText (WordUtils.capitalize (noteDescriptionTextArea.getText (), '.', '?', '!', ' '));
    }


    public void helpUserGuide() {
        try {
            Stage newStage = new Stage();

            HelpController helpController = new HelpController ();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(helpController);

            newStage.setTitle(MyResourceBundle.getString("UserGuideTitle"));
            newStage.setScene(new Scene(loader.load(), 900, 650));
            newStage.setMinHeight(650);
            newStage.setMinWidth(900);

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
