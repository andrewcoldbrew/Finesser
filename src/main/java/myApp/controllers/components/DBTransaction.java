package myApp.controllers.components;

import io.github.palexdev.materialfx.beans.NumberRange;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;

public class DBTransaction extends VBox {
    public Label nameLabel;
    public Label amountLabel;

    public DBTransaction(String name, double amount) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/dbTransaction.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(name, amount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(String name, double amount) {
        nameLabel.setText(name);
        amountLabel.setText(String.format("Amount: %.2f", amount));
    }
}
