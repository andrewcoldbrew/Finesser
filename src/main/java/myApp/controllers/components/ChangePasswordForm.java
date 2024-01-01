package myApp.controllers.components;

import animatefx.animation.Shake;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import myApp.Main;

import myApp.utils.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static io.github.palexdev.materialfx.utils.StringUtils.containsAny;

public class ChangePasswordForm extends BorderPane {
    public MFXPasswordField oldPasswordField;
    public MFXPasswordField newPasswordField;
    public MFXPasswordField reNewPasswordField;
    public Label newPWValidation;
    public Label reNewPWValidation;
    public MFXButton changeButton;
    public Button closeButton;
    private static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");
    private static final String[] upperChar = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
    private static final String[] lowerChar = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
    private static final String[] digits = "0 1 2 3 4 5 6 7 8 9".split(" ");
    private static final String[] specialCharacters = "! @ # & ( ) – [ { } ]: ; ' , ? / * ~ $ ^ + = < > -".split(" ");

    public ChangePasswordForm() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/changePasswordForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            initialize();
            new Draggable().makeDraggable(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize() {
        System.out.println("Initialize password change.");
        closeButton.setOnAction(this::close);
        changeButton.setOnAction(this::changePassword);

        Constraint lengthConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must be at least 8 characters long")
                .setCondition(newPasswordField.textProperty().length().greaterThanOrEqualTo(8))
                .get();

        Constraint digitConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must contain at least one digit")
                .setCondition(Bindings.createBooleanBinding(
                        () -> containsAny(newPasswordField.getText(), "", digits),
                        newPasswordField.textProperty()
                ))
                .get();

        Constraint charactersConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must contain at least one lowercase and one uppercase characters")
                .setCondition(Bindings.createBooleanBinding(
                        () -> containsAny(newPasswordField.getText(), "", upperChar) && containsAny(newPasswordField.getText(), "", lowerChar),
                        newPasswordField.textProperty()
                ))
                .get();

        Constraint specialCharactersConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must contain at least one special character")
                .setCondition(Bindings.createBooleanBinding(
                        () -> containsAny(newPasswordField.getText(), "", specialCharacters),
                        newPasswordField.textProperty()
                ))
                .get();

        Constraint matchingOldPWConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("New password must be different from old one")
                .setCondition(Bindings.createBooleanBinding(
                        () -> !newPasswordField.getText().trim().equals(oldPasswordField.getText().trim()),
                        newPasswordField.textProperty()
                ))
                .get();

        Constraint rePasswordConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Your password doesn't match!")
                .setCondition(Bindings.createBooleanBinding(
                        () -> matchingPassword(newPasswordField.getText(), reNewPasswordField.getText()),
                        reNewPasswordField.textProperty()
                ))
                .get();

        newPasswordField.getValidator()
                .constraint(digitConstraint)
                .constraint(charactersConstraint)
                .constraint(specialCharactersConstraint)
                .constraint(lengthConstraint)
                .constraint(matchingOldPWConstraint);

        reNewPasswordField.getValidator().constraint(rePasswordConstraint);

        newPasswordField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                newPWValidation.setVisible(false);
                newPasswordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });

        reNewPasswordField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                reNewPWValidation.setVisible(false);
                reNewPasswordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });

        newPasswordField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = newPasswordField.validate();
                if (!constraints.isEmpty()) {
                    newPasswordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    newPWValidation.setText(constraints.get(0).getMessage());
                    newPWValidation.setVisible(true);
                    changeButton.setDisable(true);
                    new Shake(newPasswordField).play();
                } else {
                    changeButton.setDisable(false);
                }
            }
        });

        reNewPasswordField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = reNewPasswordField.validate();
                if (!constraints.isEmpty()) {
                    reNewPasswordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    reNewPWValidation.setText(constraints.get(0).getMessage());
                    reNewPWValidation   .setVisible(true);
                    changeButton.setDisable(true);
                    new Shake(reNewPasswordField).play();
                } else {
                    changeButton.setDisable(false);
                }
            }
        });
    }

    private boolean matchingPassword(String password, String rePassword) {
        return password.equals(rePassword);
    }


    private void close(ActionEvent actionEvent) {
        ((Pane) getParent()).getChildren().remove(this);
    }

    private void changePassword(ActionEvent actionEvent) {
        String oldPassword = oldPasswordField.getText();
        String newPW = newPasswordField.getText();
        String reNewPW = reNewPasswordField.getText();

        if (HashManager.validatePassword(oldPassword, getHashedPWFromDatabase())) {
            if (newPW.equals(reNewPW)) {
                try {
                    if (isStrongPassword(newPW)){
                        changePasswordInDatabase(newPW);
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


    }

    public String getHashedPWFromDatabase() {
        Connection con = ConnectionManager.getConnection();
        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement("SELECT password from user WHERE userID = ?");
            statement.setInt(1, Main.getUserId());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";

    }

    private boolean isStrongPassword(String password) {

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }

    private void changePasswordInDatabase(String password) throws Exception {
        String hashedPassword = HashManager.hashPassword(password);
        Connection con = ConnectionManager.getConnection();
        PreparedStatement statement = con.prepareStatement("UPDATE user SET password = ? WHERE userID = ?");
        statement.setString(1, hashedPassword);
        statement.setInt(2, Main.getUserId());
        statement.execute();
        statement.close();
        NotificationCenter.successAlert("Password changed!", "Your password has been changed. Please login again");
    }



}
