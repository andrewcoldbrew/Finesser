package myApp.controllers.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import myApp.Main;
import myApp.controllers.components.ErrorAlert;
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
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void login(ActionEvent actionEvent) {
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
                    new ErrorAlert("WRONG PASSWORD", "The password you entered is incorrect! Please try again");
                    System.out.println("INCORRECT PASSWORD");
                }
            } else {
                new ErrorAlert("ACCOUNT NOT FOUND", "This username doesn't exists! Please enter another one");
                System.out.println("USER NOT FOUND");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void moveToSignup(ActionEvent actionEvent) {
        LoginStageManager.switchScene("signup");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("INITIALIZE LOGIN");
    }
}
