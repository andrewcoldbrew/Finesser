package myApp;

import javafx.application.Application;
import javafx.stage.Stage;
import myApp.models.Bank;
import myApp.models.Transaction;
import myApp.models.User;
import myApp.utils.ConnectionManager;
import myApp.utils.SceneManager;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.setPrimaryStage(primaryStage);

        // Add scenes to the SceneManager
        SceneManager.addScene("login", "/views/login.fxml");
        SceneManager.addScene("signup", "/views/signup.fxml");
        SceneManager.addScene("transaction", "/views/transaction.fxml");
        SceneManager.addScene("test", "/views/test.fxml");

        // Set the initial scene
        SceneManager.switchToSceneWithoutMenuBar("login");

        primaryStage.setTitle("JavaFX Scene Manager");
        primaryStage.show();

    }

    public static void main(String[] args) {
        ConnectionManager.createConnection();
        Connection con = ConnectionManager.getConnection();
        String query = "select * from user";
        try {
            Statement statement = con.createStatement();
            statement.execute(query);
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                System.out.print(result.getString(1) + "\t");
                System.out.println();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        launch(args);
        // Testing

    }


}
