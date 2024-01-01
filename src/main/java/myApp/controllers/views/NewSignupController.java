package myApp.controllers.views;

import animatefx.animation.Shake;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.utils.DateTimeUtils;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import myApp.controllers.components.ErrorAlert;
import myApp.controllers.components.SuccessAlert;
import myApp.utils.ConnectionManager;
import myApp.utils.HashManager;
import myApp.utils.LoginStageManager;
import myApp.utils.NotificationCenter;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static io.github.palexdev.materialfx.utils.StringUtils.containsAny;
import static io.github.palexdev.materialfx.utils.StringUtils.startsWithIgnoreCase;

public class NewSignupController implements Initializable {
    public AnchorPane leftSignup;
    public AnchorPane rightSignup;
//    public VBox slidingPane;
    public MFXTextField fnameField;
    public MFXTextField lnameField;
    public MFXTextField emailField;
    public MFXDatePicker dateOfBirthPicker;
    public MFXComboBox<String> countryComboBox;
    public MFXComboBox<String> jobComboBox;
    public MFXComboBox<String> usageComboBox;
    private static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");
    private static final String[] upperChar = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
    private static final String[] lowerChar = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
    private static final String[] digits = "0 1 2 3 4 5 6 7 8 9".split(" ");
    private static final String[] specialCharacters = "! @ # & ( ) – [ { } ]: ; ' , ? / * ~ $ ^ + = < > -".split(" ");
    public MFXTextField usernameField;
    public VBox slidingPane;
    public Label passwordValidation;
    public MFXPasswordField passwordField;
    public MFXPasswordField rePasswordField;
    public MFXComboBox<String> genderComboBox;
    public Label rePasswordValidation;
    public Label emailValidation;
    public MFXButton signupButton;
    public Tooltip toolTip;
    public StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComboBox();
        slidingPane.getStyleClass().add("slidingPane");
        TranslateTransition t = new TranslateTransition(Duration.seconds(1), slidingPane);
        t.setToX(rightSignup.getLayoutX());
        t.play();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), rightSignup);
        fadeOut.setToValue(0);
        fadeOut.play();

        usernameField.setTextLimit(16);

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

        Constraint emailConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Invalid email!")
                .setCondition(Bindings.createBooleanBinding(
                        () -> isValidEmail(emailField.getText()),
                        emailField.textProperty()
                ))
                .get();

        Constraint rePasswordConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Your password doesn't match!")
                .setCondition(Bindings.createBooleanBinding(
                        () -> matchingPassword(passwordField.getText(), rePasswordField.getText()),
                        rePasswordField.textProperty()
                ))
                .get();

        passwordField.getValidator()
                .constraint(digitConstraint)
                .constraint(charactersConstraint)
                .constraint(specialCharactersConstraint)
                .constraint(lengthConstraint);

        emailField.getValidator().constraint(emailConstraint);

        rePasswordField.getValidator().constraint(rePasswordConstraint);

        passwordField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                passwordValidation.setVisible(false);
                passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });

        rePasswordField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                rePasswordValidation.setVisible(false);
                rePasswordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });

        emailField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                emailValidation.setVisible(false);
                emailField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });

        passwordField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = passwordField.validate();
                if (!constraints.isEmpty()) {
                    passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    passwordValidation.setText(constraints.get(0).getMessage());
                    passwordValidation.setVisible(true);
                    signupButton.setDisable(true);
                    new Shake(passwordField).play();
                } else {
                    signupButton.setDisable(false);
                }
            }
        });

        rePasswordField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = rePasswordField.validate();
                if (!constraints.isEmpty()) {
                    rePasswordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    rePasswordValidation.setText(constraints.get(0).getMessage());
                    rePasswordValidation.setVisible(true);
                    signupButton.setDisable(true);
                    new Shake(rePasswordField).play();
                } else {
                    signupButton.setDisable(false);
                }
            }
        });

        emailField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = emailField.validate();
                if (!constraints.isEmpty()) {
                    emailField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    emailValidation.setText(constraints.get(0).getMessage());
                    emailValidation.setVisible(true);
                    signupButton.setDisable(true);
                    new Shake(emailField).play();
                } else {
                    signupButton.setDisable(false);
                }
            }
        });

