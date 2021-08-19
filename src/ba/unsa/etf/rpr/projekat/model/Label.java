package ba.unsa.etf.rpr.projekat.model;

public class Label {
    private int id;
    private int accountId;
    private String labelName;
    private String description;
    private LabelColor labelColor;

    private boolean isUpdateNeeded;

    public Label() {
    }

    public boolean isUpdateNeeded() {
        return isUpdateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        isUpdateNeeded = updateNeeded;
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
        this.labelName = labelName.substring(0, 1).toUpperCase() + labelName.substring(1);;
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
