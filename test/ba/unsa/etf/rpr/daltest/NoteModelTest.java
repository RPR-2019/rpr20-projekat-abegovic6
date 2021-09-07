package ba.unsa.etf.rpr.daltest;

import ba.unsa.etf.rpr.Utility;
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
    private static Group group;

    @BeforeAll
    static void setUp () {
        account = Utility.getInstance ().getTestAccount ();
        group = new Group (0, account.getId (), "2 by name", "3 by description", "BLUE");
        databaseConnection = DatabaseConnection.getInstance ();
        noteModel = DatabaseConnection.getInstance ().getNoteModel ();
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @AfterAll
    static void onEnd() {
        databaseConnection.deleteTestUser (account);
        DatabaseConnection.removeInstance ();
    }

    @BeforeEach
    void forEachTest() {
        databaseConnection.deleteTestUser (account);
        databaseConnection.getAccountModel ().createAccount (account);
        databaseConnection.getGroupModel ().createGroup (group);
    }

    @Test
    void testProperty() {
        // database empty
        Note note1 = new Note (0, group.getId (), "notename", "notedescription", "BLUE");
        // create notes
        noteModel.createNote (note1);
        noteModel.createNote (note1);
        noteModel.createNote (note1);
        assertEquals (3, noteModel.getAllNotesForUser (account).size ());
        assertEquals (3, noteModel.getAllNotes ().size ());
    }

    @Test
    void labelIsCreating() {
        // database empty
        Note note1 = new Note (0, group.getId (), "notename", "notedescription", "BLUE");
        Note note2 = new Note (0, group.getId (), "notename", "notedescription", "BLUE");
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
        Note note1 = new Note (0, group.getId (), "notename", "notedescription", "BLUE");
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
        Note note1 = new Note (0, group.getId (), "notename", "notedescription", "BLUE");
        // create notes
        noteModel.createNote (note1);
        assertEquals (1,noteModel.getAllNotesForUser (account).size ());
        // delete note
        noteModel.deleteNote (note1.getId ());
        // check if deleted
        assertEquals (0, noteModel.getAllNotesForUser (account).size ());
    }


}
