package ba.unsa.etf.rpr.projekat;

import ba.unsa.etf.rpr.projekat.model.Group;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GroupModel {

    private final List<Group> listGroups;
    ObservableList<String> groups;
    SimpleStringProperty currentGroup;

    public GroupModel(List<Group> groups) {
        this.groups = FXCollections.observableArrayList();
        currentGroup = new SimpleStringProperty();
        this.listGroups = groups;
        for (Group group : groups) {
            this.groups.add(group.getGroupName());
        }
    }

    public String getCurrentGroup() {
        return currentGroup.get();
    }

    public SimpleStringProperty currentGroupProperty() {
        return currentGroup;
    }

    public void setCurrentGroup(String group) {
        this.currentGroup.set(group);
    }

    public ObservableList<String> getGroups() {
        return groups;
    }

    public String getNameFromId(int id) {
        return listGroups.stream().filter(g -> g.getId() == id).findFirst().get().getGroupName();
    }

    public int getIdFromName(String name) {
        return listGroups.stream().filter(g -> g.getGroupName().equals(name)).findFirst().get().getId();
    }


}
