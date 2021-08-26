package ba.unsa.etf.rpr.projekat;

import ba.unsa.etf.rpr.projekat.controller.LoginController;
import ba.unsa.etf.rpr.projekat.dao.ProjectDAO;
import ba.unsa.etf.rpr.projekat.model.Account;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Account user = new Account();
        Locale currentLocale = Locale.getDefault();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Translation", currentLocale);

        ProjectDAO projectDAO = ProjectDAO.getInstance();
        LoginController loginController = new LoginController(projectDAO, user, resourceBundle, getHostServices ());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), resourceBundle);
        loader.setController(loginController);
        Parent root = loader.load();
        primaryStage.setTitle("NOTA");
        primaryStage.setScene(new Scene(root, 1100, 600));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(1100);
        primaryStage.show();
        //ProjectDAO.removeInstance();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
