package ba.unsa.etf.rpr.projekat.model;

import java.util.Objects;

public class Group {
    private int id;
    private int accountId;
    private String groupName;
    private String description;
    private GroupColor groupColor;

    private boolean isUpdatedNeeded;

    public Group() {
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
        return groupColor;
    }

    public void setGroupColor(GroupColor groupColor) {
        this.groupColor = groupColor;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName.substring(0, 1).toUpperCase() + groupName.substring(1);;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
