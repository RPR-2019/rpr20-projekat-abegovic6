package ba.unsa.etf.rpr.projekat.model;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NoteModel {
    ObservableList<Note> allNotes;
    ObservableList<Note> currentNotes;

    public NoteModel() {
        this.currentNotes = FXCollections.observableArrayList();
        this.allNotes = FXCollections.observableArrayList ();
    }

    public ObservableList<Note> getCurrentNotes () {
        return currentNotes;
    }

    public void setCurrentNotes (ObservableList<Note> currentNotes) {
        this.currentNotes = currentNotes;
    }

    public void sortNotes(String sort) {
        ArrayList<Note> list;
        if(sort.equals (MyResourceBundle.getString ("LastAdded"))) {
            list = new ArrayList<> (currentNotes.sorted (Comparator.comparing (Note::getId).reversed ()));
        } else if (sort.equals (MyResourceBundle.getString ("FirstAdded"))) {
            list= new ArrayList<> (currentNotes.sorted (Comparator.comparing (Note::getId)));
        } else if(sort.equals (MyResourceBundle.getString ("LastUpdated"))) {
            list = new ArrayList<> (currentNotes.sorted (Comparator.comparing (Note::getDateUpdated).reversed ()));
        } else if (sort.equals (MyResourceBundle.getString ("FirstUpdated"))) {
            list = new ArrayList<> (currentNotes.sorted (Comparator.comparing (Note::getDateUpdated)));
        } else if (sort.equals (MyResourceBundle.getString ("ByNameAsc"))) {
            list = new ArrayList<> (currentNotes.sorted (Comparator.comparing (Note::getNoteTitle)));
        } else if (sort.equals (MyResourceBundle.getString ("ByNameDesc"))) {
            list = new ArrayList<> (currentNotes.sorted (Comparator.comparing (Note::getNoteTitle).reversed ()));
        } else if (sort.equals (MyResourceBundle.getString ("ByDescriptionAsc"))) {
            list = new ArrayList<> (currentNotes.sorted (Comparator.comparing (Note::getDescription)));
        } else  {
            list = new ArrayList<> (currentNotes.sorted (Comparator.comparing (Note::getDescription).reversed ()));
        }

        currentNotes.clear ();
        currentNotes.addAll (list);
    }

    public void searchNotes(String text) {
        List<Note> list = allNotes.stream ().filter (note -> note.getNoteTitle ().toLowerCase ().contains (text.toLowerCase ()) ||
                note.getDescription ().toLowerCase ().contains (text.toLowerCase ())).collect(Collectors.toList ());

        currentNotes.clear ();
        currentNotes.addAll (list);
    }


}
