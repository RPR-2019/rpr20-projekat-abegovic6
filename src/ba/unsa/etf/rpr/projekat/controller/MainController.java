package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.utilities.PrintReport;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dal.*;
import ba.unsa.etf.rpr.projekat.dto.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;

import java.io.*;
import java.util.*;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class MainController {
    private final DatabaseConnection databaseConnection;

    private List<Node> nodes;

    private final NoteModel noteModel;
    private final GroupModel groupModel;
    private final LabelModel labelModel;

    private SortModel sortNotesModel;
    private SortModel sortGroupLabelsModel;


    @FXML
    public ListView<Group> groupListView;
    @FXML
    public ListView<ba.unsa.etf.rpr.projekat.dto.Label> labelListView;
    @FXML
    public Label userEmailLabel;
    @FXML
    public Label userNameLabel;
    @FXML
    public Label userUsernameLabel;
    @FXML
    public VBox vboxForListview;
    @FXML
    public FlowPane flowPaneForNotes;
    @FXML
    public ChoiceBox<String> sortGroupLabelsChoiceBox;
    @FXML
    public ChoiceBox<String> sortNotesChoiceBox;
    @FXML
    public TextField searchNotesField;



    public MainController() {
        this.databaseConnection = DatabaseConnection.getInstance ();

        this.noteModel = databaseConnection.getNoteModel ();
        this.noteModel.getAllNotes ().clear ();
        this.noteModel.getAllNotes().addAll (this.noteModel.getAllNotesForUser (AccountModel.getCurrentUser ()));

        this.groupModel = databaseConnection.getGroupModel ();
        this.groupModel.getAllGroups ().clear ();
        this.groupModel.getGroups ().clear ();
        this.groupModel.getAllGroups ().addAll (this.groupModel.getAllGroupsForAccount (AccountModel.getCurrentUser ()));
        for(Group group : this.groupModel.getAllGroups ()) {
            this.groupModel.getGroups ().add (group.getGroupName ());
        }

        this.labelModel = databaseConnection.getLabelModel ();
        this.labelModel.getAllLabels ().clear ();
        this.labelModel.getAllLabels ().addAll (this.labelModel.getAllLabelsForAccount (AccountModel.getCurrentUser ()));

        this.nodes = new ArrayList<> ();
    }

    @FXML
    public void initialize () {
        userEmailLabel.setText (MyResourceBundle.getString ("EmailAdress") + " " + AccountModel.getCurrentUser ().getEmailAdress ());
        userNameLabel.setText (MyResourceBundle.getString ("Name") + " " + AccountModel.getCurrentUser ().getFirstName ()
                + " " + AccountModel.getCurrentUser ().getLastName ());
        userUsernameLabel.setText (MyResourceBundle.getString ("Username") + " " + AccountModel.getCurrentUser ().getUserName ());

        noteModel.getCurrentNotes ().addListener ((ListChangeListener<Note>) c -> {
            while (c.next()) {
                if(!c.wasPermutated () && !c.wasUpdated ()) {
                    for (Note remitem : c.getRemoved()) {
                        hideTheNode (remitem);
                    }
                    for (Note additem : c.getAddedSubList()) {
                        showTheNode (additem);
                    }
                }
            }
        });

        setUpTheFlowPane ();

        groupListView = new ListView<> ();
        groupListView.setItems (groupModel.getAllGroups ());
        groupListView.setCellFactory (listView -> new GroupListCellController (groupModel.getAllGroups ()));
        groupListView.getSelectionModel ().selectedItemProperty().addListener((obs, oldGroup, newGroup) -> {
            if(newGroup != null) {
                noteModel.getCurrentNotes ().clear ();
                noteModel.getCurrentNotes ().addAll (noteModel.getNotesForGroup (newGroup.getId ()));
                var selected = sortNotesChoiceBox.getSelectionModel ().selectedItemProperty ().get ();
                if (selected != null) noteModel.sortNotes (selected);
            }
        });
        vboxForListview.getChildren ().add (2, groupListView);

        labelListView = new ListView<>();
        labelListView.setItems(labelModel.getAllLabels ());
        labelListView.setCellFactory(listView -> new LabelListCellController (labelModel.getAllLabels ()));
        labelListView.getSelectionModel ().selectedItemProperty ().addListener ((obs, oldLabel, newLabel) -> {
            if(newLabel != null) {
                noteModel.getCurrentNotes ().clear ();
                noteModel.getCurrentNotes ().addAll (noteModel.getNotesForLabel (newLabel.getId ()));
                var selected = sortNotesChoiceBox.getSelectionModel ().selectedItemProperty ().get ();
                if (selected != null) noteModel.sortNotes (selected);
            }
        });


        sortNotesModel = new SortModel ();
        sortNotesChoiceBox.setItems (sortNotesModel.getSorting ());
        sortNotesModel.currentSortingProperty ().addListener ((obs, oldSort, newSort) -> {
            if(newSort != null) {
                noteModel.sortNotes (newSort);
            }
        });

        sortGroupLabelsModel = new SortModel ();
        sortGroupLabelsModel.getSorting ().remove (2, 2);
        sortGroupLabelsChoiceBox.setItems (sortGroupLabelsModel.getSorting ());
        sortGroupLabelsModel.currentSortingProperty ().addListener ((obs, oldSort, newSort) -> {
            if(newSort != null) {
                sortGroups (newSort);
                sortLabels (newSort);
            }
        });

    }


    public void changeCurrentNoteSort() {
        sortNotesModel.setCurrentSorting (sortNotesChoiceBox.getValue());
    }

    public void changeCurrentGroupLabelSort() {
        sortGroupLabelsModel.setCurrentSorting (sortGroupLabelsChoiceBox.getValue ());
    }

    public void changeToGroups() {
        vboxForListview.getChildren().remove(2);
        vboxForListview.getChildren().add(2, groupListView);
        groupListView.getSelectionModel ().clearSelection ();
        noteModel.getCurrentNotes ().clear ();
    }

    public void changeToLabels() {
        vboxForListview.getChildren().remove(2);
        vboxForListview.getChildren().add(2, labelListView);
        labelListView.getSelectionModel ().clearSelection ();
        noteModel.getCurrentNotes ().clear ();
    }

    private void sortGroups (String sort) {
        groupModel.sortGroups (sort);
        groupListView.refresh ();
    }

    private void sortLabels (String sort) {
        labelModel.sortLabels (sort);
        labelListView.refresh ();
    }

    private void showTheNode(Note note) {
        Optional<Node> node = nodes.stream ().filter (n -> n.getId ().equals ("id" + note.getId ())).findFirst ();
        node.ifPresent (value -> flowPaneForNotes.getChildren ().add (node.get ()));
    }

    private void hideTheNode(Note note) {
        Optional<Node> node = nodes.stream ().filter (n -> n.getId ().equals ("id" + note.getId ())).findFirst ();
        node.ifPresent (value -> flowPaneForNotes.getChildren ().remove (node.get ()));
    }

    private void setUpTheFlowPane() {
        for (Note note : noteModel.getAllNotes ()) {
            flowPaneForNotes.getChildren ().add (getNoteBlock (note));
        }
        nodes = new ArrayList<> (flowPaneForNotes.getChildren ());
        flowPaneForNotes.getChildren ().clear ();
    }

    private GridPane getNoteBlock(Note note) {
        GridPane gridPane = new GridPane();
        gridPane.setMinHeight (150);
        gridPane.setMinWidth (150);
        gridPane.setMaxHeight (150);
        gridPane.setMaxWidth (200);
        gridPane.setStyle ("-fx-background-color: " + note.getNoteColor ().getHexCode ());
        note.noteColorProperty ().addListener ((obp, oldColor, newColor) -> {
            if(newColor != null) {
                gridPane.setStyle ("-fx-background-color: " + NoteColor.valueOf (newColor).getHexCode ());
            }
        });

        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.TOP_CENTER);

        RowConstraints rowConstraint1 = new RowConstraints ();
        rowConstraint1.setPercentHeight (20);

        RowConstraints rowConstraint2 = new RowConstraints ();
        rowConstraint2.setPercentHeight (80);
        rowConstraint2.setValignment (VPos.TOP);

        gridPane.getRowConstraints ().addAll (rowConstraint1, rowConstraint2);

        Label noteTitle = new Label (note.getNoteTitle ());
        note.noteTitleProperty ().addListener ((obp, oldTitle, newTitle) -> {
            if(newTitle != null)
                noteTitle.setText (newTitle);
        });
        noteTitle.getStyleClass ().add ("paragraph");
        noteTitle.setMaxWidth (gridPane.getMaxWidth ());

        Label noteDescription = new Label (note.getDescription ());
        note.descriptionProperty ().addListener ((obp, oldDes, newDes) -> {
            if(newDes != null)
                noteDescription.setText (newDes);
        });
        noteDescription.getStyleClass ().add ("paragraphsmaller");
        noteDescription.setMaxWidth (gridPane.getMaxWidth ());

        Label noteId = new Label (String.valueOf (note.getId ()));
        noteId.setId ("noteId");
        noteId.setStyle ("-fx-font-size: 1px;");
        noteId.setStyle ("-fx-text-fill: " + note.getNoteColor ().getHexCode ());
        noteId.setMinSize (0,0);

        gridPane.add (noteTitle,0,0);
        gridPane.add (noteDescription, 0, 1);
        gridPane.add (noteId, 0, 2);

        gridPane.setId ("id" + note.getId ());

        gridPane.setCursor (Cursor.HAND);

        gridPane.setOnMouseClicked (mouseEvent -> opetNoteDetails (note));

        return gridPane;

    }

    private void opetNoteDetails(Note note) {
        try {
            Stage newStage = new Stage();

            note.setUpdateNeeded (false);
            note.setDelete (false);

            NoteController noteController = new NoteController (note);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/note.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(noteController);

            newStage.setTitle(MyResourceBundle.getString("NoteInformations"));
            newStage.setScene (new Scene (loader.load (), 800, 600));
            newStage.setMinHeight (600);
            newStage.setMinWidth (800);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(note.isUpdateNeeded ()) {
                    noteModel.updateNote(note);
                }
                if(note.isDelete ()) {
                    noteModel.deleteNote (note.getId ());
                }
                var selected = sortNotesChoiceBox.getSelectionModel ().selectedItemProperty ().get ();
                if (selected != null) noteModel.sortNotes (selected);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void searchNotesAction() {
        noteModel.searchNotes (searchNotesField.getText ());
        var selected = sortNotesChoiceBox.getSelectionModel ().selectedItemProperty ().get ();
        if (selected != null) noteModel.sortNotes (selected);
    }

    public void resetNoteListAction() {
        flowPaneForNotes.getChildren ().clear ();
        searchNotesField.clear ();
    }

    public void createNewGroup() {
        try {
            Stage newStage = new Stage();

            Group group = new Group();
            group.setId(-2);
            group.setAccountId(AccountModel.getCurrentUser ().getId());



            GroupController groupController = new GroupController(group);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/group.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(groupController);

            newStage.setTitle(MyResourceBundle.getString("CreateGroup"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(group.getId() == -1) {
                    groupModel.createGroup (group);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createNewLabel() {
        try {
            Stage newStage = new Stage();

            ba.unsa.etf.rpr.projekat.dto.Label label = new ba.unsa.etf.rpr.projekat.dto.Label ();
            label.setId(-2);
            label.setAccountId(AccountModel.getCurrentUser ().getId());

            LabelController labelController = new LabelController(label);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/label.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(labelController);

            newStage.setTitle(MyResourceBundle.getString("CreateLabel"));
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(label.getId() == -1) {
                    labelModel.createLabel (label);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void createNewNote() {
        if(groupModel.getAllGroups ().isEmpty ()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(MyResourceBundle.getString("Error"));
            alert.setHeaderText(MyResourceBundle.getString("NeedAGroupAction"));
            alert.setContentText(MyResourceBundle.getString("NeedAGroupActionContent"));

            alert.showAndWait();
        } else {
            try {
                Stage newStage = new Stage ();

                Note note = new Note ();
                note.setId (-2);
                NoteController noteController = new NoteController (note);

                FXMLLoader loader = new FXMLLoader (getClass ().getResource ("/fxml/note.fxml"),
                        MyResourceBundle.getResourceBundle ());
                loader.setController (noteController);

                newStage.setTitle (MyResourceBundle.getString ("CreateNote"));
                newStage.setScene (new Scene (loader.load (), 800, 600));
                newStage.setMinHeight (600);
                newStage.setMinWidth (800);

                newStage.show ();

                newStage.setOnHiding (windowEvent -> {
                    if (note.getId () == -1) {
                        noteModel.createNote (note);
                        nodes.add (getNoteBlock (note));
                        if(groupListView.getSelectionModel ().selectedItemProperty ().get () != null &&
                                groupListView.getSelectionModel ().selectedItemProperty ().get ().getId () == note.getGroupId ())
                            noteModel.getCurrentNotes ().add (note);
                        if(labelListView.getSelectionModel ()
                                .selectedItemProperty ().get () != null &&
                                note.getLabels ().stream ().anyMatch (l -> labelListView.getSelectionModel ()
                                .selectedItemProperty ().get ().getId () == l.getId ()))
                            noteModel.getCurrentNotes ().add (note);

                    }
                    var selected = sortNotesChoiceBox.getSelectionModel ().selectedItemProperty ().get ();
                    if (selected != null) noteModel.sortNotes (selected);
                });
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }


    }

    private String writeInFile() {
        String string = MyResourceBundle.getString ("UserInformation") + "\n\n" + MyResourceBundle.getString ("UserId")
                + AccountModel.getCurrentUser ().getId () + "\n" + MyResourceBundle.getString ("FirstName") +
                AccountModel.getCurrentUser ().getFirstName () +
                "\n" + MyResourceBundle.getString ("LastName") + AccountModel.getCurrentUser ().getLastName () + "\n" +
                MyResourceBundle.getString ("Username") + AccountModel.getCurrentUser ().getUserName () + "\n" +
                MyResourceBundle.getString ("EmailAdress")
                + AccountModel.getCurrentUser ().getEmailAdress () + "\n\n**********\n";
        string += MyResourceBundle.getString ("UserGroups") + "\n\n";
        for(Group group : groupModel.getAllGroups ()) {
            string += group.writeInFile (noteModel.getNotesForGroup (group.getId ()));
        }

        string +=  "\n**********\n\n" + MyResourceBundle.getString ("UserLabels") + "\n\n";
        for(ba.unsa.etf.rpr.projekat.dto.Label label : labelModel.getAllLabels ()) {
            string += label.writeInFile (noteModel.getNotesForLabel (label.getId ()));
        }

        return string;

    }

    public void fileSave() {
        FileChooser izbornik = new FileChooser();
        izbornik.setTitle(MyResourceBundle.getString ("ChooseFile"));
        izbornik.getExtensionFilters().add(new FileChooser.ExtensionFilter(MyResourceBundle.getString ("TextFile"), "*.txt"));
        File file = izbornik.showSaveDialog(flowPaneForNotes.getScene().getWindow());

        if(file == null) return;
        try {
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            fileWriter.write(writeInFile());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void printNotes() {
        try {
            new PrintReport ().showReport(databaseConnection.getConnection (), "note");
        } catch (JRException e1) {
            e1.printStackTrace();
        }

    }
    public void printLabels() {
        try {
            new PrintReport ().showReport(databaseConnection.getConnection (), "label");
        } catch (JRException e1) {
            e1.printStackTrace();
        }

    }
    public void printGroups() {
        try {
            new PrintReport ().showReport(databaseConnection.getConnection (), "group");
        } catch (JRException e1) {
            e1.printStackTrace();
        }

    }

    public void openSettings() {
        fileSettings ();
    }

    public void fileSettings() {
        try {
            Node source = flowPaneForNotes;
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            SettingsController settingsController = new SettingsController ();

            FXMLLoader loader = new FXMLLoader (getClass ().getResource ("/fxml/settings.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(settingsController);

            newStage.setTitle(MyResourceBundle.getString("SettingsTitle"));
            newStage.setScene(new Scene(loader.load(),  USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            newStage.setResizable (false);

            oldStage.close();
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void fileExit() {
        Node n =  flowPaneForNotes;
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }


    public void editDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(searchNotesField.getContextMenu ());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(MyResourceBundle.getString ("DeleteThis"));
        alert.setHeaderText(MyResourceBundle.getString ("DeleteAccount"));
        alert.setContentText(MyResourceBundle.getString ("AreYouSure"));

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            DatabaseConnection.getInstance ().getAccountModel ().deleteAccount (AccountModel.getCurrentUser ());
        }
        logout ();

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
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void logout() {
        try {
            Node source = flowPaneForNotes;
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            LoginController loginController = new LoginController();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"),
                    MyResourceBundle.getResourceBundle ());
            loader.setController(loginController);

            newStage.setTitle(MyResourceBundle.getString("LogInTitle"));
            newStage.setScene(new Scene(loader.load(), 1100, 600));
            newStage.setMinHeight(600);
            newStage.setMinWidth(1100);

            oldStage.close();
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
