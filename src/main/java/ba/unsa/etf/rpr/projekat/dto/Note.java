package ba.unsa.etf.rpr.projekat.dto;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Note {
    private int id;
    private int groupId;
    private final ObservableList<TextStyle> textStyles;
    private final SimpleStringProperty noteTitle;
    private final SimpleStringProperty description;
    private final SimpleStringProperty noteColor;
    private String image;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private List<Label> labels;
    private boolean delete = false;
    private boolean isUpdateNeeded = false;



    public Note() {
        labels = new ArrayList<>();
        textStyles = FXCollections.observableArrayList ();
        dateUpdated = LocalDateTime.now ();
        dateCreated = LocalDateTime.now ();

        noteTitle = new SimpleStringProperty ();
        description = new SimpleStringProperty ();
        noteColor = new SimpleStringProperty ();
    }

    public Note (int id, int groupId, String noteTitle, String description, String noteColor) {
        this.id = id;
        this.groupId = groupId;
        this.noteTitle = new SimpleStringProperty ();
        this.description = new SimpleStringProperty ();
        this.noteColor = new SimpleStringProperty ();
        setNoteTitle (noteTitle);
        this.description.set (description);
        this.noteColor.set (noteColor);
        labels = new ArrayList<>();
        textStyles = FXCollections.observableArrayList ();
        dateUpdated = LocalDateTime.now ();
        dateCreated = LocalDateTime.now ();
    }

    public ObservableList<TextStyle> getTextStyles () {
        return textStyles;
    }

    public boolean isDelete () {
        return delete;
    }

    public void setDelete (boolean delete) {
        this.delete = delete;
    }

    public String getImage () {
        return image;
    }

    public void setImage (String image) {
        this.image = image;
    }

    public NoteColor getNoteColor() {
        if(noteColor.get () == null) return null;
        return NoteColor.valueOf (noteColor.get ());
    }

    public void setNoteColor(NoteColor groupColor) {
        this.noteColor.set (groupColor.name ());
    }

    public SimpleStringProperty noteColorProperty () {
        return noteColor;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteTitle() {
        return noteTitle.get ();
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle.set (noteTitle.substring(0, 1).toUpperCase() + noteTitle.substring(1));
    }

    public SimpleStringProperty noteTitleProperty () {
        return noteTitle;
    }

    public String getDescription() {
        return description.get ();
    }

    public void setDescription(String description) {
        this.description.set (description);
    }

    public SimpleStringProperty descriptionProperty () {
        return description;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public boolean isUpdateNeeded () {
        return isUpdateNeeded;
    }

    public void setUpdateNeeded (boolean updateNeeded) {
        isUpdateNeeded = updateNeeded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String writeInFile () {
        String string = MyResourceBundle.getString ("NoteId") + id + "\n" + MyResourceBundle.getString ("GroupId")
                + groupId + "\n" + MyResourceBundle.getString ("NoteTitle")+ noteTitle + "\n" +
                MyResourceBundle.getString ("NoteText") + description
                + "\n----------\n" + MyResourceBundle.getString ("NoteLabels") + "\n \n";
        for(Label label : labels) {
            string += label.getLabelName () + "\n";
        }
        string += "----------\n \n";
        return string;
    }
}
