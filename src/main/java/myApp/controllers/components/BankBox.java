package myApp.controllers.components;

import animatefx.animation.GlowBackground;
import animatefx.animation.GlowText;
import animatefx.animation.JackInTheBox;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import myApp.utils.Animate;

import java.io.IOException;

public class BankBox extends BorderPane {
    public Label bankNameLabel;
    public Label fullNameLabel;
    public Label accountNumberLabel;

    public BankBox(String bankName, String userName, String accountNumber) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/bankBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(bankName, userName, accountNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(String bankName, String userName, String accountNumber) {
        bankNameLabel.setText(bankName);
        fullNameLabel.setText(userName);
        accountNumberLabel.setText(accountNumber);
    }

    public Label getBankNameLabel() {
        return bankNameLabel;
    }

    public Label getFullNameLabel() {
        return fullNameLabel;
    }

    public Label getAccountNumberLabel() {
        return accountNumberLabel;
    }
}
