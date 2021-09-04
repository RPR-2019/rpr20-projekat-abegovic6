package javabeantest;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import ba.unsa.etf.rpr.projekat.dto.Label;
import ba.unsa.etf.rpr.projekat.dto.LabelColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LabelTest {
    private static Label label;

    @BeforeAll
    static void setUp () {
        label = new Label ();
        MyResourceBundle.setLocale (new Locale ("en"));
    }

    @Test
    void groupIsEmpty() {
        label = new Label ();
        assertEquals (0, label.getId ());
        assertEquals (0, label.getAccountId ());
        assertNull (label.getLabelName ());
        assertNull (label.getDescription ());
        assertNull (label.getLabelColor ());
    }

    @Test
    void secondConstructorTest() {
        label = new Label (0, 0, "labelname", "labeldescription", "BLUE");
        assertEquals (0, label.getId ());
        assertEquals (0, label.getAccountId ());
        assertEquals ("Labelname", label.getLabelName ());
        assertEquals ("labeldescription", label.getDescription ());
        assertEquals (LabelColor.BLUE, label.getLabelColor ());
    }
}
