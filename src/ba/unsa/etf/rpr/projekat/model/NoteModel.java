package ba.unsa.etf.rpr.projekat.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class NoteModel {
    ObservableList<Note> notes;
    SimpleObjectProperty<Note> currentNote;

    SimpleObjectProperty<List<Note>> simpleListObjectProperty;

    public NoteModel() {
        this.notes = FXCollections.observableArrayList();
        currentNote = new SimpleObjectProperty<> ();
        simpleListObjectProperty = new SimpleObjectProperty<> ();
    }

    public List<Note> getSimpleListObjectProperty () {
        return simpleListObjectProperty.get ();
    }

    public SimpleObjectProperty<List<Note>> simpleListObjectPropertyProperty () {
        return simpleListObjectProperty;
    }

    public void setSimpleListObjectProperty (List<Note> simpleListObjectProperty) {
        this.simpleListObjectProperty.set (simpleListObjectProperty);
    }

    public Note getCurrentNote () {
        return currentNote.get();
    }

    public SimpleObjectProperty<Note> currentNoteProperty () {
        return currentNote;
    }

    public void setCurrentNote (Note note) {
        this.currentNote.set(note);
    }


    public ObservableList<Note> getNotes () {
        return notes;
    }

    public void setNotes (ObservableList<Note> notes) {
        this.notes = notes;
    }

    //    public String getNameFromId(int id) {
//        return noteList.stream().filter(g -> g.getId() == id).findFirst().get().getGroupName();
//    }
//
//    public int getIdFromName(String name) {
//        return noteList.stream().filter(g -> g.getGroupName().equals(name)).findFirst().get().getId();
//    }


}
