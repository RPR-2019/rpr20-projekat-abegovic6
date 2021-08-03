package ba.unsa.etf.rpr.projekat;

import ba.unsa.etf.rpr.projekat.database.ProjectDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        primaryStage.setTitle("NOTA");
        primaryStage.setScene(new Scene(root, 1100, 600));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(1100);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
