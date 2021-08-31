package ba.unsa.etf.rpr.projekat.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GroupModel {

    private List<Group> listGroups;
    ObservableList<String> groups;
    SimpleStringProperty currentGroup;

    public GroupModel() {
        this.groups = FXCollections.observableArrayList();
        currentGroup = new SimpleStringProperty();
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
