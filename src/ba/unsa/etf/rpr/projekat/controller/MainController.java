package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.*;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
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

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class MainController {
    private final Account user;
    private final ProjectDAO projectDAO;

    private final ObservableList<Group> groupsObservableList;
    private final ObservableList<ba.unsa.etf.rpr.projekat.model.Label> labelObservableList;
    private final ObservableList<Note> noteObservableList;
    private List<Node> nodes;

    private NoteModel noteModel;
    private SortModel sortNotesModel;
    private SortModel sortGroupLabelsModel;
    private HostServices hostServices;


    @FXML
    public ListView<Group> groupListView;
    @FXML
    public ListView<ba.unsa.etf.rpr.projekat.model.Label> labelListView;
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



    public MainController(ProjectDAO projectDAO, Account user, HostServices hostServices) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.hostServices = hostServices;

        this.groupsObservableList = FXCollections.observableArrayList();
        this.labelObservableList = FXCollections.observableArrayList();
        this.noteObservableList = FXCollections.observableArrayList();

        this.groupsObservableList.addAll(this.projectDAO.getAllGroupsForAccount(this.user));
        this.labelObservableList.addAll(this.projectDAO.getAllLabelsForAccount(this.user));
        this.noteObservableList.addAll (this.projectDAO.getAllNotesForUser (this.user));
    }

    @FXML
    public void initialize () {
        userEmailLabel.setText (MyResourceBundle.getString ("EmailAdress") + " " + user.getEmailAdress ());
        userNameLabel.setText (MyResourceBundle.getString ("Name") + " " + user.getFirstName () + " " + user.getLastName ());
        userUsernameLabel.setText (MyResourceBundle.getString ("Username") + " " + user.getUserName ());

        noteModel = new NoteModel ();

        noteModel.getCurrentNotes ().addListener ((ListChangeListener<Note>) c -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        //permutate
                    }
                } else if (c.wasUpdated()) {

                } else {
                    for (Note remitem : c.getRemoved()) {
                        hideTheNode (remitem);
                    }
                    for (Note additem : c.getAddedSubList()) {
                        showTheNode (additem);
                    }
                }
            }
        });

        nodes = new ArrayList<> ();

        setUpTheFlowPane ();


        groupListView = new ListView<> ();
        groupListView.setItems (groupsObservableList);
        groupListView.setCellFactory (listView -> new GroupListCellController (groupsObservableList, projectDAO,
                noteObservableList, hostServices, noteModel));
        groupListView.getSelectionModel ().selectedItemProperty().addListener((obs, oldGroup, newGroup) -> {
            if(newGroup != null) {
                noteModel.getCurrentNotes ().clear ();
                noteModel.getCurrentNotes ().addAll (getNotesForGroup (newGroup.getId ()));
                var selected = sortNotesChoiceBox.getSelectionModel ().selectedItemProperty ().get ();
                if (selected != null) noteModel.sortNotes (selected);
            }
        });
        vboxForListview.getChildren ().add (2, groupListView);

        labelListView = new ListView<>();
        labelListView.setItems(labelObservableList);
        labelListView.setCellFactory(listView -> new LabelListCellController (labelObservableList, projectDAO,
                noteObservableList, hostServices, noteModel));
        labelListView.getSelectionModel ().selectedItemProperty ().addListener ((obs, oldLabel, newLabel) -> {
            if(newLabel != null) {
                noteModel.getCurrentNotes ().clear ();
                noteModel.getCurrentNotes ().addAll (getNotesForLabel (newLabel.getId ()));
                var selected = sortNotesChoiceBox.getSelectionModel ().selectedItemProperty ().get ();
                if (selected != null) noteModel.sortNotes (selected);
            }
        });

        sortNotesModel = new SortModel ();
        sortGroupLabelsModel = new SortModel ();

        sortNotesChoiceBox.setItems (sortNotesModel.getSorting ());
        sortNotesModel.currentSortingProperty ().addListener ((obs, oldSort, newSort) -> {
            if(newSort != null) {
                noteModel.sortNotes (newSort);
            }
        });

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

    private void sortGroups (String sort) {
        if(sort.equals (MyResourceBundle.getString ("LastAdded"))) {
            groupListView.getItems ()
                    .sort (Comparator.comparingInt (Group::getId).reversed ());
        } else if (sort.equals (MyResourceBundle.getString ("FirstAdded"))) {
            groupListView.getItems ()
                    .sort (Comparator.comparingInt (Group::getId));
        } else if (sort.equals (MyResourceBundle.getString ("ByNameAsc"))) {
            groupListView.getItems ()
                    .sort (Comparator.comparing(Group::getGroupName));
        } else if (sort.equals (MyResourceBundle.getString ("ByNameDesc"))) {
            groupListView.getItems ()
                    .sort (Comparator.comparing(Group::getGroupName).reversed ());
        } else if (sort.equals (MyResourceBundle.getString ("ByDescriptionAsc"))) {
            groupListView.getItems ()
                    .sort (Comparator.comparing(Group::getDescription));
        } else  {
            groupListView.getItems ()
                    .sort (Comparator.comparing(Group::getDescription).reversed ());
        }
        groupListView.refresh ();

    }

    private void sortLabels (String sort) {
        if(sort.equals (MyResourceBundle.getString ("LastAdded"))) {
            labelListView.getItems ()
                    .sort (Comparator.comparingInt (ba.unsa.etf.rpr.projekat.model.Label::getId).reversed ());
        } else if (sort.equals (MyResourceBundle.getString ("FirstAdded"))) {
            labelListView.getItems ()
                    .sort (Comparator.comparingInt (ba.unsa.etf.rpr.projekat.model.Label::getId));
        } else if (sort.equals (MyResourceBundle.getString ("ByNameAsc"))) {
            labelListView.getItems ()
                    .sort (Comparator.comparing(ba.unsa.etf.rpr.projekat.model.Label::getLabelName));
        } else if (sort.equals (MyResourceBundle.getString ("ByNameDesc"))) {
            labelListView.getItems ()
                    .sort (Comparator.comparing(ba.unsa.etf.rpr.projekat.model.Label::getLabelName).reversed ());
        } else if (sort.equals (MyResourceBundle.getString ("ByDescriptionAsc"))) {
            labelListView.getItems ()
                    .sort (Comparator.comparing(ba.unsa.etf.rpr.projekat.model.Label::getDescription));
        } else  {
            labelListView.getItems ()
                    .sort (Comparator.comparing(ba.unsa.etf.rpr.projekat.model.Label::getDescription).reversed ());
        }
        labelListView.refresh ();

    }


    private List<Note> getNotesForGroup(int groupId) {
        return noteObservableList.stream ().filter (n -> n.getGroupId () == groupId).collect (Collectors.toList ());
    }

    private List<Note> getNotesForLabel(int labelId) {
        return noteObservableList.stream ().filter (n -> n.getLabels ().stream ().anyMatch (l -> l.getId () == labelId))
                .collect (Collectors.toList ());
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
        for (Note note : noteObservableList) {
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
        noteTitle.getStyleClass ().add ("paragraph");
        noteTitle.setMaxWidth (gridPane.getMaxWidth ());

        Label noteDescription = new Label (note.getDescription ());
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

        gridPane.setOnMouseClicked (mouseEvent -> {
            opetNoteDetails (note);

        });

        return gridPane;

    }

    private void opetNoteDetails(Note note) {
        try {
            Stage newStage = new Stage();

            note.setUpdateNeeded (true);

            NoteController noteController = new NoteController (note, labelObservableList, groupsObservableList, hostServices);

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
                    projectDAO.updateNote(note);
                    updateNode(note);
                }
                if(note.isDelete ()) {
                    projectDAO.deleteNote (note.getId ());
                    noteObservableList.remove (note);
                    if(noteModel.getCurrentNotes ().contains (note))
                        noteModel.getCurrentNotes ().remove (note);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeNotesForGroup (int id) {
        List<Note> notes = getNotesForGroup (id);
        for(Note note : notes) {
            noteObservableList.remove (note);
            if(noteModel.getCurrentNotes ().contains (note))
                noteModel.getCurrentNotes ().remove (note);
        }
    }

    public void removeNotesForLabel (int id) {
        List<Note> notes = getNotesForLabel (id);
        for(Note note : notes) {
            noteObservableList.remove (note);
            if(noteModel.getCurrentNotes ().contains (note))
                noteModel.getCurrentNotes ().remove (note);
        }
    }

    private void updateNode (Note note) {
        Node newNode = getNoteBlock (note);

        Node oldNode = null;
        int position = -1;
        for(int i = 0; i < nodes.size (); i++) {
            if(nodes.get (i).getId ().equals ("id" + note.getId ())) {
                oldNode = nodes.get (i);
                position = i;
            }
        }

        if(oldNode != null) {
            nodes.remove (position);
            nodes.add (position, newNode);
        }

        oldNode = null;
        position = -1;

        for(int i = 0; i < flowPaneForNotes.getChildren ().size (); i++) {
            if(flowPaneForNotes.getChildren ().get (i).getId ().equals ("id" + note.getId ())) {
                oldNode = flowPaneForNotes.getChildren ().get (i);
                position = i;
            }
        }

        if(oldNode != null) {
            flowPaneForNotes.getChildren ().remove (position);
            flowPaneForNotes.getChildren ().add (position, newNode);
        }





    }

    public void changeToGroups() {
        vboxForListview.getChildren().remove(2);
        vboxForListview.getChildren().add(2, groupListView);
        groupListView.getSelectionModel ().clearSelection ();
    }

    public void changeToLabels() {
        vboxForListview.getChildren().remove(2);
        vboxForListview.getChildren().add(2, labelListView);
        labelListView.getSelectionModel ().clearSelection ();
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
            group.setAccountId(user.getId());



            GroupController groupController = new GroupController(group, groupsObservableList, new ArrayList<> (),
                    hostServices);

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
                    if(projectDAO.createGroup(group)) {
                        groupsObservableList.add(group);
                    }


                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createNewLabel() {
        try {
            Stage newStage = new Stage();

            ba.unsa.etf.rpr.projekat.model.Label label = new ba.unsa.etf.rpr.projekat.model.Label();
            label.setId(-2);
            label.setAccountId(user.getId());

            LabelController labelController = new LabelController(label, labelObservableList,
                    new ArrayList<> (), hostServices);

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
                    if(projectDAO.createLabel(label)) {
                        labelObservableList.add(label);
                    }


                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void createNewNote() {
        if(groupsObservableList.isEmpty ()) {
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

                NoteController noteController = new NoteController (note, labelObservableList,
                        groupsObservableList, hostServices);

                FXMLLoader loader = new FXMLLoader (getClass ().getResource ("/fxml/note.fxml"),
                        MyResourceBundle.getResourceBundle ());
                loader.setController (noteController);

                newStage.setTitle (MyResourceBundle.getString ("CreateNote"));
                newStage.setScene (new Scene (loader.load (), 800, 600));
                newStage.setMinHeight (600);
                newStage.setMinWidth (800);

                newStage.show ();

                newStage.setOnHiding (windowEvent -> {
                    if (note.getId () == -1 && projectDAO.createNote (note)) {
                        nodes.add (getNoteBlock (note));
                        noteObservableList.add (note);
                        if(groupListView.getSelectionModel ().selectedItemProperty ().get () != null &&
                                groupListView.getSelectionModel ().selectedItemProperty ().get ().getId () == note.getGroupId ())
                            noteModel.getCurrentNotes ().add (note);
                        if(labelListView.getSelectionModel ()
                                .selectedItemProperty ().get () != null && !note.getLabels ().stream().
                                filter (l -> labelListView.getSelectionModel ()
                                        .selectedItemProperty ().get ().getId () == l.getId ())
                                .collect (Collectors.toList ()).isEmpty ())
                            noteModel.getCurrentNotes ().add (note);




                    }
                });
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }


    }

    private String writeInFile() {
        String string = MyResourceBundle.getString ("UserInformation") + "\n\n" + MyResourceBundle.getString ("UserId")
                + user.getId () + "\n" + MyResourceBundle.getString ("FirstName") + user.getFirstName () +
                "\n" + MyResourceBundle.getString ("LastName") + user.getLastName () + "\n" +
                MyResourceBundle.getString ("Username") + user.getUserName () + "\n" + MyResourceBundle.getString ("EmailAdress")
                + user.getEmailAdress () + "\n\n**********\n";
        string += MyResourceBundle.getString ("UserGroups") + "\n\n";
        for(Group group : groupsObservableList) {
            string += group.writeInFile (getNotesForGroup (group.getId ()));
        }

        string +=  "\n**********\n\n" + MyResourceBundle.getString ("UserLabels") + "\n\n";
        for(ba.unsa.etf.rpr.projekat.model.Label label : labelObservableList) {
            string += label.writeInFile (getNotesForLabel (label.getId ()));
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

    public void filePrint() {

    }

    public void openSettings() {
        fileSettings ();
    }

    public void fileSettings() {
        try {
            Node source = (Node)  flowPaneForNotes;
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            SettingsController settingsController = new SettingsController (user, projectDAO, hostServices);

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

    public void fileExit(ActionEvent actionEvent) {
        Node n = (Node) flowPaneForNotes;
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
            projectDAO.deleteAccount (user);
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

            aboutController.setHostServices (hostServices);

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
            Node source = (Node)  flowPaneForNotes;
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            LoginController loginController = new LoginController(projectDAO, user, hostServices);

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
