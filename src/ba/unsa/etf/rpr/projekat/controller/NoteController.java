package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Group;
import ba.unsa.etf.rpr.projekat.model.Label;
import ba.unsa.etf.rpr.projekat.model.Note;
import javafx.fxml.FXML;

import java.util.List;
import java.util.ResourceBundle;

public class NoteController {
    private final Note note;
    private final List<Label> labels;
    private final List<Group> groups;
    private final ProjectDAO projectDAO;
    private final ResourceBundle resourceBundle;

    public NoteController(Note note, List<Label> labels, List<Group> groups, ProjectDAO projectDAO, ResourceBundle resourceBundle) {
        this.note = note;
        this.labels = labels;
        this.groups = groups;
        this.projectDAO = projectDAO;
        this.resourceBundle = resourceBundle;
    }

    @FXML
    public void initialize () {


    }



}
