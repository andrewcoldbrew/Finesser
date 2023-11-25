package myApp.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import myApp.utils.ConnectionManager;
import myApp.utils.HashManager;
import myApp.utils.SceneManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.crypto.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.ResourceBundle;


public class SignupController implements Initializable {
    public Button signupButton;
    public PasswordField rePasswordField;
    public Tooltip passwordAlert;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void signup(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String rePassword = rePasswordField.getText();

        if (password.equals(rePassword)) {
            Connection con = ConnectionManager.getConnection();

            try {
                if (isUsernameTaken(con, username)) {
                    System.out.println("Username already exists. Choose a different username.");
                } else if (isStrongPassword(password)){
                    registerUser(con, username, password);
                    System.out.println("User successfully registered.");
                    SceneManager.switchToSceneWithoutMenuBar("login");
                } else {
                    System.out.println("Password not strong enough");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("PASSWORD DOESN'T MATCH");
        }
    }

    private boolean isUsernameTaken(Connection con, String username) throws Exception {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM user WHERE username = ?");
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    private boolean isStrongPassword(String password) {
        // Define the regular expression for a strong password
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }

    private void registerUser(Connection con, String username, String password) throws Exception {
        // Hash the password before storing it
        String hashedPassword = HashManager.hashPassword(password);
        // Insert the new user
        PreparedStatement statement = con.prepareStatement("INSERT INTO user (username, password) VALUES (?, ?)");
        statement.setString(1, username);
        statement.setString(2, hashedPassword);
        statement.execute();
        statement.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
