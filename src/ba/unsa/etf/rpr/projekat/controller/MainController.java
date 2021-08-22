package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import ba.unsa.etf.rpr.projekat.model.Group;
import ba.unsa.etf.rpr.projekat.model.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController {
    private final Account user;
    private final ResourceBundle resourceBundle;
    private final ProjectDAO projectDAO;

    private ObservableList<Group> groupsObservableList;
    private ObservableList<ba.unsa.etf.rpr.projekat.model.Label> labelObservableList;
    private Map<Group, ObservableList<Note>> noteObservableListMap;

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


        ListView<Group> groupListView = new ListView<> ();
        groupListView.setItems (groupsObservableList);
        groupListView.setCellFactory (listView -> new GroupListCellController (groupsObservableList, projectDAO, resourceBundle));
        vboxForListview.getChildren ().add (2, groupListView);

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



    public void changeToGroups() {
        ListView<Group> groupListView = new ListView<>();
        groupListView.setItems(groupsObservableList);
        groupListView.setCellFactory(listView -> new GroupListCellController (groupsObservableList, projectDAO, resourceBundle));
        vboxForListview.getChildren().remove(2);
        vboxForListview.getChildren().add(2, groupListView);

    }

    public void changeToLabels() {
        ListView<ba.unsa.etf.rpr.projekat.model.Label> labelListView = new ListView<>();
        labelListView.setItems(labelObservableList);
        labelListView.setCellFactory(listView -> new LabelListCellController (labelObservableList, projectDAO, resourceBundle));
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
                    if (note.getId () == -1) {
                        if (projectDAO.createNote (note)) {

                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }


    }
}
