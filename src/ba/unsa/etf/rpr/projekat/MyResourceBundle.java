package ba.unsa.etf.rpr.projekat;

import java.util.Locale;
import java.util.ResourceBundle;

public class MyResourceBundle {
    private static Locale locale;

    private MyResourceBundle () {
    }

    public static Locale getLocale () {
        return locale;
    }

    public static void setLocale(Locale locale) {
        MyResourceBundle.locale = locale;
    }

    public static String getString(String key) {
        return ResourceBundle.getBundle("Translation", locale).getString(key);
    }

    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle ("Translation", locale);
    }

}
