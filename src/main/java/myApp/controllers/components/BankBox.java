package myApp.controllers.components;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.io.IOException;

public class BankBox extends BorderPane {
    public Label bankNameLabel;
    public Label userNameLabel;
    public Label accountNumberLabel;

    public BankBox(String bankName, String userName, String accountNumber) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/bankBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(bankName, userName, accountNumber);
            addHoverScalingEffect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(String bankName, String userName, String accountNumber) {
        bankNameLabel.setText(bankName);
        userNameLabel.setText(userName);
        accountNumberLabel.setText(accountNumber);
    }

    private void addHoverScalingEffect() {
        ScaleTransition scaleInTransition = createScaleTransition(1.0, 1.02);
        ScaleTransition scaleOutTransition = createScaleTransition(1.02, 1.0);

        setOnMouseEntered(event -> {
            scaleInTransition.play();
            setCursor(Cursor.HAND);
        });

        setOnMouseExited(event -> {
            scaleOutTransition.play();
            setCursor(Cursor.DEFAULT);
        });
    }

    private ScaleTransition createScaleTransition(double fromValue, double toValue) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), this);
        scaleTransition.setFromX(fromValue);
        scaleTransition.setFromY(fromValue);
        scaleTransition.setToX(toValue);
        scaleTransition.setToY(toValue);
        return scaleTransition;
    }
}
