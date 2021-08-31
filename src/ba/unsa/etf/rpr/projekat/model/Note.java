package ba.unsa.etf.rpr.projekat.model;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Note {
    private int id;
    private int groupId;
    private List<Label> labels;
    private String noteTitle;
    private String description;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private NoteColor noteColor;

    private boolean delete = false;

    public boolean isDelete () {
        return delete;
    }

    public void setDelete (boolean delete) {
        this.delete = delete;
    }

    private byte[] image;

    public byte[] getImage () {
        return image;
    }

    public void setImage (byte[] image) {
        this.image = image;
    }

    private boolean isUpdateNeeded = false;

    public Note() {
        labels = new ArrayList<>();
        dateUpdated = LocalDateTime.now ();
        dateCreated = LocalDateTime.now ();

    }

    public NoteColor getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(NoteColor groupColor) {
        this.noteColor = groupColor;
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
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle.substring(0, 1).toUpperCase() + noteTitle.substring(1);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
