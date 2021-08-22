package ba.unsa.etf.rpr.projekat.model;

import ba.unsa.etf.rpr.projekat.model.GroupColor;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GroupColorModel {

    ObservableList<String> colors;
    SimpleStringProperty currentColor;

    public GroupColorModel() {
        this.colors = FXCollections.observableArrayList();
        currentColor = new SimpleStringProperty();
        fill();
    }

    public String getCurrentColor() {
        return currentColor.get();
    }

    public SimpleStringProperty currentColorProperty() {
        return currentColor;
    }

    public void setCurrentColor(String currentColor) {
        this.currentColor.set(currentColor);
    }

    public void fill() {
        for(GroupColor groupColor : GroupColor.values()) {
            colors.add(groupColor.toString());
        }
    }

    public ObservableList<String> getColors() {
        return colors;
    }

    public void setColors(ObservableList<String> colors) {
        this.colors = colors;
    }
}
