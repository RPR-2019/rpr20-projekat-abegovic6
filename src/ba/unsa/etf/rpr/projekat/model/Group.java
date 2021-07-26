package ba.unsa.etf.rpr.projekat.model;

import java.util.Objects;

public class Group {
    private int id;
    private int accountId;
    private String groupName;
    private String description;
    private GroupColor groupColor;

    public Group() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GroupColor getColor() {
        return groupColor;
    }

    public void setColor(GroupColor groupColor) {
        this.groupColor = groupColor;
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
