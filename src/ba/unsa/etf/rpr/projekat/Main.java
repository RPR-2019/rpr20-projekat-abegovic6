package ba.unsa.etf.rpr.projekat;

import ba.unsa.etf.rpr.projekat.controller.LoginController;
import ba.unsa.etf.rpr.projekat.dal.AccountModel;
import ba.unsa.etf.rpr.projekat.dto.Account;
import ba.unsa.etf.rpr.projekat.utilities.MyHostServices;
import ba.unsa.etf.rpr.projekat.utilities.MyResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MyResourceBundle.setLocale (Locale.getDefault());
        MyHostServices.setHostServices (getHostServices ());
        AccountModel.setCurrentUser (new Account ());

        System.out.println("JavaFX Version: " + System.getProperty("javafx.version"));
        System.out.println("JavaFX Runtime Version: " + System.getProperty("javafx.runtime.version"));

        LoginController loginController = new LoginController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), MyResourceBundle.getResourceBundle ());
        loader.setController(loginController);
        Parent root = loader.load();
        primaryStage.setTitle(MyResourceBundle.getString ("LogInTitle"));
        primaryStage.setScene(new Scene(root, 1100, 600));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(1100);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
