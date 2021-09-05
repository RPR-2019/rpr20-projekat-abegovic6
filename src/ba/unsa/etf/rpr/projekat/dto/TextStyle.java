package ba.unsa.etf.rpr.projekat.dto;

public enum TextStyle {
    BOLD("bold"),
    ITALIC("italic"),
    UNDERLINE("underline "),
    STRIKETROUGH("striketrough");

    private String value;

    private TextStyle () {
    }

    private TextStyle (String value) {
        this.value = value;
    }

    public String getCss() {
        return this.value;
    }
}
