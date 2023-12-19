package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FinanceBox extends HBox {

    public Label nameLabel;
    public Label categoryLabel;
    public Label amountLabel;
    public Label dateLabel;
    public MFXButton updateButton;
    public MFXButton deleteButton;

    public FinanceBox(String name, double amount, String category, LocalDate transactionDate) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/financeBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(name, amount, category, transactionDate);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file", e);
        }
    }
    public double getAmount() {
        return Double.parseDouble(amountLabel.getText().replace("Amount: $", ""));
    }


    private void initialize(String name, double amount, String category, LocalDate transactionDate) {
        nameLabel.setText(name);
        amountLabel.setText(String.format("Amount: $%.2f", amount));
        categoryLabel.setText(category);
        dateLabel.setText(String.format("Date: %s", transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));


        // this.getStyleClass().add("finance-box-" + category.toLowerCase());
    }
}
