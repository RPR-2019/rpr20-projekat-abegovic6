package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.GroupColorModel;
import ba.unsa.etf.rpr.projekat.model.Account;
import ba.unsa.etf.rpr.projekat.model.Group;
import ba.unsa.etf.rpr.projekat.model.GroupColor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.ResourceBundle;

public class GroupController {


    private Group group;
    private final List<Group> groups;
    private final ResourceBundle resourceBundle;
    private final Account user;
    private GroupColorModel groupColorModel;
    private String color = null;

    @FXML
    public Label groupsTitleLabel;
    @FXML
    public TextField groupNameTextField;
    @FXML
    public TextArea groupDescriptionTextField;
    @FXML
    public ChoiceBox<String> groupColorChoiceBox;
    @FXML
    public GridPane groupGridPane;
    @FXML
    public Label groupNameErrorLabel;
    @FXML
    public Label groupColorErrorLabel;

    public GroupController(Group group, List<Group> groups, Account user, ResourceBundle resourceBundle) {
        this.group = group;
        this.groups = groups;
        this.user = user;
        this.resourceBundle = resourceBundle;

    }

    @FXML
    public void initialize() {
        groupColorModel = new GroupColorModel();
        groupColorChoiceBox.setItems(groupColorModel.getColors());

        groupColorModel.currentColorProperty().addListener((obp, oldColor, newColor) -> {
            String hex = GroupColor.valueOf(newColor).getHexCode();
            groupGridPane.setStyle("-fx-background-color: " + hex);
            color = newColor;
        });

        if(group.getId() == -2) {
            groupsTitleLabel.setText(resourceBundle.getString("CreateANewGroup"));
        } else {
            groupsTitleLabel.setText(resourceBundle.getString("GroupInformation"));
            groupNameTextField.setText(group.getGroupName());
            groupDescriptionTextField.setText(group.getDescription());
            groupColorModel.setCurrentColor(group.getGroupColor().toString());
            groupColorChoiceBox.getSelectionModel().select(group.getGroupColor().toString());
        }



    }

    public void changeCurrentColor() {
        groupColorModel.setCurrentColor(groupColorChoiceBox.getValue());

    }

    public void cancelGroupChanges(ActionEvent actionEvent) {
        group.setUpdatedNeeded(false);
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void confirmGroupChanges(ActionEvent actionEvent) {
        boolean isAlertNeeded = false;

        groupNameTextField.getStyleClass().remove("turnRed");
        groupNameErrorLabel.getStyleClass().remove("errorLabel");
        groupNameErrorLabel.setText("");
        groupColorErrorLabel.getStyleClass().remove("errorLabel");
        groupColorErrorLabel.setText("");

        String groupName = groupNameTextField.getText();
        if(groupName.length() == 0) {
            isAlertNeeded = true;
            groupNameErrorLabel.setText(resourceBundle.getString("ThisCantBeEmpty"));
            groupNameErrorLabel.getStyleClass().add("errorLabel");
            groupNameTextField.getStyleClass().add("turnRed");
        } else if(groups.stream().anyMatch(g -> g.getGroupName().equals(groupName))) {
            groupNameErrorLabel.setText(resourceBundle.getString("NewGroupNameError"));
            groupNameErrorLabel.getStyleClass().add("errorLabel");
            groupNameTextField.getStyleClass().add("turnRed");
        }

        if(color == null) {
            isAlertNeeded = true;
            groupColorErrorLabel.getStyleClass().add("errorLabel");
            groupColorErrorLabel.setText(resourceBundle.getString("GroupColorNotChosen"));
        }

        if(isAlertNeeded) {
            openAlertMessage();
        } else if(group.getId() == -2){
            group.setId(-1);
            group.setGroupName(groupName);
            group.setDescription(groupDescriptionTextField.getText());
            group.setGroupColor(GroupColor.valueOf(color));


            Node n = (Node) actionEvent.getSource();
            Stage stage = (Stage) n.getScene().getWindow();
            stage.close();
        } else {
            group.setGroupName(groupName);
            group.setDescription(groupDescriptionTextField.getText());
            group.setGroupColor(GroupColor.valueOf(color));
            group.setUpdatedNeeded(true);
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
