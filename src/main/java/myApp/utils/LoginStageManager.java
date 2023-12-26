package myApp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class LoginStageManager {

    private static Stage loginStage = new Stage();
    private static final Map<String, String> scenes = Map.of(
            "login", "/views/login.fxml",
            "signup", "/views/signup.fxml"
    );

    public static void setupLoginStage() {
        // Create a new stage


        // Set up the login page on the new stage
        switchScene("login");

        // You can add additional setup for the stage if needed

        // Show the stage
        loginStage.show();
    }

    public static void switchScene(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(LoginStageManager.class.getResource(scenes.get(name)));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            loginStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stage getLoginStage() {
        return loginStage;
    }
}
