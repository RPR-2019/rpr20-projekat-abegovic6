package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import ba.unsa.etf.rpr.projekat.model.Group;
import ba.unsa.etf.rpr.projekat.model.Note;
import ba.unsa.etf.rpr.projekat.model.NoteModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController {
    private final Account user;
    private final ResourceBundle resourceBundle;
    private final ProjectDAO projectDAO;

    private ObservableList<Group> groupsObservableList;
    private ObservableList<ba.unsa.etf.rpr.projekat.model.Label> labelObservableList;
    private Map<Group, ObservableList<Note>> noteObservableListMap;

    private NoteModel noteModel;

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



    public MainController(ProjectDAO projectDAO, Account user, ResourceBundle resourceBundle) {
        this.projectDAO = projectDAO;
        this.user = user;
        this.resourceBundle = resourceBundle;

        this.groupsObservableList = FXCollections.observableArrayList();
        this.labelObservableList = FXCollections.observableArrayList();
        this.noteObservableListMap = new HashMap<> ();

        this.groupsObservableList.addAll(this.projectDAO.getAllGroupsForAccount(this.user));
        this.labelObservableList.addAll(this.projectDAO.getAllLabelsForAccount(this.user));

        for(Group group : groupsObservableList) {
            ObservableList<Note> notes = FXCollections.observableArrayList ();
            notes.addAll (this.projectDAO.getAllNotesForGroup (group.getId ()));
            this.noteObservableListMap.put (group, notes);
        }

    }

    @FXML
    public void initialize () {
        userEmailLabel.setText (resourceBundle.getString ("EmailAdress") + " " + user.getEmailAdress ());
        userNameLabel.setText (resourceBundle.getString ("Name") + " " + user.getFirstName () + " " + user.getLastName ());
        userUsernameLabel.setText (resourceBundle.getString ("Username") + " " + user.getUserName ());

        noteModel = new NoteModel ();

        noteModel.getNotes ().addListener (new ListChangeListener<Note>() {
            public void onChanged(Change<? extends Note> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //permutate
                        }
                    } else if (c.wasUpdated()) {
                        changeFlowPane (c.getList ());
                    } else {
                        for (Note remitem : c.getRemoved()) {
                            removeItemFromPane(remitem);
                        }
                        for (Note additem : c.getAddedSubList()) {
                            flowPaneForNotes.getChildren ().add (getNoteBlock (additem));
                        }
                    }
                }
            }
        });


        groupListView = new ListView<> ();
        groupListView.setItems (groupsObservableList);
        groupListView.setCellFactory (listView -> new GroupListCellController (groupsObservableList, projectDAO, resourceBundle));
        groupListView.getSelectionModel ().selectedItemProperty().addListener((obs, oldKorisnik, newKorisnik) -> {
            if(newKorisnik != null) {
                noteModel.getNotes ().clear ();
                noteModel.getNotes ().addAll (projectDAO.getAllNotesForGroup (newKorisnik.getId ()));
            }
        });
        vboxForListview.getChildren ().add (2, groupListView);

        labelListView = new ListView<>();
        labelListView.setItems(labelObservableList);
        labelListView.setCellFactory(listView -> new LabelListCellController (labelObservableList, projectDAO, resourceBundle));

        labelListView.getSelectionModel ().selectedItemProperty ().addListener ((obs, oldLabel, newLabel) -> {
            if(newLabel != null) {
                noteModel.getNotes ().clear ();
                noteModel.getNotes ().addAll (projectDAO.getAllNotesForLabel(newLabel.getId ()));
            }
        });


//        for (Note note : noteObservableListMap.get (groupsObservableList.get (0))) {
//
//
//
//
//        }

//            try {
////                //NoteCardController noteCardController = new NoteCardController ();
////
////                FXMLLoader loader = new FXMLLoader (getClass ().getResource ("/fxml/notecard.fxml"), resourceBundle);
////                loader.setController (noteCardController);
////                Node node = loader.load ();
////
////                noteCardController.getNoteCardDescription ().setText (note.getDescription ());
////                noteCardController.getNoteCardTitle ().setText (note.getNoteTitle ());
////                noteCardController.getNoteCardGridPane ().setStyle ("-fx-background-color: "+ note.getNoteColor ().getHexCode ());
//
//
//            } catch (IOException e) {
//                e.printStackTrace ();
//            }
//
//        groupListView.getSelectionModel ().selectionModeProperty ().addListener ((obp, oldGroup, newGroup) -> {
//            if(newGroup != null) {
//                for(Note note : noteObservableListMap.get (newGroup)) {
//                    try {
//                        NoteCardController noteCardController = new NoteCardController ();
//                        noteCardController.getNoteCardDescription ().setText (note.getDescription ());
//                        noteCardController.getNoteCardTitle ().setText (note.getNoteTitle ());
//                        noteCardController.getNoteCardGridPane ().setStyle ("-fx-background-color: "+ note.getNoteColor ().getHexCode ());
//                        FXMLLoader loader = new FXMLLoader (getClass ().getResource ("/fxml/notecard.fxml"), resourceBundle);
//                        loader.setController (noteCardController);
//                        Node node = loader.load ();
//
//                        flowPaneForNotes.getChildren ().add (node);
//                    } catch (IOException e) {
//                        e.printStackTrace ();
//                    }
//                }
//            }
//        });
//
//        groupListView.getSelectionModel ().selectFirst ();

    }

    private void removeItemFromPane (Note remitem) {
        Node remove = null;
        for(Node node : flowPaneForNotes.getChildren ()) {
            if(((Label)((GridPane) node).getChildren ().get (2)).getText().equals (String.valueOf (remitem.getId ()))) {
                remove = node;
            }
        }

        if (remove != null) {
            flowPaneForNotes.getChildren ().remove (remove);
        }

    }

    private void changeFlowPane(ObservableList<? extends Note> notes) {
        List<Node> nodes = flowPaneForNotes.getChildren ();
        flowPaneForNotes.getChildren ().removeAll (nodes);
        for(Note note : notes) {
            flowPaneForNotes.getChildren ().add (getNoteBlock (note));
        }
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

        return gridPane;

    }



    public void changeToGroups() {
        vboxForListview.getChildren().remove(2);
        vboxForListview.getChildren().add(2, groupListView);
    }


    public void changeToLabels() {
        vboxForListview.getChildren().remove(2);
        vboxForListview.getChildren().add(2, labelListView);
    }

    public void createNewGroup() {
        try {
            Stage newStage = new Stage();

            Group group = new Group();
            group.setId(-2);
            group.setAccountId(user.getId());

            GroupController groupController = new GroupController(group, groupsObservableList, user, resourceBundle);

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

            LabelController labelController = new LabelController(label, labelObservableList, user, resourceBundle);

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

                NoteController noteController = new NoteController (note, labelObservableList, groupsObservableList, resourceBundle);

                FXMLLoader loader = new FXMLLoader (getClass ().getResource ("/fxml/note.fxml"), resourceBundle);
                loader.setController (noteController);

                newStage.setTitle (resourceBundle.getString ("CreateNote"));
                newStage.setScene (new Scene (loader.load (), 800, 600));
                newStage.setMinHeight (600);
                newStage.setMinWidth (800);

                newStage.show ();

                newStage.setOnHiding (windowEvent -> {
                    if (note.getId () == -1 && projectDAO.createNote (note) && !noteModel.getNotes ().isEmpty ()) {
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
}
