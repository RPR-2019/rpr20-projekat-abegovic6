package daltest;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dal.DatabaseConnection;
import ba.unsa.etf.rpr.projekat.dto.Account;
import ba.unsa.etf.rpr.projekat.dto.Group;
import ba.unsa.etf.rpr.projekat.dto.Note;
import ba.unsa.etf.rpr.projekat.dal.NoteModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoteModelTest {
    private static DatabaseConnection databaseConnection;
    private static NoteModel noteModel;
    private static Account account;

    @BeforeAll
    static void setUp () {
        account = new Account (0, "firstname", "lastname",
                "username", "email@gmail.com", "Password10#");
        databaseConnection = DatabaseConnection.getInstance ();
        noteModel = DatabaseConnection.getInstance ().getNoteModel ();
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @AfterAll
    static void onEnd() {
        DatabaseConnection.removeInstance ();
    }

    @BeforeEach
    void forEachTest() {
        databaseConnection.vratiNaDefault ();
        Group group1 = new Group (0, 0, "2 by name", "3 by description", "BLUE");
        databaseConnection.getGroupModel ().createGroup (group1);
    }

    @Test
    void testProperty() {
        // database empty
        Note note1 = new Note (0, 0, "notename", "notedescription", "BLUE");
        // create notes
        noteModel.createNote (note1);
        noteModel.createNote (note1);
        noteModel.createNote (note1);
        assertEquals (3, noteModel.getAllNotesForUser (account).size ());
        assertEquals (3, noteModel.getAllNotes ().size ());
    }

//    @Test
//    void testSorting() {
//        // database empty
//        Note note1 = new Note (0, 0, "2 by name", "3 by description", "BLUE");
//        Note note2 = new Note (0, 0, "3 by name", "1 by description", "BLUE");
//        Note note3 = new Note (0, 0, "1 by name", "2 by description", "BLUE");
//        // add notes to current notes
//
//        noteModel.getCurrentNotes ().add(note1);
//        noteModel.getCurrentNotes ().add(note2);
//        noteModel.getCurrentNotes ().add(note3);
//
//        // sort by date created
//        noteModel.sortNotes (MyResourceBundle.getString ("LastAdded"));
//        assertEquals (2, noteModel.getCurrentNotes ().get (0).getId ());
//        noteModel.sortNotes (MyResourceBundle.getString ("FirstAdded"));
//        assertEquals (0, noteModel.getCurrentNotes ().get (0).getId ());
//
//        // sort by date updated
//        note2.setDateUpdated (LocalDateTime.now ());
//        noteModel.sortNotes (MyResourceBundle.getString ("LastUpdated"));
//        assertEquals (1, noteModel.getCurrentNotes ().get (0).getId ());
//        noteModel.sortNotes (MyResourceBundle.getString ("FirstUpdated"));
//        assertEquals (0, noteModel.getCurrentNotes ().get (0).getId ());
//
//        // sort by name
//        noteModel.sortNotes (MyResourceBundle.getString ("ByNameAsc"));
//        assertEquals (2, noteModel.getCurrentNotes ().get (0).getId ());
//        noteModel.sortNotes (MyResourceBundle.getString ("ByNameDesc"));
//        assertEquals (1, noteModel.getCurrentNotes ().get (0).getId ());
//
//        // sort by description
//        noteModel.sortNotes (MyResourceBundle.getString ("ByDescriptionAsc"));
//        assertEquals (1, noteModel.getCurrentNotes ().get (0).getId ());
//        noteModel.sortNotes (MyResourceBundle.getString ("ByDescriptionDesc"));
//        assertEquals (0, noteModel.getCurrentNotes ().get (0).getId ());
//
//    }

    @Test
    void labelIsCreating() {
        // database empty
        Note note1 = new Note (0, 0, "notename", "notedescription", "BLUE");
        Note note2 = new Note (0, 0, "notename", "notedescription", "BLUE");
        // create notes
        noteModel.createNote (note1); // id = 0
        noteModel.createNote (note2); // id = 1

        // notes creating
        assertEquals (2, noteModel.getAllNotesForUser (account).size ());

        // createNote should change noteid
        assertEquals (0, note1.getId ());
        assertEquals (1, note2.getId ());

    }

    @Test
    void labelIsUpdating() {
        // database empty
        Note note1 = new Note (0, 0, "notename", "notedescription", "BLUE");
        // create notes
        noteModel.createNote (note1);
        // edit label
        note1.setNoteTitle ("newname");
        // update label
        noteModel.updateNote (note1);
        // check if updated
        List<Note> notes = new ArrayList<> (noteModel.getAllNotesForUser (account));
        assertEquals ("Newname", notes.get (0).getNoteTitle ());
    }

    @Test
    void noteIsDeleting() {
        // database empty
        Note note1 = new Note (0, 0, "notename", "notedescription", "BLUE");
        // create notes
        noteModel.createNote (note1);
        assertEquals (1,noteModel.getAllNotesForUser (account).size ());
        // delete note
        noteModel.deleteNote (note1.getId ());
        // check if deleted
        assertEquals (0, noteModel.getAllNotesForUser (account).size ());
    }


}
