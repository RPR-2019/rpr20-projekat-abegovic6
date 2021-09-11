package ba.unsa.etf.rpr.projekat.dal;

import ba.unsa.etf.rpr.projekat.dto.LabelColor;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LabelColorModel {

    ObservableList<String> colors;
    SimpleStringProperty currentColor;

    public LabelColorModel() {
        this.colors = FXCollections.observableArrayList();
        currentColor = new SimpleStringProperty();
        fill();
    }

    public SimpleStringProperty currentColorProperty() {
        return currentColor;
    }

    public void setCurrentColor(String currentColor) {
        this.currentColor.set(currentColor);
    }

    public void fill() {
        for(LabelColor labelColor : LabelColor.values()) {
            colors.add(labelColor.toString());
        }
    }

    public ObservableList<String> getColors() {
        return colors;
    }

    public void setColors(ObservableList<String> colors) {
        this.colors = colors;
    }
}
