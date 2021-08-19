package ba.unsa.etf.rpr.projekat.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class Group {
    private int id;
    private int accountId;
    private SimpleStringProperty groupName;
    private SimpleStringProperty description;
    private SimpleStringProperty groupColor;

    private boolean isUpdatedNeeded;

    public Group() {
        groupName = new SimpleStringProperty();
        description = new SimpleStringProperty();
        groupColor = new SimpleStringProperty();
    }

    public boolean isUpdatedNeeded() {
        return isUpdatedNeeded;
    }

    public void setUpdatedNeeded(boolean updatedNeeded) {
        isUpdatedNeeded = updatedNeeded;
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
}
