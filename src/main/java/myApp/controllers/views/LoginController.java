package myApp.controllers.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import myApp.utils.ConnectionManager;
import myApp.utils.HashManager;
import myApp.utils.SceneManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    public Button loginButton;
    public Button signupButton;
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void login(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Retrieve user data from the database
        Connection con = ConnectionManager.getConnection();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT username, password FROM user WHERE username = ?");
            statement.setString(1, username);
            System.out.println(statement);
            ResultSet resultSet = statement.executeQuery();
            // If username exists!
            if (resultSet.next()) {
                String hashedStoredPassword = resultSet.getString("password");

                // Check password correct or not
                if (HashManager.validatePassword(password, hashedStoredPassword)) {
                    System.out.println("ACCOUNT FOUND");
                    SceneManager.switchToSceneWithMenuBar("transaction");
                } else {
                    System.out.println("WRONG PASSWORD DUMBASS");
                }
            } else {
                System.out.println("NO ACCOUNT FOUND");
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveToSignup(ActionEvent actionEvent) {
        SceneManager.switchToSceneWithoutMenuBar("signup");
    }
}
