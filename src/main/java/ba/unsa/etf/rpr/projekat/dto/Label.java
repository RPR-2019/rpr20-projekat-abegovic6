package ba.unsa.etf.rpr.projekat.dto;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import javafx.beans.property.SimpleStringProperty;


import java.util.List;

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

    public Label (int id, int accountId, String labelName, String description, String labelColor) {
        this.id = id;
        this.accountId = accountId;
        this.labelName = new SimpleStringProperty();
        this.labelColor = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        setLabelName (labelName);
        this.description.set (description);
        this.labelColor.set (labelColor);
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

    public ba.unsa.etf.rpr.projekat.dto.LabelColor getLabelColor() {
        if(labelColor.get () == null) return null;
        return ba.unsa.etf.rpr.projekat.dto.LabelColor.valueOf(labelColor.get());
    }

    public void setLabelColor(ba.unsa.etf.rpr.projekat.dto.LabelColor labelColor) {
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

    public String writeInFile (List<ba.unsa.etf.rpr.projekat.dto.Note> notes) {
        String string = MyResourceBundle.getString ("LabelId") + id + "\n" + MyResourceBundle.getString ("LabelName") +
                labelName.get () + "\n" + MyResourceBundle.getString ("LabelDescription") + description.get ()
                + "\n----------\n" + MyResourceBundle.getString ("UserNotes") + "\n\n";
        for (ba.unsa.etf.rpr.projekat.dto.Note note : notes) {
            string += note.writeInFile();
        }

        return string;
    }


}
