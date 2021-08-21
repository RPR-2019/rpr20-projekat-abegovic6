package ba.unsa.etf.rpr.projekat.model;

public enum NoteColor {
    BLUE("#048a81"),
    COPPER("#c57b57"),
    GREEN("#70ae6e"),
    ORANGE("#ec7d10"),
    PINK("#d81159"),
    PURPLE("#6e2594"),
    RED("#f8333c"),
    YELLOW("#f5b301");

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
