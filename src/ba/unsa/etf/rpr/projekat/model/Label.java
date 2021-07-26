package ba.unsa.etf.rpr.projekat.model;

public class Label {
    private int id;
    private int accountId;
    private String labelName;
    private String description;
    private LabelColor labelColor;

    public Label() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LabelColor getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(LabelColor labelColor) {
        this.labelColor = labelColor;
    }
}
