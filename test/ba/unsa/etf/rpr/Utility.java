package ba.unsa.etf.rpr;

import ba.unsa.etf.rpr.projekat.dto.Account;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Utility {
    private static Utility instance = null;
    private static Account testAccount;

    private Utility() {

    }

    public static Utility getInstance() {
        if(instance == null) {
            instance = new Utility();
            testAccount = new Account (0, "firstname", "lastname",
                    "username", "email@gmail.com", "Password10#");
        }
        return instance;
    }

    public Account getTestAccount () {
        return testAccount;
    }

    public static boolean checkTextFieldStyle(TextField field, String style) {
        for (String s : field.getStyleClass())
            if (s.equals(style)) return true;
        return false;
    }

    public static boolean checkLabelStyle(Label field, String style) {
        for (String s : field.getStyleClass())
            if (s.equals(style)) return true;
        return false;
    }

}
