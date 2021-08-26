package ba.unsa.etf.rpr.projekat.controller;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

public class AboutController {
    private HostServices hostServices;
    public Hyperlink page;
    public Hyperlink git;

    public AboutController() {

    }

    @FXML
    public void initialize() {

    }

    public HostServices getHostServices() {
        return hostServices;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
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
