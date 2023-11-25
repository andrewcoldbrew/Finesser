package myApp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static final Map<String, Scene> scenes = new HashMap<>();
    private static final FXMLLoader leftMenuBarLoader = new FXMLLoader(SceneManager.class.getResource("/views/menuBar.fxml"));
    private static Parent leftMenuBar;

    private static Stage primaryStage = null;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        primaryStage.setResizable(true);
    }

    public static void addScene(String name, String fxmlURL) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlURL));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scenes.put(name, scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Switch to a scene without a left menu bar (e.g., login or signup)
    public static void switchToSceneWithoutMenuBar(String name) {
        Scene scene = scenes.get(name);
        primaryStage.setScene(scene);
    }

    // Switch to a scene with a left menu bar (e.g., main app)
    public static void switchToSceneWithMenuBar(String name) {
        try {
            if (leftMenuBar == null) {
                // Load the left menu bar only if it's not already loaded
                leftMenuBar = leftMenuBarLoader.load();
            }

            BorderPane mainLayout = new BorderPane();

            // Set the left menu bar
            mainLayout.setLeft(leftMenuBar);

            // Load the main content
            Parent content = scenes.get(name).getRoot();
            mainLayout.setCenter(content);

            // Create a new scene with the main layout
            Scene scene = new Scene(mainLayout);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
