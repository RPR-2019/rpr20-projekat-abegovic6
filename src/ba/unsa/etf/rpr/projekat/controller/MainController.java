package ba.unsa.etf.rpr.projekat.controller;

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
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class MainController {
    private final Account user;
    private final ResourceBundle resourceBundle;
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



    public MainController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle, HostServices hostServices) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;
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
        userEmailLabel.setText (resourceBundle.getString ("EmailAdress") + " " + user.getEmailAdress ());
        userNameLabel.setText (resourceBundle.getString ("Name") + " " + user.getFirstName () + " " + user.getLastName ());
        userUsernameLabel.setText (resourceBundle.getString ("Username") + " " + user.getUserName ());

        noteModel = new NoteModel ();

        noteModel.getNotes ().addListener ((ListChangeListener<Note>) c -> {
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
        groupListView.setCellFactory (listView -> new GroupListCellController (groupsObservableList, projectDAO, noteObservableList, resourceBundle, hostServices));
        groupListView.getSelectionModel ().selectedItemProperty().addListener((obs, oldGroup, newGroup) -> {
            if(newGroup != null) {
                noteModel.getNotes ().clear ();
                noteModel.getNotes ().addAll (getNotesForGroup (newGroup.getId ()));
                var selected = sortNotesChoiceBox.getSelectionModel ().selectedItemProperty ().get ();
                if (selected != null) sortNotes (selected);
            }
        });
        vboxForListview.getChildren ().add (2, groupListView);

        labelListView = new ListView<>();
        labelListView.setItems(labelObservableList);
        labelListView.setCellFactory(listView -> new LabelListCellController (labelObservableList, projectDAO, noteObservableList, resourceBundle, hostServices));
        labelListView.getSelectionModel ().selectedItemProperty ().addListener ((obs, oldLabel, newLabel) -> {
            if(newLabel != null) {
                noteModel.getNotes ().clear ();
                noteModel.getNotes ().addAll (getNotesForLabel (newLabel.getId ()));
                var selected = sortNotesChoiceBox.getSelectionModel ().selectedItemProperty ().get ();
                if (selected != null) sortNotes (selected);
            }
        });

        sortNotesModel = new SortModel (resourceBundle);
        sortGroupLabelsModel = new SortModel (resourceBundle);

        sortNotesChoiceBox.setItems (sortNotesModel.getSorting ());
        sortNotesModel.currentSortingProperty ().addListener ((obs, oldSort, newSort) -> {
            if(newSort != null) {
                sortNotes (newSort);
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
        if(sort.equals (resourceBundle.getString ("LastAdded"))) {
            groupListView.getItems ()
                    .sort (Comparator.comparingInt (Group::getId).reversed ());
        } else if (sort.equals (resourceBundle.getString ("FirstAdded"))) {
            groupListView.getItems ()
                    .sort (Comparator.comparingInt (Group::getId));
        } else if (sort.equals (resourceBundle.getString ("ByNameAsc"))) {
            groupListView.getItems ()
                    .sort (Comparator.comparing(Group::getGroupName));
        } else if (sort.equals (resourceBundle.getString ("ByNameDesc"))) {
            groupListView.getItems ()
                    .sort (Comparator.comparing(Group::getGroupName).reversed ());
        } else if (sort.equals (resourceBundle.getString ("ByDescriptionAsc"))) {
            groupListView.getItems ()
                    .sort (Comparator.comparing(Group::getDescription));
        } else  {
            groupListView.getItems ()
                    .sort (Comparator.comparing(Group::getDescription).reversed ());
        }
        groupListView.refresh ();

    }

    private void sortLabels (String sort) {
        if(sort.equals (resourceBundle.getString ("LastAdded"))) {
            labelListView.getItems ()
                    .sort (Comparator.comparingInt (ba.unsa.etf.rpr.projekat.model.Label::getId).reversed ());
        } else if (sort.equals (resourceBundle.getString ("FirstAdded"))) {
            labelListView.getItems ()
                    .sort (Comparator.comparingInt (ba.unsa.etf.rpr.projekat.model.Label::getId));
        } else if (sort.equals (resourceBundle.getString ("ByNameAsc"))) {
            labelListView.getItems ()
                    .sort (Comparator.comparing(ba.unsa.etf.rpr.projekat.model.Label::getLabelName));
        } else if (sort.equals (resourceBundle.getString ("ByNameDesc"))) {
            labelListView.getItems ()
                    .sort (Comparator.comparing(ba.unsa.etf.rpr.projekat.model.Label::getLabelName).reversed ());
        } else if (sort.equals (resourceBundle.getString ("ByDescriptionAsc"))) {
            labelListView.getItems ()
                    .sort (Comparator.comparing(ba.unsa.etf.rpr.projekat.model.Label::getDescription));
        } else  {
            labelListView.getItems ()
                    .sort (Comparator.comparing(ba.unsa.etf.rpr.projekat.model.Label::getDescription).reversed ());
        }
        labelListView.refresh ();

    }

    private void sortNotes(String sort) {
        ArrayList<Node> list;
        if(sort.equals (resourceBundle.getString ("LastAdded"))) {
            list = new ArrayList<> (flowPaneForNotes.getChildren ().sorted (Comparator.comparing (Node::getId).reversed ()));
        } else if (sort.equals (resourceBundle.getString ("FirstAdded"))) {
            list= new ArrayList<> (flowPaneForNotes.getChildren ().sorted (Comparator.comparing (Node::getId)));
        } else if(sort.equals (resourceBundle.getString ("LastUpdated"))) {
            list = new ArrayList<> (flowPaneForNotes.getChildren ().sorted ((i1, i2) -> {
                Optional<Note> n1 = noteObservableList.stream().filter (n -> i1.getId ().equals ("id" + n.getId ())).findFirst ();
                Optional<Note> n2 = noteObservableList.stream().filter (n -> i2.getId ().equals ("id" + n.getId ())).findFirst ();
                if(n1.isPresent () && n2.isPresent ())
                    return n2.get ().getDateUpdated ().compareTo (n1.get ().getDateUpdated ());
                return 0;
            }));
        } else if (sort.equals (resourceBundle.getString ("FirstUpdated"))) {
            list = new ArrayList<> (flowPaneForNotes.getChildren ().sorted ((i1, i2) -> {
                Optional<Note> n1 = noteObservableList.stream().filter (n -> i1.getId ().equals ("id" + n.getId ())).findFirst ();
                Optional<Note> n2 = noteObservableList.stream().filter (n -> i2.getId ().equals ("id" + n.getId ())).findFirst ();
                if(n1.isPresent () && n2.isPresent ())
                    return n1.get ().getDateUpdated ().compareTo (n2.get ().getDateUpdated ());
                return 0;
            }));
        } else if (sort.equals (resourceBundle.getString ("ByNameAsc"))) {
            list = new ArrayList<> (flowPaneForNotes.getChildren ()
                    .sorted ((Comparator.comparing (node -> ((Label)((GridPane)node).getChildren ().get (0)).getText ()))));
        } else if (sort.equals (resourceBundle.getString ("ByNameDesc"))) {
            list = new ArrayList<> (flowPaneForNotes.getChildren ()
                    .sorted ((Comparator.comparing (node -> ((Label)((GridPane)node).getChildren().get(0)).getText(),
                            Comparator.reverseOrder ()))));
        } else if (sort.equals (resourceBundle.getString ("ByDescriptionAsc"))) {
            list = new ArrayList<> (flowPaneForNotes.getChildren ()
                    .sorted ((Comparator.comparing (node -> ((Label)((GridPane)node).getChildren ().get (1)).getText ()))));
        } else  {
            list = new ArrayList<> (flowPaneForNotes.getChildren ()
                    .sorted ((Comparator.comparing (node -> ((Label)((GridPane)node).getChildren().get(1)).getText(),
                            Comparator.reverseOrder ()))));
        }

        flowPaneForNotes.getChildren ().clear ();
        flowPaneForNotes.getChildren ().addAll (list);
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

            NoteController noteController = new NoteController (note, labelObservableList, groupsObservableList, resourceBundle, hostServices);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/note.fxml"), resourceBundle);
            loader.setController(noteController);

            newStage.setTitle(resourceBundle.getString("NoteInformations"));
            newStage.setScene (new Scene (loader.load (), 800, 600));
            newStage.setMinHeight (600);
            newStage.setMinWidth (800);

            newStage.show();

            newStage.setOnHiding(windowEvent -> {
                if(note.isUpdateNeeded ()) {
                    projectDAO.updateNote(note);
                    updateNode(note);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
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
        flowPaneForNotes.getChildren ().clear ();
        flowPaneForNotes.getChildren ().addAll (nodes.stream ()
                .filter (node ->  ((Label)((GridPane) node).getChildren ().get (0)).getText().toLowerCase ()
                        .contains(searchNotesField.getText ().toLowerCase ())
                        || ((Label)((GridPane) node).getChildren ().get (1)).getText().toLowerCase ()
                        .contains(searchNotesField.getText ().toLowerCase ())).collect(Collectors.toList ()));
        var selected = sortNotesChoiceBox.getSelectionModel ().selectedItemProperty ().get ();
        if (selected != null) sortNotes (selected);
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
                    resourceBundle, hostServices);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/group.fxml"), resourceBundle);
            loader.setController(groupController);

            newStage.setTitle(resourceBundle.getString("CreateGroup"));
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

            LabelController labelController = new LabelController(label, labelObservableList, new ArrayList<> (), resourceBundle, hostServices);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/label.fxml"), resourceBundle);
            loader.setController(labelController);

            newStage.setTitle(resourceBundle.getString("CreateLabel"));
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
            alert.setTitle(resourceBundle.getString("Error"));
            alert.setHeaderText(resourceBundle.getString("NeedAGroupAction"));
            alert.setContentText(resourceBundle.getString("NeedAGroupActionContent"));

            alert.showAndWait();
        } else {
            try {


                Stage newStage = new Stage ();
                Note note = new Note ();
                note.setId (-2);

                NoteController noteController = new NoteController (note, labelObservableList, groupsObservableList, resourceBundle, hostServices);

                FXMLLoader loader = new FXMLLoader (getClass ().getResource ("/fxml/note.fxml"), resourceBundle);
                loader.setController (noteController);

                newStage.setTitle (resourceBundle.getString ("CreateNote"));
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
                            noteModel.getNotes ().add (note);
                        if(labelListView.getSelectionModel ()
                                .selectedItemProperty ().get () != null && !note.getLabels ().stream().
                                filter (l -> labelListView.getSelectionModel ()
                                        .selectedItemProperty ().get ().getId () == l.getId ())
                                .collect (Collectors.toList ()).isEmpty ())
                            noteModel.getNotes ().add (note);


                    }
                });
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }


    }

    private String writeInFile() {
        String string = resourceBundle.getString ("UserInformation") + "\n\n" + resourceBundle.getString ("UserId")
                + user.getId () + "\n" + resourceBundle.getString ("FirstName") + user.getFirstName () +
                "\n" + resourceBundle.getString ("LastName") + user.getLastName () + "\n" +
                resourceBundle.getString ("Username") + user.getUserName () + "\n" + resourceBundle.getString ("EmailAdress")
                + user.getEmailAdress () + "\n\n**********\n";
        string += resourceBundle.getString ("UserGroups") + "\n\n";
        for(Group group : groupsObservableList) {
            string += group.writeInFile (getNotesForGroup (group.getId ()), resourceBundle);
        }

        string +=  "\n**********\n\n" + resourceBundle.getString ("UserLabels") + "\n\n";
        for(ba.unsa.etf.rpr.projekat.model.Label label : labelObservableList) {
            string += label.writeInFile (getNotesForLabel (label.getId ()), resourceBundle);
        }

        return string;

    }

    public void fileSave() {
        FileChooser izbornik = new FileChooser();
        izbornik.setTitle(resourceBundle.getString ("ChooseFile"));
        izbornik.getExtensionFilters().add(new FileChooser.ExtensionFilter(resourceBundle.getString ("TextFile"), "*.txt"));
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

    public void fileSettings(ActionEvent actionEvent) {
        try {
            Node source = (Node)  flowPaneForNotes;
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            SettingsController settingsController = new SettingsController (user, projectDAO, resourceBundle, hostServices);

            FXMLLoader loader = new FXMLLoader (getClass ().getResource ("/fxml/settings.fxml"), resourceBundle);
            loader.setController(settingsController);

            newStage.setTitle(resourceBundle.getString("SettingsTitle"));
            newStage.setScene(new Scene(loader.load(),  USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            newStage.setResizable (false);

            oldStage.close();
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void fileExit(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }


    public void editDelete() {

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
            newStage.setScene(new Scene(loader.load(), 700, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(700);

            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void logout(ActionEvent actionEvent) {
        try {
            Node source = (Node)  actionEvent.getSource();
            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            LoginController loginController = new LoginController(projectDAO, user, resourceBundle, hostServices);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), resourceBundle);
            loader.setController(loginController);

            newStage.setTitle(resourceBundle.getString("LogInTitle"));
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
