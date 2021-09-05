package dtotest;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dto.Note;
import ba.unsa.etf.rpr.projekat.dto.NoteColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NoteTest {

    @BeforeAll
    static void setUp () {
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @Test
    void groupIsEmpty() {
        Note note = new Note ();
        assertEquals (0, note.getId ());
        assertEquals (0, note.getGroupId ());
        assertNull (note.getNoteTitle ());
        assertNull (note.getDescription ());
        assertNull (note.getNoteColor ());
        assertEquals (LocalDate.now (),  note.getDateCreated ().toLocalDate ());
        assertEquals (LocalDate.now (), note.getDateUpdated ().toLocalDate ());
    }

    @Test
    void secondConstructorTest() {
        Note note = new Note (0, 0, "notename", "notedescription", "BLUE");
        assertEquals (0, note.getId ());
        assertEquals (0, note.getGroupId ());
        assertEquals ("Notename", note.getNoteTitle ());
        assertEquals ("notedescription", note.getDescription ());
        assertEquals (NoteColor.BLUE, note.getNoteColor ());
        assertEquals (LocalDate.now (),  note.getDateCreated ().toLocalDate ());
        assertEquals (LocalDate.now (), note.getDateUpdated ().toLocalDate ());
    }
}
