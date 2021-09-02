package ba.unsa.etf.rpr.projekat.model;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.javabean.Account;
import ba.unsa.etf.rpr.projekat.javabean.Label;
import ba.unsa.etf.rpr.projekat.javabean.Note;
import ba.unsa.etf.rpr.projekat.javabean.NoteColor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class NoteModel {
    private static NoteModel instance;

    private final PreparedStatement getAllNotesForGroupStatement;
    private final PreparedStatement createNoteStatement;
    private final PreparedStatement updateNotesStatement;
    private final PreparedStatement deleteNoteStatement;
    private final PreparedStatement getNewIdNoteStatement;
    private final PreparedStatement getAllNotesForAccountStatement;
    private final PreparedStatement addNewLabelForNoteStatement;
    private final PreparedStatement removeLabelsFromNote;
    private final PreparedStatement deleteNoteIdStatement;

    ObservableList<Note> allNotes;
    ObservableList<Note> currentNotes;

    private NoteModel (Connection conn) throws SQLException {
        this.currentNotes = FXCollections.observableArrayList();
        this.allNotes = FXCollections.observableArrayList ();

        createNoteStatement = conn.prepareStatement("INSERT INTO notes (id, groupId, noteTitle, description, noteColor, image, dateCreated, dateUpdated) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        updateNotesStatement = conn.prepareStatement("UPDATE notes SET (noteTitle, description, noteColor, image, dateUpdated) " + "= (?,?,?,?,?) WHERE id = ?");
        getAllNotesForGroupStatement = conn.prepareStatement("SELECT * FROM notes WHERE groupId = ?");
        deleteNoteStatement = conn.prepareStatement("DELETE FROM notes WHERE id = ?");
        getNewIdNoteStatement = conn.prepareStatement("SELECT MAX(id) + 1 FROM notes");
        getAllNotesForAccountStatement = conn.prepareStatement ("SELECT n.* FROM notes n, groups g WHERE g.accountId = ? AND g.id = n.groupId");

        addNewLabelForNoteStatement = conn.prepareStatement("INSERT INTO intertable (noteId, labelId) VALUES (?, ?)");
        removeLabelsFromNote = conn.prepareStatement("DELETE FROM intertable WHERE noteId = ?");
        deleteNoteIdStatement = conn.prepareStatement("DELETE FROM intertable WHERE noteId = ?");
    }

    public static NoteModel getInstance(Connection conn) throws SQLException {
        if(instance == null) instance = new NoteModel (conn);
        return instance;
    }

    private NoteColor stringToNoteColor(String string) {
        return NoteColor.valueOf(string);
    }

    private String localeDateTimeToString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format (formatter);
    }

    private LocalDateTime stringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    private Note getNoteFromResultSet (ResultSet resultSetNotes, LabelModel labelModel) {
        try {
            Note note = new Note();
            note.setId(resultSetNotes.getInt(1));
            note.setGroupId(resultSetNotes.getInt(2));
            note.setNoteTitle(resultSetNotes.getString(3));
            note.setDescription(resultSetNotes.getString(4));
            note.setNoteColor(stringToNoteColor(resultSetNotes.getString(5)));
            note.setDateCreated (stringToLocalDateTime (resultSetNotes.getString (7)));
            note.setDateUpdated (stringToLocalDateTime (resultSetNotes.getString (8)));
            note.setLabels (labelModel.getLabelListForNote (note.getId ()));
            note.setImage (null);


            return note;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<Note> getNoteListFromResultSet(ResultSet resultSetNotes, LabelModel labelModel) {
        try {
            List<Note> notes = new ArrayList<> ();
            while(resultSetNotes.next()) {
                notes.add(getNoteFromResultSet(resultSetNotes, labelModel));
            }
            return notes;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Note> getAllNotesForUser(Account user, LabelModel labelModel) {
        try {
            getAllNotesForAccountStatement.setInt (1, user.getId ());
            return getNoteListFromResultSet(getAllNotesForAccountStatement.executeQuery(), labelModel);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();

    }

    public List<Note> getAllNotesForGroup(int groupId, LabelModel labelModel) {
        try {
            getAllNotesForGroupStatement.setInt(1, groupId);
            return getNoteListFromResultSet(getAllNotesForGroupStatement.executeQuery(), labelModel);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    public boolean createNote (Note note) {
        try {
            ResultSet resultSet = getNewIdNoteStatement.executeQuery();
            if(resultSet.next()) {
                note.setId(resultSet.getInt(1));
            } else {
                note.setId(1);
            }
            createNoteStatement.setInt(1, note.getId());
            createNoteStatement.setInt(2, note.getGroupId ());
            createNoteStatement.setString(3, note.getNoteTitle ());
            createNoteStatement.setString(4, note.getDescription ());
            createNoteStatement.setString(5, note.getNoteColor ().name ());
            createNoteStatement.setBytes (6, note.getImage ());
            createNoteStatement.setString (7, localeDateTimeToString (LocalDateTime.now ()));
            createNoteStatement.setString (8, localeDateTimeToString (LocalDateTime.now ()));
            createNoteStatement.executeUpdate();

            setLabelListForNote (note.getId (), note.getLabels ());

            allNotes.add (note);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    private void updateNoteLabels(int id, List<Label> labels) {
        removeLabelListForNote(id);
        setLabelListForNote (id, labels);
    }

    public void updateNote (Note note) {
        try {
            updateNotesStatement.setInt(5, note.getId());
            updateNotesStatement.setString(1, note.getNoteTitle ());
            updateNotesStatement.setString(2, note.getDescription());
            updateNotesStatement.setString(3, note.getNoteColor ().name ());
            updateNotesStatement.setBytes (4, note.getImage ());
            updateNotesStatement.setString (5, localeDateTimeToString (LocalDateTime.now ()));

            updateNoteLabels (note.getId (), note.getLabels ());

            updateNotesStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteNote(int id) {
        try {
            deleteNoteStatement.setInt (1, id);
            deleteNoteStatement.executeUpdate ();
            deleteNoteIdStatement.setInt (1, id);
            deleteNoteIdStatement.executeUpdate ();

            Optional<Note> note = allNotes.stream ().filter (note1 -> note1.getId () == id).findAny ();
            note.ifPresent (value -> allNotes.remove (value));
            note = currentNotes.stream ().filter (note1 -> note1.getId () == id).findAny ();
            note.ifPresent (value -> currentNotes.remove (value));

        } catch (SQLException throwables) {
            throwables.printStackTrace ();
        }

    }

    private void setLabelListForNote(int id, List<Label> labels) {
        for (Label label : labels) {
            try {
                addNewLabelForNoteStatement.setInt (1, id);
                addNewLabelForNoteStatement.setInt (2, label.getId ());
                addNewLabelForNoteStatement.executeUpdate ();
            } catch (SQLException throwables) {
                throwables.printStackTrace ();
            }
        }
    }

    private void removeLabelListForNote (int id) {
        try {
            removeLabelsFromNote.setInt (1, id);
            removeLabelsFromNote.executeUpdate ();
        } catch (SQLException throwables) {
            throwables.printStackTrace ();
        }
    }


    public ObservableList<Note> getCurrentNotes () {
        return currentNotes;
    }

    public ObservableList<Note> getAllNotes () {
        return allNotes;
    }

    public void setAllNotes (ObservableList<Note> allNotes) {
        this.allNotes = allNotes;
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

    public List<Note> getNotesForGroup(int id) {
        return allNotes.stream ().filter (note -> note.getGroupId () == id).collect (Collectors.toList ());
    }

    public List<Note> getNotesForLabel(int id) {
        return allNotes.stream().filter (note -> note.getLabels ().stream ()
                .anyMatch (l -> l.getId () == id))
                .collect(Collectors.toList ());
    }


}
