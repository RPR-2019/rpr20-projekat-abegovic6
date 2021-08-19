package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.LabelColorModel;
import ba.unsa.etf.rpr.projekat.model.Account;
import ba.unsa.etf.rpr.projekat.model.GroupColor;
import ba.unsa.etf.rpr.projekat.model.LabelColor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.ResourceBundle;

public class LabelController {

    private ba.unsa.etf.rpr.projekat.model.Label label;
    private final List<ba.unsa.etf.rpr.projekat.model.Label> labels;
    private final ResourceBundle resourceBundle;
    private final Account user;
    private LabelColorModel labelColorModel;
    private String color = null;

    @FXML
    public Label labelTitleLabel;
    @FXML
    public TextField labelNameTextField;
    @FXML
    public TextArea labelDescriptionTextField;
    @FXML
    public ChoiceBox<String> labelColorChoiceBox;
    @FXML
    public GridPane labelGridPane;
    @FXML
    public Label labelNameErrorLabel;
    @FXML
    public Label labelColorErrorLabel;

    public LabelController(ba.unsa.etf.rpr.projekat.model.Label label, List<ba.unsa.etf.rpr.projekat.model.Label> labels, Account user, ResourceBundle resourceBundle) {
        this.label = label;
        this.labels = labels;
        this.user = user;
        this.resourceBundle = resourceBundle;

    }

    @FXML
    public void initialize() {
        labelColorModel = new LabelColorModel();
        labelColorChoiceBox.setItems(labelColorModel.getColors());

        labelColorModel.currentColorProperty().addListener((obp, oldColor, newColor) -> {
            String hex = LabelColor.valueOf(newColor).getHexCode();
            labelGridPane.setStyle("-fx-background-color: " + hex);
            color = newColor;
        });

        if(label.getId() == -2) {
            labelTitleLabel.setText(resourceBundle.getString("CreateANewLabel"));
        } else {
            labelTitleLabel.setText(resourceBundle.getString("LabelInformation"));
            labelNameTextField.setText(label.getLabelName());
            labelDescriptionTextField.setText(label.getDescription());
            labelColorModel.setCurrentColor(label.getLabelColor().toString());
        }



    }

    public void changeCurrentColor() {
        labelColorModel.setCurrentColor(labelColorChoiceBox.getValue());

    }

    public void cancelLabelChanges(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void confirmLabelChanges(ActionEvent actionEvent) {
        boolean isAlertNeeded = false;

        labelNameTextField.getStyleClass().remove("turnRed");
        labelNameErrorLabel.getStyleClass().remove("errorLabel");
        labelNameErrorLabel.setText("");
        labelColorErrorLabel.getStyleClass().remove("errorLabel");
        labelColorErrorLabel.setText("");

        String groupName = labelNameTextField.getText();
        if(groupName.length() == 0) {
            isAlertNeeded = true;
            labelNameErrorLabel.setText(resourceBundle.getString("ThisCantBeEmpty"));
            labelNameErrorLabel.getStyleClass().add("errorLabel");
            labelNameTextField.getStyleClass().add("turnRed");
        } else if(labels.stream().anyMatch(g -> g.getLabelName().equals(groupName))) {
            labelNameErrorLabel.setText(resourceBundle.getString("NewLabelNameError"));
            labelNameErrorLabel.getStyleClass().add("errorLabel");
            labelNameTextField.getStyleClass().add("turnRed");
        }

        if(color == null) {
            isAlertNeeded = true;
            labelColorErrorLabel.getStyleClass().add("errorLabel");
            labelColorErrorLabel.setText(resourceBundle.getString("LabelColorNotChosen"));
        }

        if(isAlertNeeded) {
            openAlertMessage();
        } else {
            label.setId(-1);
            label.setLabelName(groupName);
            label.setDescription(labelDescriptionTextField.getText());
            label.setLabelColor(LabelColor.valueOf(color));


            Node n = (Node) actionEvent.getSource();
            Stage stage = (Stage) n.getScene().getWindow();
            stage.close();
        }
    }

    private void openAlertMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resourceBundle.getString("Error"));
        alert.setHeaderText(resourceBundle.getString("ProblemNewAccountAlert"));
        alert.setContentText(resourceBundle.getString("PleaseTryAgain"));

        alert.showAndWait();

    }
}
