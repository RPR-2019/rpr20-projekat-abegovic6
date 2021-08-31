package ba.unsa.etf.rpr.projekat.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelpController {

    private List<Image> images;
    private SimpleObjectProperty<Integer> currentImageId;

    @FXML
    public Button nextButton;
    @FXML
    public Button backButton;
    @FXML
    public ImageView helpImageView;
    @FXML
    public Label helpLabel;

    public HelpController () {
    }

    @FXML
    public void initialize() {
        images = new ArrayList<> ();

        images.add (new Image ("/images/logoRPR.jpg"));
        images.add (new Image ("/images/logoRPR.jpg"));
        images.add (new Image ("/images/logoRPR.jpg"));

        currentImageId = new SimpleObjectProperty<> ();
        currentImageId.set (1);
        helpLabel.setText (1 + "/" + images.size ());
        helpImageView.setImage (images.get (0));
        backButton.setDisable (true);
        if(currentImageId.get () == images.size ()) {
            nextButton.setDisable (true);
        }

        currentImageId.addListener ((obp, oldId, newId) -> {
            if(newId != null) {
                helpLabel.setText (newId + "/" + images.size ());
                helpImageView.setImage (images.get (newId - 1));
                backButton.setDisable (newId == 1);
                nextButton.setDisable (newId == images.size ());
            }

        });
    }

    public void backImage() {
        currentImageId.set (currentImageId.get () - 1);

    }

    public void nextImage() {
        currentImageId.set (currentImageId.get () + 1);
    }

}
