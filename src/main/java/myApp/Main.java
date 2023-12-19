package myApp;

import javafx.application.Application;
import javafx.stage.Stage;
import myApp.utils.ConnectionManager;
import myApp.utils.LoginStageManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends Application {

    private static int userId;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the initial scene
        LoginStageManager.setupLoginStage();

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

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int newUserId) {
        userId = newUserId;
    }
}
