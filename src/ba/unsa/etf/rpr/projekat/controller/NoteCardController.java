package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.model.Note;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class NoteCardController {

    @FXML
    public Label noteCardTitle;
    @FXML
    public Label noteCardDescription;
    @FXML
    public GridPane noteCardGridPane;


    public NoteCardController () {
    }

    @FXML
    public void initialize () {


    }

    public Label getNoteCardTitle () {
        return noteCardTitle;
    }

    public Label getNoteCardDescription () {
        return noteCardDescription;
    }

    public GridPane getNoteCardGridPane () {
        return noteCardGridPane;
    }
}
