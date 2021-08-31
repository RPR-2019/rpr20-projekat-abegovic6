package ba.unsa.etf.rpr.projekat.model;

import ba.unsa.etf.rpr.projekat.MyResourceBundle;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;
import java.util.ResourceBundle;

public class Label {
    private int id;
    private int accountId;
    private final SimpleStringProperty labelName;
    private final SimpleStringProperty description;
    private final SimpleStringProperty labelColor;
    private boolean isUpdateNeeded;
    private boolean delete;

    public Label() {
        labelName = new SimpleStringProperty();
        labelColor = new SimpleStringProperty();
        description = new SimpleStringProperty();
    }

    public boolean isDelete () {
        return delete;
    }

    public void setDelete (boolean delete) {
        this.delete = delete;
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
        return labelName.get();
    }

    public void setLabelName(String labelName) {
        this.labelName.set(labelName.substring(0, 1).toUpperCase() + labelName.substring(1));
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public LabelColor getLabelColor() {
        return LabelColor.valueOf(labelColor.get());
    }

    public void setLabelColor(LabelColor labelColor) {
        this.labelColor.set(labelColor.name());
    }

    public SimpleStringProperty labelNameProperty() {
        return labelName;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleStringProperty labelColorProperty() {
        return labelColor;
    }

    public void setLabelColor(String labelColor) {
        this.labelColor.set(labelColor);
    }

    public String writeInFile (List<Note> notes) {
        String string = MyResourceBundle.getString ("LabelId") + id + "\n" + MyResourceBundle.getString ("LabelName") +
                labelName.get () + "\n" + MyResourceBundle.getString ("LabelDescription") + description.get ()
                + "\n----------\n" + MyResourceBundle.getString ("UserNotes") + "\n\n";
        for (Note note : notes) {
            string += note.writeInFile();
        }

        return string;
    }


}
