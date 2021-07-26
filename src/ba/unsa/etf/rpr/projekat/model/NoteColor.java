package ba.unsa.etf.rpr.projekat.model;

public enum NoteColor {
    BLUE("#CFE7EA"),
    GREEN("D4E6C4"),
    PINK("#FBC1AD"),
    POLOBLUE("#7DABD0"),
    PURPLE("#E5E1E0"),
    RED("#F56E4A"),
    YELLOW("#FFDF6F");

    private String value;

    private NoteColor() {
    }

    private NoteColor(String value) {
        this.value = value;
    }

    public String getHexCode() {
        return this.value;
    }
}
