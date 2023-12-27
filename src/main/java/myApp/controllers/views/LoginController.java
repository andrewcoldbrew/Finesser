package myApp.controllers.views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import myApp.Main;
import myApp.controllers.components.ErrorAlert;
import myApp.controllers.components.ManualAlert;
import myApp.controllers.components.NewManualAlert;
import myApp.utils.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public Button loginButton;
    public Button signupButton;
    public StackPane stackPane;
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        passwordField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                login();
            }
        });
    }

    public void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Connection con = ConnectionManager.getConnection();
        try {
            // Update the query: use 'name' instead of 'username'
            PreparedStatement statement = con.prepareStatement("SELECT userID, username, password FROM user WHERE username = ?");
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("userID");
                String hashedStoredPassword = resultSet.getString("password");

                if (HashManager.validatePassword(password, hashedStoredPassword)) {
                    System.out.println("ACCOUNT FOUND");
                    Main.setUserId(userId);
                    System.out.println(Main.getUserId());
                    LoginStageManager.getLoginStage().close();
                    MainAppManager.setupMainApp();
                } else {

                    new ErrorAlert(stackPane, "Incorrect password", "The password you entered is incorrect! Please try again");
                    System.out.println("INCORRECT PASSWORD");
                }
            } else {
                new ErrorAlert(stackPane,"User not found", "This username doesn't exists! Please enter another one");
                System.out.println("USER NOT FOUND");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void moveToSignup() {
        LoginStageManager.switchScene("signup");
    }
}
