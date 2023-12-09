package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import myApp.utils.ConnectionManager;
import myApp.utils.HashManager;
import myApp.utils.LoginStageManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ResourceBundle;

import static io.github.palexdev.materialfx.utils.StringUtils.containsAny;


public class SignupController implements Initializable {
    public Button signupButton;
    public MFXPasswordField rePasswordField;
    public Tooltip passwordAlert;
    public Hyperlink loginLink;

    @FXML
    private MFXTextField usernameField;

    @FXML
    private MFXPasswordField passwordField;

    public Label validationLabel;

    private static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");
    private static final String[] upperChar = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
    private static final String[] lowerChar = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
    private static final String[] digits = "0 1 2 3 4 5 6 7 8 9".split(" ");
    private static final String[] specialCharacters = "! @ # & ( ) – [ { } ]: ; ' , ? / * ~ $ ^ + = < > -".split(" ");

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
                    LoginStageManager.switchScene("login");
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
        System.out.println("INITIALIZE SIGNUP");
        usernameField.setTextLimit(10);

        Constraint lengthConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must be at least 8 characters long")
                .setCondition(passwordField.textProperty().length().greaterThanOrEqualTo(8))
                .get();

        Constraint digitConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must contain at least one digit")
                .setCondition(Bindings.createBooleanBinding(
                        () -> containsAny(passwordField.getText(), "", digits),
                        passwordField.textProperty()
                ))
                .get();

        Constraint charactersConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must contain at least one lowercase and one uppercase characters")
                .setCondition(Bindings.createBooleanBinding(
                        () -> containsAny(passwordField.getText(), "", upperChar) && containsAny(passwordField.getText(), "", lowerChar),
                        passwordField.textProperty()
                ))
                .get();

        Constraint specialCharactersConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must contain at least one special character")
                .setCondition(Bindings.createBooleanBinding(
                        () -> containsAny(passwordField.getText(), "", specialCharacters),
                        passwordField.textProperty()
                ))
                .get();

        passwordField.getValidator()
                .constraint(digitConstraint)
                .constraint(charactersConstraint)
                .constraint(specialCharactersConstraint)
                .constraint(lengthConstraint);

        passwordField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                validationLabel.setVisible(false);
                passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });

        passwordField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = passwordField.validate();
                if (!constraints.isEmpty()) {
                    passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    validationLabel.setText(constraints.get(0).getMessage());
                    validationLabel.setVisible(true);
                }
            }
        });
    }

    public void moveToLogin(ActionEvent actionEvent) {
        LoginStageManager.switchScene("login");
    }
}
