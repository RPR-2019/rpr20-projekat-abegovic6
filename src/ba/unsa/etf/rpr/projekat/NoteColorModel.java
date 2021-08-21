package ba.unsa.etf.rpr.projekat;

import ba.unsa.etf.rpr.projekat.model.GroupColor;
import ba.unsa.etf.rpr.projekat.model.NoteColor;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NoteColorModel {

    ObservableList<String> colors;
    SimpleStringProperty currentColor;

    public NoteColorModel() {
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
        for(NoteColor noteColor : NoteColor.values()) {
            colors.add(noteColor.toString());
        }
    }

    public ObservableList<String> getColors() {
        return colors;
    }

    public void setColors(ObservableList<String> colors) {
        this.colors = colors;
    }
}
