package ba.unsa.etf.rpr.projekat.dto;

public enum TextStyle {
    BOLD("bold"),
    ITALIC("italic"),
    UNDERLINE("underline "),
    STRIKETROUGH("striketrough");

    private String value;

    TextStyle () {
    }

    TextStyle (String value) {
        this.value = value;
    }

    public String getCss() {
        return this.value;
    }
}
