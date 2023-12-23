package myApp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainAppManager {

    private static Stage mainAppStage = new Stage();
    private static BorderPane mainLayout;
    private static final Map<String, String> scenes = new HashMap<>();

    static {
        scenes.put("transaction", "/views/transaction.fxml");
        scenes.put("budget", "/views/budget.fxml");
        scenes.put("finance", "/views/finance.fxml");
        scenes.put("account", "/views/account.fxml");
        scenes.put("dashboard", "/views/dashboard.fxml");
//        scenes.put("testTransaction", "/views/testTransaction.fxml");
    }

    public static void setupMainApp() {
        mainAppStage.setMaximized(false);


        mainLayout = new BorderPane();


        Parent leftMenuBar = loadLeftMenuBar();
        mainLayout.setLeft(leftMenuBar);


        setupScene("account");
        mainAppStage.setMaximized(true);

        Scene scene = new Scene(mainLayout);
        mainAppStage.setScene(scene);
        mainAppStage.show();
    }

    private static void setupScene(String name) {
        try {

            FXMLLoader loader = new FXMLLoader(MainAppManager.class.getResource(scenes.get(name)));
            Parent content = loader.load();
            mainLayout.setCenter(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Parent loadLeftMenuBar() {
        try {
            FXMLLoader loader = new FXMLLoader(MainAppManager.class.getResource("/views/menuBar.fxml"));
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stage getMainAppStage() {
        return mainAppStage;
    }


    public static void switchScene(String name) {
        setupScene(name);
    }
}
