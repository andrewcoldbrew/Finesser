package myApp.controllers.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class BankBox extends BorderPane {
    public Label bankNameLabel;
    public Label userNameLabel;
    public BankBox(String bankName, String userName) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/bankBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(bankName, userName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(String bankName, String userName) {
        bankNameLabel.setText(bankName);
        userNameLabel.setText(userName);
    }
}
