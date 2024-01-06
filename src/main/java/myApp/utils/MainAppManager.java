package myApp.utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import myApp.controllers.components.ChatScreen;
import myApp.controllers.components.Chatbot;
import myApp.controllers.views.MenuBarController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainAppManager {

    private static Stage mainAppStage;
    private static BorderPane mainLayout;
    private static final Map<String, String> scenes = new HashMap<>();

    private static Chatbot chatbot;
    private static ChatScreen chatScreen;

    static {
        scenes.put("transaction", "/views/transaction.fxml");
        scenes.put("budget", "/views/budget.fxml");
        scenes.put("finance", "/views/finance.fxml");
        scenes.put("account", "/views/account.fxml");
        scenes.put("dashboard", "/views/dashboard.fxml");
        scenes.put("report", "/views/report.fxml");
    }

    private static MenuBarController menuBarController;
    public static void setupMainApp() {
        chatbot = new Chatbot();
        chatScreen = new ChatScreen();

        ChatbotManager.initializeBot();

        mainAppStage = new Stage();
        mainAppStage.setMaximized(false);
        mainLayout = new BorderPane();


        Parent leftMenuBar = loadLeftMenuBar();
        mainLayout.setLeft(leftMenuBar);


        switchScene("dashboard");
        mainAppStage.setMaximized(false);

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
            Parent menuBar = loader.load();
            menuBarController = loader.getController(); // Get the controller after loading the FXML
            return menuBar;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Stage getMainAppStage() {
        return mainAppStage;
    }


    public static void switchScene(String name) {
        setupScene(name);
        menuBarController.setActiveButtonForScene(name);
    }

    public static void addChatBot(StackPane stackPane) {
        stackPane.getChildren().add(chatbot);
        stackPane.setAlignment(Pos.BOTTOM_RIGHT);
    }
}
