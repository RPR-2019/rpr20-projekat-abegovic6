package ba.unsa.etf.rpr.projekat.dto;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;
import java.util.Objects;

public class Group {
    private int id;
    private int accountId;
    private final SimpleStringProperty groupName;
    private final SimpleStringProperty description;
    private final SimpleStringProperty groupColor;

    private boolean isUpdatedNeeded;
    private boolean delete;

    public Group() {
        groupName = new SimpleStringProperty();
        description = new SimpleStringProperty();
        groupColor = new SimpleStringProperty();
    }

    public Group (int id, int accountId, String groupName, String description, String groupColor) {
        this.id = id;
        this.accountId = accountId;
        this.groupName = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.groupColor = new SimpleStringProperty();
        setGroupName (groupName);
        this.description.set (description);
        this.groupColor.set (groupColor);
    }

    public boolean isUpdatedNeeded() {
        return isUpdatedNeeded;
    }

    public void setUpdatedNeeded(boolean updatedNeeded) {
        isUpdatedNeeded = updatedNeeded;
    }

    public boolean isDelete () {
        return delete;
    }

    public void setDelete (boolean delete) {
        this.delete = delete;
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

    public GroupColor getGroupColor() {
        if(this.groupColor.get () == null) return null;
        return GroupColor.valueOf(groupColor.get());
    }

    public void setGroupColor(GroupColor groupColor) {
        this.groupColor.set(groupColor.name());
    }

    public String getGroupName() {
        return groupName.get();
    }

    public void setGroupName(String groupName) {
        this.groupName.set(groupName.substring(0, 1).toUpperCase() + groupName.substring(1));
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public SimpleStringProperty groupNameProperty() {
        return groupName;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleStringProperty groupColorProperty() {
        return groupColor;
    }

    public void setGroupColor(String groupColor) {
        this.groupColor.set(groupColor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id &&
                groupName.equals(group.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupName);
    }

    @Override
    public String toString() {
        return groupName.get();
    }

    public String writeInFile (List<Note> notes) {
        String string = MyResourceBundle.getString ("GroupId")  + id + "\n" + MyResourceBundle.getString ("GroupName")
                + groupName.get () + "\n" + MyResourceBundle.getString ("GroupDescription") + description.get ()
                + "\n----------\n" + MyResourceBundle.getString ("UserNotes") + "\n\n";
        for (Note note : notes) {
            string += note.writeInFile ();
        }

        return string;
    }
}
