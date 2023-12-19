package myApp.controllers.components;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import myApp.utils.Animate;

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
//            Animate.addHoverScalingEffect(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(String bankName, String userName, String accountNumber) {
        bankNameLabel.setText(bankName);
        userNameLabel.setText(userName);
        accountNumberLabel.setText(accountNumber);
    }

}
