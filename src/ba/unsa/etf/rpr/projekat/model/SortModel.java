package ba.unsa.etf.rpr.projekat.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ResourceBundle;

public class SortModel {

    private final ResourceBundle resourceBundle;
    ObservableList<String> sorting;
    SimpleStringProperty currentSorting;


    public SortModel(ResourceBundle resourceBundle) {
        this.sorting = FXCollections.observableArrayList();
        currentSorting = new SimpleStringProperty();
        this.resourceBundle = resourceBundle;
        fill();
    }

    public String getCurrentSorting () {
        return currentSorting.get();
    }

    public SimpleStringProperty currentSortingProperty () {
        return currentSorting;
    }

    public void setCurrentSorting (String currentSorting) {
        this.currentSorting.set(currentSorting);
    }

    public void fill() {
        sorting.add (resourceBundle.getString ("LastAdded"));
        sorting.add (resourceBundle.getString ("FirstAdded"));
        sorting.add (resourceBundle.getString ("ByNameAsc"));
        sorting.add (resourceBundle.getString ("ByNameDesc"));
        sorting.add (resourceBundle.getString ("ByDescriptionAsc"));
        sorting.add (resourceBundle.getString ("ByDescriptionDesc"));

    }

    public ObservableList<String> getSorting () {
        return sorting;
    }

    public void setSorting (ObservableList<String> sorting) {
        this.sorting = sorting;
    }
}
