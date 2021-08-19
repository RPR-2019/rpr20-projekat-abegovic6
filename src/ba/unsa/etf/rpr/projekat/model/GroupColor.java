package ba.unsa.etf.rpr.projekat.model;

public enum GroupColor {
    BLUE("#048a81"),
    GREEN("#70ae6e"),
    PINK("#d81159"),
    PURPLE("#6e2594"),
    RED("#f8333c"),
    ORANGE("#ec7d10"),
    YELLOW("f80700"),
    COPPER("#c57b57");

    private String value;

    private GroupColor() {
    }

    private GroupColor(String value) {
        this.value = value;
    }

    public String getHexCode() {
        return this.value;
    }

}
