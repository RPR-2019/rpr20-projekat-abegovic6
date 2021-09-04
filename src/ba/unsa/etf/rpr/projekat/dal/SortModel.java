package ba.unsa.etf.rpr.projekat.dal;

import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SortModel {

    ObservableList<String> sorting;
    SimpleStringProperty currentSorting;


    public SortModel() {
        this.sorting = FXCollections.observableArrayList();
        currentSorting = new SimpleStringProperty();
        fill();
    }

    public SimpleStringProperty currentSortingProperty () {
        return currentSorting;
    }

    public void setCurrentSorting (String currentSorting) {
        this.currentSorting.set(currentSorting);
    }

    public void fill() {
        sorting.add (MyResourceBundle.getString ("LastAdded"));
        sorting.add (MyResourceBundle.getString ("FirstAdded"));
        sorting.add (MyResourceBundle.getString ("LastUpdated"));
        sorting.add (MyResourceBundle.getString ("FirstUpdated"));
        sorting.add (MyResourceBundle.getString ("ByNameAsc"));
        sorting.add (MyResourceBundle.getString ("ByNameDesc"));
        sorting.add (MyResourceBundle.getString ("ByDescriptionAsc"));
        sorting.add (MyResourceBundle.getString ("ByDescriptionDesc"));

    }

    public ObservableList<String> getSorting () {
        return sorting;
    }

}
