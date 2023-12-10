package myApp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class MainAppManager {

    private static Stage mainAppStage = new Stage();
    private static BorderPane mainLayout = new BorderPane();
    private static final Map<String, String> scenes = Map.of(
            "transaction", "/views/transaction.fxml",
            "budget", "/views/budget.fxml",
            "finance", "/views/finance.fxml",
            "account", "/views/account.fxml",
            "testTransaction", "/views/testTransaction.fxml"
    );

    public static void setupMainApp() {
        // Set up the left menu bar
        Parent leftMenuBar = loadLeftMenuBar();
        mainLayout.setLeft(leftMenuBar);

        // Set up the content in the center
        switchScene("transaction");

        // You can add additional setup for the stage if needed

        // Show the stage
        mainAppStage.setScene(new Scene(mainLayout));
        mainAppStage.show();
    }

    private static Parent loadLeftMenuBar() {
        try {
            FXMLLoader loader = new FXMLLoader(LoginStageManager.class.getResource("/views/menuBar.fxml"));
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void switchScene(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(LoginStageManager.class.getResource(scenes.get(name)));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            mainLayout.setCenter(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stage getMainAppStage() {
        return mainAppStage;
    }

    public static BorderPane getMainLayout() {
        return mainLayout;
    }
}
