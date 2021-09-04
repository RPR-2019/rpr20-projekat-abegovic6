package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.MyHostServices;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

public class AboutController {
    private final HostServices hostServices;
    @FXML
    public Hyperlink page;
    @FXML
    public Hyperlink git;

    public AboutController() {
        hostServices = MyHostServices.getHostServices ();
    }

    @FXML
    public void initialize() {

    }

    @FXML
    public void openURLGit() {
        hostServices.showDocument(git.getText());
    }

    @FXML
    public void openURLPage() {
        hostServices.showDocument(page.getText());
    }
}