//        rePasswordField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (oldValue && !newValue) {
//                if (!matchingPassword(rePasswordField.getText(), passwordField.getText())) {
//                    rePasswordValidation.setText("Password doesn't match!");
//                    new Shake(rePasswordField);
//                    signupButton.setDisable(true);
//                } else {
//                    rePasswordValidation.setText("");
//                    signupButton.setDisable(false);
//                }
//            }
//        });
//
//        emailField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (oldValue && !newValue) {
//                if (!isValidEmail(emailField.getText())) {
//                    emailValidation.setText("Invalid email!");
//                    new Shake(emailField);
//                    signupButton.setDisable(true);
//                } else {
//                    emailValidation.setText("");
//                    signupButton.setDisable(false);
//                }
//            }
//        });

    }

    public void handleSignup(ActionEvent actionEvent) {
        String fname = fnameField.getText();
        String lname = lnameField.getText();
        String email = emailField.getText();
        LocalDate dob = dateOfBirthPicker.getValue();
        String gender = genderComboBox.getValue();
        String country = countryComboBox.getValue();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String rePassword = rePasswordField.getText();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || gender == null || dob == null || country == null) {
            NotificationCenter.errorAlert("Empty fields!", "Please fill in all fields before proceed");
            return;
        }

        if (password.equals(rePassword)) {
            try {
                if (isUsernameTaken(username)) {
                    NotificationCenter.errorAlert("Username is taken!",
                            "Someone with this username already exists. Please choose a different username");
                } else if (isEmailTaken(email)) {
                    NotificationCenter.errorAlert("Email is taken!",
                            "This email is already in used. Please choose a different email");
                } else if (isStrongPassword(password)){
                    registerUser(username, password, fname, lname, email, dob, gender, country);
                } else {
                    NotificationCenter.errorAlert("Weak password!",
                            "Your password is not strong enough");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            NotificationCenter.errorAlert("Unmatched passwords!",
                    "The re-entered password must be matching your original password");
        }
    }

    private void registerUser(String username, String password, String fname, String lname, String email, LocalDate dob, String gender, String country) {
        String hashedPassword = HashManager.hashPassword(password);
        Connection con = ConnectionManager.getConnection();
        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement("INSERT INTO user (username, password, fname, lname, email, gender, DOB, country) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setString(3, fname);
            statement.setString(4, lname);
            statement.setString(5, email);
            statement.setString(6, gender);
            statement.setDate(7, Date.valueOf(dob));
            statement.setString(8, country);
            statement.execute();
//            new SuccessAlert(stackPane, "Your account has been created!");
            LoginStageManager.switchScene("login");
            statement.close();
        } catch (SQLException e) {
            System.out.println("ACCOUNT CREATE FAILED!");
            throw new RuntimeException(e);
        }

    }

    private boolean isStrongPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }

    private boolean isUsernameTaken(String username) throws Exception {
        Connection con = ConnectionManager.getConnection();
        PreparedStatement statement = con.prepareStatement("SELECT * FROM user WHERE username = ?");
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        boolean isTaken = resultSet.next();
        resultSet.close();
        statement.close();
        return isTaken;
    }

    private boolean isEmailTaken(String email) throws Exception {
        Connection con = ConnectionManager.getConnection();
        PreparedStatement statement = con.prepareStatement("SELECT * FROM user WHERE email = ?");
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();
        boolean isTaken = resultSet.next();
        resultSet.close();
        statement.close();
        return isTaken;
    }

    private boolean matchingPassword(String password, String rePassword) {
        return password.equals(rePassword);
    }

    public void moveToLeft(ActionEvent actionEvent) {
        TranslateTransition t = new TranslateTransition(Duration.seconds(1), slidingPane);
        t.setToX(rightSignup.getLayoutX() + 20);
        t.play();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), rightSignup);
        fadeOut.setToValue(0);
        fadeOut.play();

        t.setOnFinished(e -> {
            // Fade in leftSignup
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), leftSignup);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    public void moveToRight(ActionEvent actionEvent) {
        TranslateTransition t = new TranslateTransition(Duration.seconds(1), slidingPane);
        t.setToX(leftSignup.getLayoutX());
        t.play();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), leftSignup);
        fadeOut.setToValue(0);
        fadeOut.play();

        t.setOnFinished(e -> {
            // Fade in rightSignup
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), rightSignup);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    private void initializeComboBox() {
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        countryComboBox.setItems(FXCollections.observableArrayList("Vietnam", "China", "Australia",
                "Jamaica", "India", "Canada", "United States", "Japan", "Korea"));

    }

    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    }

    public void moveToLogin(ActionEvent actionEvent) {
        LoginStageManager.switchScene("login");
    }
}
